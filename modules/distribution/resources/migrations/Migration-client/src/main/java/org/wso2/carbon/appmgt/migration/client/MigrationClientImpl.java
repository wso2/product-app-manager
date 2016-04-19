/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.api.model.WebApp;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.AppManagerConfiguration;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.appmgt.migration.util.Constants;
import org.wso2.carbon.appmgt.migration.util.MigratingWebapp;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.ActionConstants;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.wso2.carbon.utils.FileUtil;

import java.text.ParseException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * This class contains all the methods which is used to migrate Webapps from App Manager 1.0.0 to App Manager 1.1.0.
 * The migration performs in database, registry and file system
 */

public class MigrationClientImpl implements MigrationClient {

    private static final Log log = LogFactory.getLog(MigrationClientImpl.class);
    private List<Tenant> tenantsArray;
    private MigrationDBCreator migrationDBCreator;

    public MigrationClientImpl(String tenantArguments) throws UserStoreException, AppManagementException {
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        AppManagerConfiguration config = ServiceHolder.getAppManagerConfigurationService().getAPIManagerConfiguration();
        String dataSourceName = config.getFirstProperty(Constants.DATA_SOURCE_NAME);
        migrationDBCreator = new MigrationDBCreator(initializeDataSource(dataSourceName));

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
     * @throws org.wso2.carbon.appmgt.api.AppManagementException
     * @throws java.sql.SQLException
     */
    @Override
    public void databaseMigration() throws SQLException {
        try {
            migrationDBCreator.createDatabase();
        } catch (APPMMigrationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialize received data source
     *
     * @param dataSourceName : Data source name needs to be initialized
     * @return DataSource
     * @throws org.wso2.carbon.appmgt.api.AppManagementException if an error occurs while initializing the data source
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
        log.info("Rxt migration for App Manager 1.1.0 started.");
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
                    HashMap<String, MigratingWebapp> defaultWebappMap = new HashMap<String, MigratingWebapp>();
                    HashMap<String, MigratingWebapp> oldWebappMap = new HashMap<String, MigratingWebapp>();
                    updateWebappMaps(artifacts, registry, defaultWebappMap, oldWebappMap);
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
                        migrateArtifactLifecycleHistory(registry, apiIdentifier);
                        artifact.removeAttribute("overview_makeAsDefaultVersion");
                        if (defaultWebappMap.get(apiIdentifier.getApiName()).getAppVersion().equals(artifact.getAttribute("overview_version"))) {
                            artifact.addAttribute("overview_makeAsDefaultVersion", "true");
                        } else {
                            artifact.addAttribute("overview_makeAsDefaultVersion", "false");
                        }

                        artifact.removeAttribute("overview_oldVersion");
                        if (!apiIdentifier.getVersion().equals(oldWebappMap.get(apiIdentifier.getApiName()).getAppVersion())) {
                            artifact.addAttribute("overview_oldVersion", oldWebappMap.get(apiIdentifier.getApiName()).getAppVersion());
                        }
                        artifact.removeAttribute("overview_treatAsSite");
                        artifact.addAttribute("overview_treatAsSite", "false");
                        artifactManager.updateGenericArtifact(artifact);
                        String path = artifact.getPath();
                        Resource resource = registry.get(artifact.getPath());
                        Properties properties = resource.getProperties();

                        Iterator<Object> keySetItr = properties.keySet().iterator();
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        while (keySetItr.hasNext()) {
                            Object key = keySetItr.next();
                            stringArrayList.add(key.toString());

                        }
                        for (String s : stringArrayList) {
                            resource.removeProperty(s);
                        }

                        registry.put(path, resource);
                        Resource resource1 = registry.get(path);
                    }
                    //  migrationDBCreator.updateAppDefaultVersions(defaultWebappMap, "-1234");

                } else {
                    log.info("No webapp artifacts found in registry for tenant " + tenant.getDomain());
                }
            } catch (AppManagementException e) {
                handleException("Error occurred while reading API from the artifact ", e);
            } catch (RegistryException e) {
                handleException("Error occurred while accessing the registry", e);
            } catch (UserStoreException e) {
                handleException("Error occurred while reading tenant information", e);
            } finally {
                if (isTenantFlowStarted) {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            }
            log.info("End rxtMigration for tenant " + tenant.getId() + "(" + tenant.getDomain() + ")");
        }

