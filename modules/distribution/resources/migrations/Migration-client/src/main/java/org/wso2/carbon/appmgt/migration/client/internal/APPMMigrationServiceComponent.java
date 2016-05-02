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

package org.wso2.carbon.appmgt.migration.client.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.appmgt.impl.AppManagerConfigurationService;
import org.wso2.carbon.appmgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.MigrationClient;
import org.wso2.carbon.appmgt.migration.client.MigrationClientImpl;
import org.wso2.carbon.appmgt.migration.util.Constants;
import org.wso2.carbon.appmgt.migration.util.RegistryServiceImpl;
import org.wso2.carbon.core.services.callback.LoginSubscriptionManagerService;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.rest.api.service.RestApiAdminService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;

import java.sql.SQLException;

/**
 * @scr.component name="org.wso2.carbon.appmgt.migration.client" immediate="true"
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService" cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="registry.service"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="registry.core.dscomponent"
 * interface="org.wso2.carbon.registry.core.service.RegistryService" cardinality="1..1"
 * policy="dynamic" bind="setRegistryService" unbind="unsetRegistryService"
 * @scr.reference name="tenant.registryloader" interface="org.wso2.carbon.registry.core.service.TenantRegistryLoader" cardinality="1..1"
 * policy="dynamic" bind="setTenantRegistryLoader" unbind="unsetTenantRegistryLoader"
 * @scr.reference name="appm.configuration" interface="org.wso2.carbon.appmgt.impl.AppManagerConfigurationService" cardinality="1..1"
 * policy="dynamic" bind="setApiManagerConfig" unbind="unsetApiManagerConfig"
 */

public class APPMMigrationServiceComponent {

    private static final Log log = LogFactory.getLog(APPMMigrationServiceComponent.class);

    /**
     * Method to activate bundle.
     *
     * @param context OSGi component context.
     */
    protected void activate(ComponentContext context) {

        String migrateToVersion = System.getProperty(Constants.ARG_MIGRATE_TO_VERSION);
        TenantManager tenantManager = ServiceHolder.getRealmService().getTenantManager();
        String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
        boolean migrateAll = Boolean.parseBoolean(System.getProperty(Constants.ARG_MIGRATE));
        String migrateProfile = System.getProperty(Constants.ARG_MIGRATE_PROFILE);

        try {
            APIMgtDBUtil.initialize();
        } catch (Exception e) { //APIMgtDBUtil initialize method throws a generic exception.
            log.error("Error occurred while initializing App Manager database utils.", e);
        }

        try {
            if (migrateToVersion != null) {
                if (Constants.VERSION_1_2_0.equalsIgnoreCase(migrateToVersion)) {

                    log.info("Starting WSO2 App Manager 1.1.0 to 1.2.0 migration.");

                    RegistryServiceImpl registryService = new RegistryServiceImpl();
                    MigrationClient migrationClient = new MigrationClientImpl(tenants, registryService, tenantManager);

                    if (migrateAll) {
                        migrationClient.databaseMigration();
                        migrationClient.registryResourceMigration();
                        migrationClient.synapseFileSystemMigration();
                    } else {
                        if (migrateProfile != null) {
                            if (Constants.PUBLISHER_MIGRATE_PROFILE.equals(migrateProfile)) {
                                if (log.isDebugEnabled()) {
                                    log.debug(Constants.PUBLISHER_MIGRATE_PROFILE + " profile migration has started");
                                }
                                migrationClient.databaseMigration();
                                migrationClient.registryResourceMigration();
                            } else if (Constants.GATEWAY_MIGRATE_PROFILE.equals(migrateProfile)) {
                                if (log.isDebugEnabled()) {
                                    log.debug(Constants.GATEWAY_MIGRATE_PROFILE + " profile migration has started");
                                }
                                migrationClient.synapseFileSystemMigration();
                            } else {
                                log.error("Invalid migration profile : " + migrateProfile);
                            }
                        } else {
                            log.error("Migration profile is not specified. Please specify the migration profile.");
                        }
                    }
                    log.info("App Manager 1.1.0 to 1.2.0 migration successfully completed");

                } else {
                    log.error("The given migrate version " + migrateToVersion +
                            " is not supported. Please check the version and try again.");
                }
            } else { // Migration version not specified
                log.error("Migration version is not specified");
            }
        } catch (APPMMigrationException e) {
            log.error("API Management  exception occurred while migrating", e);
        } catch (UserStoreException e) {
            log.error("User store  exception occurred while migrating", e);
        }
    }

    /**
     * Method to deactivate bundle.
     *
     * @param context OSGi component context.
     */
    protected void deactivate(ComponentContext context) {
        log.info("WSO2 App Manager migration bundle is deactivated");
    }

    /**
     * Method to set registry service.
     *
     * @param registryService service to get tenant data.
     */
    protected void setRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting RegistryService for WSO2 App Manager migration");
        }
        ServiceHolder.setRegistryService(registryService);
    }

    /**
     * Method to unset registry service.
     *
     * @param registryService service to get registry data.
     */
    protected void unsetRegistryService(RegistryService registryService) {
        if (log.isDebugEnabled()) {
            log.debug("Unset Registry service");
        }
        ServiceHolder.setRegistryService(null);
    }

    /**
     * Method to set realm service.
     *
     * @param realmService service to get tenant data.
     */
    protected void setRealmService(RealmService realmService) {
        log.debug("Setting RealmService for WSO2 App Manager migration");
        ServiceHolder.setRealmService(realmService);
    }

    /**
     * Method to unset realm service.
     *
     * @param realmService service to get tenant data.
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unset Realm service");
        }
        ServiceHolder.setRealmService(null);
    }

    /**
     * Method to set tenant registry loader
     *
     * @param tenantRegLoader tenant registry loader
     */
    protected void setTenantRegistryLoader(TenantRegistryLoader tenantRegLoader) {
        log.debug("Setting TenantRegistryLoader for WSO2 App Manager migration");
        ServiceHolder.setTenantRegLoader(tenantRegLoader);
    }

    /**
     * Method to unset tenant registry loader
     *
     * @param tenantRegLoader tenant registry loader
     */
    protected void unsetTenantRegistryLoader(TenantRegistryLoader tenantRegLoader) {
        log.debug("Unset Tenant Registry Loader");
        ServiceHolder.setTenantRegLoader(null);
    }

    /**
     * Method to set App Manager configuration
     *
     * @param appManagerConfig app manager configuration
     */
    protected void setApiManagerConfig(AppManagerConfigurationService appManagerConfig) {
        ServiceHolder.setAppManagerConfigurationService(appManagerConfig);
    }

    /**
     * Method to unset API manager configuration
     *
     * @param appManagerConfig app manager configuration
     */
    protected void unsetApiManagerConfig(AppManagerConfigurationService appManagerConfig) {
        ServiceHolder.setAppManagerConfigurationService(null);
    }

}
