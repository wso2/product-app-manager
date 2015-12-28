/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.appmgt.migration.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.api.model.WebApp;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.AppManagerConfiguration;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.appmgt.migration.util.Constants;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.ActionConstants;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the methods which is used to migrate Webapps from App Manager 1.0.0 to App Manager 1.1.0.
 * The migration performs in database, registry and file system
 */

public class MigrationClientImpl implements MigrationClient {

    private static final Log log = LogFactory.getLog(MigrationClientImpl.class);
    private List<Tenant> tenantsArray;

    public MigrationClientImpl(String tenantArguments) throws UserStoreException {
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();

        if (tenantArguments != null) {  // Tenant arguments have been provided so need to load specific ones
            tenantArguments = tenantArguments.replaceAll("\\s", ""); // Remove spaces and tabs

            tenantsArray = new ArrayList();

            if (tenantArguments.contains(",")) { // Multiple arguments specified
                String[] parts = tenantArguments.split(",");

                for (int i = 0; i < parts.length; ++i) {
                    if (parts[i].length() > 0) {
                        populateTenants(tenantManager, tenantsArray, parts[i]);
                    }
                }
            } else { // Only single argument provided
                populateTenants(tenantManager, tenantsArray, tenantArguments);
            }
        } else {  // Load all tenants
            tenantsArray = new ArrayList(Arrays.asList(tenantManager.getAllTenants()));
            Tenant superTenant = new Tenant();
            superTenant.setDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            superTenant.setId(MultitenantConstants.SUPER_TENANT_ID);
            tenantsArray.add(superTenant);
        }
    }

    private void populateTenants(TenantManager tenantManager, List<Tenant> tenantList, String argument) throws UserStoreException {
        log.debug("Argument provided : " + argument);

        if (argument.contains("@")) { // Username provided as argument
            int tenantID = tenantManager.getTenantId(argument);

            if (tenantID != -1) {
                tenantList.add(tenantManager.getTenant(tenantID));
            } else {
                log.error("Tenant does not exist for username " + argument);
            }
        } else { // Domain name provided as argument
            Tenant[] tenants = tenantManager.getAllTenantsForTenantDomainStr(argument);

            if (tenants.length > 0) {
                tenantList.addAll(Arrays.asList(tenants));
            } else {
                log.error("Tenant does not exist for domain " + argument);
            }
        }
    }
    /**
     * This method is used to migrate database tables
     * This executes the database queries according to the user's db type and alters the tables
     *
     * @param migrateVersion version to be migrated
     * @throws org.wso2.carbon.appmgt.api.AppManagementException
     * @throws java.sql.SQLException
     */
    @Override
    public void databaseMigration(String migrateVersion) throws SQLException {
        AppManagerConfiguration config = ServiceHolder.getAppManagerConfigurationService().getAPIManagerConfiguration();
        String dataSourceName = config
                .getFirstProperty(Constants.DATA_SOURCE_NAME);
        try {
            MigrationDBCreator migrationDBCreator = new MigrationDBCreator(initializeDataSource(dataSourceName));
            migrationDBCreator.createDatabase();
        } catch (AppManagementException e) {
            e.printStackTrace();
        } catch (APPMMigrationException e) {
            e.printStackTrace();
        }

    }
    /**
     * Initialize received data source
     *
     * @param dataSourceName
     *            : Data source name needs to be initialized
     * @return DataSource
     * @throws org.wso2.carbon.appmgt.api.AppManagementException
     *             if an error occurs while initializing the data source
     */
    public static DataSource initializeDataSource(String dataSourceName)
            throws AppManagementException {
        DataSource ds = null;
        if (dataSourceName != null) {
            try {
                Context ctx = new InitialContext();
                ds = (DataSource) ctx.lookup(dataSourceName);
            } catch (NamingException e) {
                log.error(e.getMessage());
                throw new AppManagementException(
                        "Error while looking up the data " + "source: "
                                + dataSourceName);
            }
        }
        return ds;
    }