        log.info("Rxt resource migration done for all the tenants");
    }

    public void migrateArtifactLifecycleHistory(Registry registry, APIIdentifier apiIdentifier) {
        String appLcHistoryPath = Constants.REGISTRY_ARTIFACT_LIFECYCLE_HISTORY_OLD + "provider_" +
                apiIdentifier.getProviderName() + "_" + apiIdentifier.getApiName() + "_" + apiIdentifier.getVersion() + "_webapp";
        try {
            Resource resource = registry.get(appLcHistoryPath);
            JSONObject lcHistoryContent = XML.toJSONObject(new String((byte[]) resource.getContent()));
            JSONObject lcHistoryElements = lcHistoryContent.getJSONObject("lifecycleHistory").getJSONObject("item");

        } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
            log.error("Error occurred while retrieving lifecycle history registry resource : " + appLcHistoryPath, e);
        } catch (JSONException e) {
            log.error("Error occurred while parsing lifecycle history resource content", e);
        }
    }


    /**
     * This method is used to migrate rxt and rxt data
     * This adds three new attributes to the api rxt
     *
     * @throws org.wso2.carbon.appmgt.migration.APPMMigrationException
     */
    private void rxtMigration() throws APPMMigrationException {
        log.info("Rxt migration for API Manager started.");

        String rxtName = "webapp.rxt";
        String rxtDir = CarbonUtils.getCarbonHome() + File.separator + "migration-scripts" + File.separator +
                "19-110-migration" + File.separator + "rxts" + File.separator + rxtName;


        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);

                log.info("Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                //Update api.rxt file
                String rxt = FileUtil.readFileToString(rxtDir);
                registryService.updateRXTResource(rxtName, rxt);
                log.info("End Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');

                log.info("Start rxt data migration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                GenericArtifact[] artifacts = registryService.getGenericAPIArtifacts();
                for (GenericArtifact artifact : artifacts) {
                    artifact.setAttribute("overview_endpointAuthDigest", "false");
                }
                registryService.updateGenericAPIArtifacts(artifacts);
                log.info("End rxt data migration for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');

            } catch (GovernanceException e) {
                log.error("Error when accessing API artifact in registry for tenant "+ tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (IOException e) {
                log.error("Error when reading api.rxt from " + rxtDir + "for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                log.error("Error while updating api.rxt in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (UserStoreException e) {
                log.error("Error while updating api.rxt in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            }
            finally {
                registryService.endTenantFlow();
            }
        }

        log.info("Rxt resource migration done for all the tenants");
    }

    public void updateWebappMaps(GenericArtifact[] artifacts, Registry registry,
                                 HashMap<String, MigratingWebapp> defaultWebappMap, HashMap<String, MigratingWebapp> oldWebappMap) {

        try {
            for (GenericArtifact artifact : artifacts) {
                if (AppManagerUtil.getAPI(artifact) == null) {
                    continue;
                }
                String apiProvider = artifact.getAttribute("overview_provider");
                String appName = artifact.getAttribute("overview_name");
                String version = artifact.getAttribute("overview_version");
                String lcState = artifact.getLifecycleState();
                String createdTimeStamp = artifact.getAttribute("overview_createdtime");
                createdTimeStamp.replaceFirst("^0+(?!$)", "");
                long epochTimeStamp = Long.parseLong(createdTimeStamp);

                MigratingWebapp newWebapp = new MigratingWebapp();
                newWebapp.setAppName(appName);
                newWebapp.setAppProvider(apiProvider);
                newWebapp.setAppVersion(version);
                newWebapp.setLcState(lcState);
                newWebapp.setCreatedTimeStamp(new Date(epochTimeStamp));

                if (defaultWebappMap.containsKey(appName)) {
                    MigratingWebapp currentDefaultWebapp = defaultWebappMap.get(appName);
                    if ((currentDefaultWebapp.isPublished() && newWebapp.isPublished()
                            && newWebapp.getCreatedTimeStamp().after(currentDefaultWebapp.getCreatedTimeStamp()) ||
                            (newWebapp.isPublished() && !currentDefaultWebapp.isPublished()) ||
                            (!currentDefaultWebapp.isPublished() && !newWebapp.isPublished()
                                    && newWebapp.getCreatedTimeStamp().after(currentDefaultWebapp.getCreatedTimeStamp())))) {
                        defaultWebappMap.put(appName, newWebapp);

                    }
                } else {
                    defaultWebappMap.put(appName, newWebapp);
                }

                //Update old version Map
                if (oldWebappMap.containsKey(appName)) {
                    MigratingWebapp currentOld = oldWebappMap.get(appName);
                    if (newWebapp.getCreatedTimeStamp().before(currentOld.getCreatedTimeStamp())) {
                        oldWebappMap.put(appName, newWebapp);
                    }
                } else {
                    oldWebappMap.put(appName, newWebapp);
                }

            }
        } catch (GovernanceException e) {
            log.error("Error occurred while reading artifact attribute values", e);
        } catch (AppManagementException e) {
            log.error("Error occurred while retrieving webapp", e);
        }
    }


    private static void handleException(String msg, Throwable t) throws APPMMigrationException {
        log.error(msg, t);
        throw new APPMMigrationException(msg, t);
    }

}
