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
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.service.TenantRegistryLoader;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;

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

public class APIMMigrationServiceComponent {

    private static final Log log = LogFactory.getLog(APIMMigrationServiceComponent.class);

    /**
     * Method to activate bundle.
     *
     * @param context OSGi component context.
     */
    protected void activate(ComponentContext context) {
        try {
            APIMgtDBUtil.initialize();
        } catch (Exception e) {
            log.error("Error occurred while initializing DB Util ", e);
        }

        String migrateToVersion = System.getProperty(Constants.ARG_MIGRATE_TO_VERSION);
        String tenants = System.getProperty(Constants.ARG_MIGRATE_TENANTS);
        boolean migrateAll = Boolean.parseBoolean(System.getProperty(Constants.ARG_MIGRATE_ALL));
        boolean isDBMigration = Boolean.parseBoolean(System.getProperty(Constants.ARG_MIGRATE_DB));
        boolean isRegistryMigration = Boolean.parseBoolean(System.getProperty(Constants.ARG_MIGRATE_REG));


        try {
            if (migrateToVersion != null) {
                if (Constants.VERSION_1_1_0.equalsIgnoreCase(migrateToVersion)) {
                    log.info("Migrating WSO2 App Manager 1.0.0 to  WSO2 App Manager 1.1.0");

                    MigrationClient migrationClient = new MigrationClientImpl(tenants);

                    //Default operation will migrate all three types of resources
                    if (migrateAll) {
                        log.info("Migrating WSO2 App Manager 1.0.0 resources to WSO2 App Manager 1.1.0");
                        migrationClient.databaseMigration(migrateToVersion);
                        migrationClient.registryResourceMigration();
                    } else {
                        //Only performs database migration
                        if (isDBMigration) {
                            log.info("Migrating WSO2 App Manager 1.0.0 databases to WSO2 App Manager 1.1.0");
                            migrationClient.databaseMigration(migrateToVersion);
                        }
                        //Only performs registry migration
                        if (isRegistryMigration) {
                            log.info("Migrating WSO2 App Manager 1.0.0 registry resources to WSO2 App Manager 1.1.0");
                            migrationClient.registryResourceMigration();
                        }
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("App Manager 1.0.0 to 1.1.0 migration successfully completed");
                    }
                } else {
                    log.error("The given migrate version " + migrateToVersion + " is not supported. Please check the version and try again.");

                }
            }
            else { // Migration version not specified
                if (migrateAll || isDBMigration || isRegistryMigration) {
                    log.error("The property " + Constants.ARG_MIGRATE_TO_VERSION + " has not been specified . Please specify the property and try again.");
                }
            }
        } catch (APPMMigrationException e) {
            log.error("API Management  exception occurred while migrating", e);
        } catch (UserStoreException e) {
            log.error("User store  exception occurred while migrating", e);
        } catch (SQLException e) {
            log.error("SQL exception occurred while migrating", e);
        } catch (Throwable t) {
            log.error("Throwable error", t);
        }
        log.info("WSO2 App Manager migration component successfully activated.");
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