    /**
     * This method is used to migrate all registry resources
     * This migrates webapp rxts
     *
     * @throws org.wso2.carbon.appmgt.migration.APPMMigrationException
     */
    @Override
    public void registryResourceMigration() throws APPMMigrationException {
        log.info("Rxt migration for App Manager 1.0.0 started.");
        boolean isTenantFlowStarted = false;
        for (Tenant tenant : tenantsArray) {
            if (log.isDebugEnabled()) {
                log.debug("Start rxtMigration for tenant " + tenant.getId() + "(" + tenant.getDomain() + ")");
            }
            try {
                PrivilegedCarbonContext.startTenantFlow();
                isTenantFlowStarted = true;

                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenant.getDomain(), true);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenant.getId(), true);

                String adminName = ServiceHolder.getRealmService().getTenantUserRealm(tenant.getId())
                        .getRealmConfiguration().getAdminUserName();

                ServiceHolder.getTenantRegLoader().loadTenantRegistry(tenant.getId());
                Registry registry = ServiceHolder.getRegistryService().getGovernanceUserRegistry(adminName, tenant
                        .getId());
                GenericArtifactManager artifactManager = AppManagerUtil.getArtifactManager(registry, AppMConstants.API_KEY);

                if (artifactManager != null) {
                    GovernanceUtils.loadGovernanceArtifacts((UserRegistry) registry);
                    GenericArtifact[] artifacts = artifactManager.getAllGenericArtifacts();
                    for (GenericArtifact artifact : artifacts) {
                        WebApp webapp = AppManagerUtil.getAPI(artifact);

                        if (webapp == null) {
                            log.error("Cannot find corresponding web application for registry artifact " +
                                    artifact.getAttribute("overview_name") + "-"
                                    + artifact.getAttribute("overview_version") + "-" + artifact.getAttribute("overview_provider") +
                                    " of tenant " + tenant.getId() + "(" + tenant.getDomain() + ") in AM_DB");
                            continue;
                        }

                        APIIdentifier apiIdentifier = webapp.getId();
                        StringBuilder visibleRoles = new StringBuilder();
                        String roleRestrictionsPath = artifact.getPath()+Constants.ROLE_RESTRICTIONS;
                        if(registry.resourceExists(roleRestrictionsPath)){
                            Resource resource = registry.get(roleRestrictionsPath);
                            Object content = resource.getContent();
                            if (content != null) {
                                JSONArray permissionArray = (JSONArray) new JSONParser().parse(new String((byte[]) resource.getContent()));
                                for(int i = 0;i<permissionArray.size();i++){
                                    Map permissionElement = (Map) permissionArray.get(i);
                                    visibleRoles.append( permissionElement.get("role"));
                                    visibleRoles.append(",");

                                }
                            }
                        }
                        String allowedRoles = visibleRoles.length() > 0 ? visibleRoles.substring(0, visibleRoles.length() - 1) : "";
                        boolean isToUpdate = false;

                        if (artifact.getAttribute("overview_advertiseOnly") == null) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_advertiseOnly", "false");
                        }
                        if (artifact.getAttribute("overview_appOwner") == null) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_appOwner", apiIdentifier.getProviderName());
                        }
                        if (artifact.getAttribute("overview_appTenant") == null) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_appTenant", tenant.getDomain());
                        }
                        if (artifact.getAttribute("overview_subscriptionAvailability") == null && ("".equals(allowedRoles))) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_subscriptionAvailability", Constants.CURRENT_TENANT);
                        }
                        if (artifact.getAttribute("overview_tenants") == null) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_tenants", tenant.getDomain());
                        }
                        if (artifact.getAttribute("overview_visibleRoles") == null) {
                            isToUpdate = true;
                            artifact.setAttribute("overview_visibleRoles", allowedRoles);
                        }
                        if (isToUpdate) {
                            artifactManager.updateGenericArtifact(artifact);
                        }
                    }
                } else {
                    log.info("No webapp artifacts found in registry for tenant " + tenant.getDomain());
                }
            } catch (AppManagementException e) {
                handleException("Error occurred while reading API from the artifact ", e);
            } catch (RegistryException e) {
               handleException("Error occurred while accessing the registry", e);
            } catch (UserStoreException e) {
                handleException("Error occurred while reading tenant information", e);
            } catch (ParseException e) {
                handleException("Error while parsing resource permission content", e);
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            log.info("End rxtMigration for tenant " + tenant.getId() + "(" + tenant.getDomain() + ")");
        }

        log.info("Rxt resource migration done for all the tenants");
    }

    /**
     * Retrieve allowedRoleSet
     *
     * @param tenantId
     * @param sourceResourcePath
     * @return
     */
    public String getAllowedRoles(int tenantId, String sourceResourcePath) {

        StringBuilder visibleRoles = new StringBuilder();
        try {
            AuthorizationManager authManager =
                    ServiceHolder
                            .getRealmService()
                            .getTenantUserRealm(tenantId)
                            .getAuthorizationManager();

            String[] allAllowedRoles =
                    authManager.getAllowedRolesForResource(sourceResourcePath,
                            ActionConstants.GET);
            for (int i = 0; i < allAllowedRoles.length; i++) {
                String currentRole = allAllowedRoles[i];
                if (!("admin".equals(currentRole)) && !("Internal/everyone".equals(currentRole))) {
                    visibleRoles.append(currentRole);
                    visibleRoles.append(",");
                }
            }
        } catch (UserStoreException e) {
            log.error("Error occurred while retrieving allowed role set of the resource path : " +
                    sourceResourcePath + " for tenant id : " + tenantId);
        }
        return visibleRoles.length() > 0 ? visibleRoles.substring(0, visibleRoles.length() - 1) : "";

    }
    private static void handleException(String msg, Throwable t) throws APPMMigrationException {
        log.error(msg, t);
        throw new APPMMigrationException(msg, t);
    }

}
