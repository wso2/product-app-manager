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

import org.apache.axis2.AxisFault;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.appmgt.api.AppManagementException;
import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.api.model.JavaPolicy;
import org.wso2.carbon.appmgt.api.model.WebApp;
import org.wso2.carbon.appmgt.impl.AppMConstants;
import org.wso2.carbon.appmgt.impl.AppManagerConfiguration;
import org.wso2.carbon.appmgt.impl.dao.AppMDAO;
import org.wso2.carbon.appmgt.impl.dto.Environment;
import org.wso2.carbon.appmgt.impl.service.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.template.APITemplateBuilder;
import org.wso2.carbon.appmgt.impl.template.APITemplateBuilderImpl;
import org.wso2.carbon.appmgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.appmgt.impl.utils.RESTAPIAdminClient;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.dto.SynapseDTO;
import org.wso2.carbon.appmgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.appmgt.migration.util.*;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.user.api.Tenant;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.FileUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * This class contains all the methods which is used to migrate Webapps from App Manager 1.0.0 to App Manager 1.1.0.
 * The migration performs in database, registry and file system
 */

public class MigrationClientImpl implements MigrationClient {

    private static final Log log = LogFactory.getLog(MigrationClientImpl.class);
    private List<Tenant> tenantsArray;
    private MigrationDBCreator migrationDBCreator;
    RegistryService registryService;

    public MigrationClientImpl(String tenantArguments, RegistryService registryService,
                               TenantManager tenantManager) throws UserStoreException, APPMMigrationException {

        AppManagerConfiguration config = ServiceHolder.getAppManagerConfigurationService().getAPIManagerConfiguration();
        String dataSourceName = config.getFirstProperty(Constants.DATA_SOURCE_NAME);
        this.registryService = registryService;
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

    /**
     * This method is used to migrate database tables
     * This executes the database queries according to the user's db type and alters the tables
     *
     * @throws APPMMigrationException
     */
    @Override
    public void databaseMigration() {
        log.info("Database migration for App Manager 1.2.0 started");
        try {
            final String productHome = CarbonUtils.getCarbonHome();
            String scriptPath = productHome + Constants.MIGRATION_SCRIPTS_LOCATION;
            updateAPPManagerDatabase(scriptPath);
        } catch (APPMMigrationException e) {
            log.error("Error occurred while migrating databases for App Manager 1.2.0", e);
        }
        log.info("Database migration for App Manager 1.2.0 is successfully completed for all tenants");
    }


    /**
     * This method is used to migrate all registry resources
     * This migrates webapp rxts
     *
     * @throws org.wso2.carbon.appmgt.migration.APPMMigrationException
     */
    @Override
    public void registryResourceMigration() {
        log.info("Registry resource migration for App Manager is 1.2.0 started");
        try {
            migrateRxts();
            migrateLifeCycles();
            registryArtifactMigration();
            signUpConfigurationMigration();
            updateTenantStoreConfiguration();
        } catch (APPMMigrationException e) {
            log.error("Error occurred while migrating registry resources for App Manager 1.2.0", e);
        }
        log.info("Registry resource migration for App Manager 1.2.0 is successfully completed for all tenants");
    }

    @Override
    public void synapseFileSystemMigration() {
        log.info("Synapse configuration file migration for App Manager 1.2.0 started");
        try {
        synapseAPIMigration();
        } catch (APPMMigrationException e) {
            log.error("Error occurred while migrating synapse configuration files for App Manager 1.2.0", e);
        }
        log.info("Synapse configuration file migration for App Manager 1.2.0 is completed successfully");
    }

    private static DataSource initializeDataSource(String dataSourceName) throws APPMMigrationException {
        DataSource ds = null;
        if (dataSourceName != null) {
            try {
                Context ctx = new InitialContext();
                ds = (DataSource) ctx.lookup(dataSourceName);
            } catch (NamingException e) {
                ResourceUtil.handleException("Error while looking up the data " + "source: " + dataSourceName, e);
            }
        }
        return ds;
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

    protected void updateAPPManagerDatabase(String sqlScriptPath) throws APPMMigrationException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        BufferedReader bufferedReader = null;
        try {
            connection = APIMgtDBUtil.getConnection();
            connection.setAutoCommit(false);
            String dbType = MigrationDBCreator.getDatabaseType(connection);

            InputStream is = new FileInputStream(sqlScriptPath + dbType + ".sql");
            bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String sqlQuery = "";
            boolean isFoundQueryEnd = false;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("//") || line.startsWith("--")) {
                    continue;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken();
                    if ("REM".equalsIgnoreCase(token)) {
                        continue;
                    }
                }

                if (line.contains("\\n")) {
                    line = line.replace("\\n", "");
                }

                sqlQuery += ' ' + line;
                if (line.contains(";")) {
                    isFoundQueryEnd = true;
                }

                if (org.wso2.carbon.appmgt.migration.util.Constants.DB_TYPE_ORACLE.equals(dbType)) {
                    sqlQuery = sqlQuery.replace(";", "");
                }

                if (isFoundQueryEnd) {
                    if (sqlQuery.length() > 0) {
                        if (log.isDebugEnabled()) {
                            log.debug("SQL to be executed : " + sqlQuery);
                        }

                        preparedStatement = connection.prepareStatement(sqlQuery.trim());
                        preparedStatement.execute();
                        connection.commit();
                    }

                    // Reset variables to read next SQL
                    sqlQuery = "";
                    isFoundQueryEnd = false;
                }
            }

        } catch (IOException e) {
            throw new APPMMigrationException("Error occurred while migrating App Management database", e);
        } catch (Exception e) {
            /* MigrationDBCreator extends from org.wso2.carbon.utils.dbcreator.DatabaseCreator and in the super class
            method getDatabaseType throws generic Exception */
            throw new APPMMigrationException("Error occurred while migrating App Management databases", e);
        } finally {
            APIMgtDBUtil.closeAllConnections(preparedStatement, connection, null);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    handleException(
                            "Error occurred while migrating App Management Databases. Failed to read sql scripts in "
                                    + sqlScriptPath, e);
                }
            }
        }
    }

    private void synapseAPIMigration() throws APPMMigrationException {
        for (Tenant tenant : tenantsArray) {
            if(log.isDebugEnabled()){
                log.debug("Synapse configuration file system migration is started for tenant "+tenant.getDomain());
            }
            String apiPath = ResourceUtil.getApiPath(tenant.getId(), tenant.getDomain());
            List<SynapseDTO> synapseDTOs = ResourceUtil.getVersionedAPIs(apiPath);

            for (SynapseDTO synapseDTO : synapseDTOs) {
                Document document = synapseDTO.getDocument();
                NodeList resourceNodes = document.getElementsByTagName("resource");
                for (int i = 0; i < resourceNodes.getLength(); i++) {
                    Element resourceElement = (Element) resourceNodes.item(i);
                    Element inSequenceElement = (Element) resourceElement.getElementsByTagName(Constants.SYNAPSE_IN_SEQUENCE_ELEMENT).item(0);
                    //Set attribute values to in sequence 'noVersion' property


                    //Find the property element in the inSequence
                    NodeList propertyElements = inSequenceElement.getElementsByTagName(Constants.SYNAPSE_PROPERTY_ELEMENT);

                    boolean isNoVersionPropertyExists = false;
                    for (int j = 0; j < propertyElements.getLength(); j++) {
                        Element propertyElement = (Element) propertyElements.item(j);
                        if ("POST_TO_URI".equals(propertyElement.getAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME))) {
                            propertyElement.setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_VALUE, "false");
                        } else if ("noVersion".equals(propertyElement.getAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME))) {
                            isNoVersionPropertyExists = true;
                        }
                    }
                    if (!isNoVersionPropertyExists) {
                        Element newElement = document.createElement(Constants.SYNAPSE_PROPERTY_ELEMENT);
                        newElement.setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME, Constants.SYNAPSE_API_NO_VERSION_PROPERTY);
                        newElement.setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_EXPRESSION, "get-property('transport', 'WSO2_APPM_INVOKED_WITHOUT_VERSION')");
                        newElement.setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_VALUE, "true");
                        inSequenceElement.insertBefore(newElement, inSequenceElement.getFirstChild());
                    }

                }
                ResourceUtil.transformXMLDocument(document, synapseDTO.getFile());
            }
            if(log.isDebugEnabled()){
                log.debug("Synapse configuration file system migration for tenant "+tenant.getDomain()+"is completed successfully");
            }
        }
    }

    private void migrateLifeCycles() throws APPMMigrationException {
        log.info("Lifecycle migration is started for App Manager 1.2.0");
        migrateLifeCycle(Constants.WEBAPP_LIFECYCLE);
        migrateLifeCycle(Constants.MOBILEAPP_LIFECYCLE);
        log.info("Lifecycle migration is completed successfully for App Manager 1.2.0");
    }

    /**
     * Migrates and update lifecycle resources
     *
     * @throws APPMMigrationException
     */
    private void migrateLifeCycle(String lifecycleType) throws APPMMigrationException {
        log.info("Lifecycle migration for " + lifecycleType + " is started.");
        for (Tenant tenant : tenantsArray) {
            registryService.startTenantFlow(tenant);
            String lifeCycleXMLPath = CarbonUtils.getCarbonHome() + Constants.MIGRATION_LIFECYCLE_LOCATION +
                    lifecycleType + ".xml";

            String lifecycleConfig = null;
            final String lifeCycleRegistryPath = RegistryConstants.LIFECYCLE_CONFIGURATION_PATH + lifecycleType;
            try {
                if (log.isDebugEnabled()) {
                    log.info("Migrating " + lifecycleType + " for tenant " + tenant.getDomain() + " is started.");
                }
                lifecycleConfig = IOUtils.toString(new FileInputStream(new File(lifeCycleXMLPath)));
                registryService.updateConfigRegistryResource(lifeCycleRegistryPath, lifecycleConfig);
                if (log.isDebugEnabled()) {
                    log.info("Migrating " + lifecycleType + " for tenant " + tenant.getDomain() + " is completed successfully.");
                }
            } catch (FileNotFoundException e) {
                handleException("Error occurred while updating the lifecycle :" + lifecycleType +
                        lifecycleType + ".xml file cannot be found at : " + lifeCycleXMLPath, e);
            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                handleException("Error occurred while updating " + lifecycleType +
                        " resource in config registry for tenant : " + tenant.getDomain(), e);
            } catch (UserStoreException e) {
                handleException("Error occurred while retrieving config registry for tenant : " +
                        tenant.getDomain(), e);
            } catch (IOException e) {
                handleException("Error occurred while reading " + lifecycleType + ".xml from " +
                        lifeCycleXMLPath, e);
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Lifecycle migration for" + lifecycleType + " migration is completed successfully");
    }

    private void registryArtifactMigration() throws APPMMigrationException {
        log.info("Registry artifact migration is started");
        webAppArtifactMigration();
        mobileAppArtifactMigration();
        log.info("Registry artifact migration is completed successfully");

    }

    private void webAppArtifactMigration() throws APPMMigrationException {
        log.info("Webapp registry artifact migration is started");
        for (Tenant tenant : tenantsArray) {
            registryService.startTenantFlow(tenant);
            if (log.isDebugEnabled()) {
                log.debug("Starting webapp registry artifact migration for tenant " + tenant.getDomain());
            }
            try {

                Registry registry = registryService.getGovernanceRegistry();
                GenericArtifact[] artifacts = registryService.getGenericArtifacts(AppMConstants.WEBAPP_ASSET_TYPE);
                Map<String, MigratingWebApp> oldWebappMap = getOldWebAppVersionMap(artifacts);

                for (GenericArtifact webAppArtifact : artifacts) {

                    WebApp webapp = AppManagerUtil.getAPI(webAppArtifact, registry);
                    if (webapp == null) {
                        log.error("Cannot find corresponding web application for registry artifact " +
                                webAppArtifact.getAttribute("overview_name") + "-"
                                + webAppArtifact.getAttribute("overview_version") + "-" +
                                webAppArtifact.getAttribute("overview_provider") + " of tenant " + tenant.getDomain());
                        continue;
                    }
                    APIIdentifier webAppIdentifier = webapp.getId();

                    webAppArtifact.removeAttribute(AppMConstants.APP_OVERVIEW_MAKE_AS_DEFAULT_VERSION);
                    webAppArtifact.addAttribute(AppMConstants.APP_OVERVIEW_MAKE_AS_DEFAULT_VERSION, "false");

                    webAppArtifact.removeAttribute(AppMConstants.APP_OVERVIEW_OLD_VERSION);
                    if (!webAppIdentifier.getVersion().equals(oldWebappMap.get(webAppIdentifier.getApiName()).getVersion())) {
                        webAppArtifact.addAttribute(AppMConstants.APP_OVERVIEW_OLD_VERSION, oldWebappMap.get(webAppIdentifier.getApiName()).getVersion());
                    } else {
                        webAppArtifact.addAttribute(AppMConstants.APP_OVERVIEW_OLD_VERSION, "");
                    }
                    webAppArtifact.removeAttribute(AppMConstants.APP_OVERVIEW_TREAT_AS_A_SITE);
                    webAppArtifact.addAttribute(AppMConstants.APP_OVERVIEW_TREAT_AS_A_SITE, "FALSE");

                    String resourcePath = webAppArtifact.getPath();
                    Resource resource = registry.get(resourcePath);
                    Properties properties = resource.getProperties();

                    Iterator<Object> propertyKeySetItr = properties.keySet().iterator();
                    ArrayList<String> propertyLeyList = new ArrayList<String>();
                    while (propertyKeySetItr.hasNext()) {
                        Object key = propertyKeySetItr.next();
                        propertyLeyList.add(key.toString());
                    }

                    ArrayList<String> mandatoryPropertyList = getMandatoryArtifactProperties();

                    //Remove unwanted properties in webapp artifact in order to avoid indexing issues
                    for (String propertyKey : propertyLeyList) {
                        if (!mandatoryPropertyList.contains(propertyKey)) {
                            resource.removeProperty(propertyKey);
                        }
                    }
                    //Update the registry artifact resource after removing the unwanted properties
                    registry.put(resourcePath, resource);
                }
                registryService.updateGenericArtifacts(AppMConstants.WEBAPP_ASSET_TYPE, artifacts);

            } catch (UserStoreException e) {
                handleException("Error occurred while retrieving admin user details of tenant : " +
                        tenant.getDomain(), e);
            } catch (AppManagementException e) {
                handleException("Error occurred while retrieving webapp artifacts for tenant : " +
                        tenant.getDomain(), e);
            } catch (RegistryException e) {
                handleException("Error occurred while retrieving webapp artifacts from registry for tenant : " +
                        tenant.getDomain(), e);
            } finally {
                registryService.endTenantFlow();
            }
            if (log.isDebugEnabled()) {
                log.debug("End of webapp registry artifact migration for tenant " + tenant.getDomain());
            }
        }
        log.info("Webapp registry artifact migration is completed successfully");
    }

    private void mobileAppArtifactMigration() throws APPMMigrationException {
        log.info("MobileApp registry artifact migration is started");
        for (Tenant tenant : tenantsArray) {
            registryService.startTenantFlow(tenant);
            if (log.isDebugEnabled()) {
                log.debug("Starting mobileapp registry artifact migration for tenant " + tenant.getDomain());
            }
            try {

                GenericArtifact[] mobileAppArtifacts =
                        registryService.getGenericArtifacts(AppMConstants.MOBILE_ASSET_TYPE);

                for (GenericArtifact mobileAppArtifact : mobileAppArtifacts) {

                    mobileAppArtifact.removeAttribute(AppMConstants.API_OVERVIEW_DISPLAY_NAME);
                    mobileAppArtifact.addAttribute(AppMConstants.API_OVERVIEW_DISPLAY_NAME,
                            mobileAppArtifact.getAttribute(AppMConstants.MOBILE_APP_OVERVIEW_NAME));

                    mobileAppArtifact.removeAttribute(AppMConstants.MOBILE_APP_OVERVIEW_CATEGORY);
                    mobileAppArtifact.addAttribute(AppMConstants.MOBILE_APP_OVERVIEW_CATEGORY, Constants.MOBILE_APP_DEFAULT_CATEGORY);
                    mobileAppArtifact.removeAttribute(AppMConstants.API_OVERVIEW_VISIBILITY);
                    mobileAppArtifact.addAttribute(AppMConstants.API_OVERVIEW_VISIBILITY, "");
                    String thumbnailImageId =
                            getMobileImageId(mobileAppArtifact.getAttribute(AppMConstants.MOBILE_APP_IMAGES_THUMBNAIL));
                    mobileAppArtifact.setAttribute(AppMConstants.MOBILE_APP_IMAGES_THUMBNAIL, thumbnailImageId);
                    String bannerImageId = getMobileImageId(mobileAppArtifact.getAttribute(AppMConstants.APP_IMAGES_BANNER));
                    mobileAppArtifact.setAttribute(AppMConstants.APP_IMAGES_BANNER, bannerImageId);
                    String screenShots = mobileAppArtifact.getAttribute(AppMConstants.MOBILE_APP_IMAGES_SCREENSHOTS);
                    ArrayList<String> screenShotIds = new ArrayList<>();
                    if (StringUtils.isNotEmpty(screenShots)) {
                        for (String screenShot : screenShots.split(",")) {
                            String screenShotId = "";
                            if (StringUtils.isNotEmpty(screenShot)) {
                                screenShotId = getMobileImageId(screenShot);
                            }
                            screenShotIds.add(screenShotId);
                        }
                        mobileAppArtifact.setAttribute(AppMConstants.MOBILE_APP_IMAGES_SCREENSHOTS, StringUtils.join(screenShotIds, ","));
                    }
                }
                registryService.updateGenericArtifacts(AppMConstants.MOBILE_ASSET_TYPE, mobileAppArtifacts);

            } catch (GovernanceException e) {
                log.error("Error occurred while migrating mobileapp registry artifacts for tenant " + tenant.getDomain());
            } finally {
                registryService.endTenantFlow();
            }
            if (log.isDebugEnabled()) {
                log.debug("End of mobileapp registry artifact migration for tenant " + tenant.getDomain());
            }
        }
        log.info("MobileApp registry artifact migration is completed successfully");
    }

    private String getMobileImageId(String imageURL) {
        String imageId = null;
        if (StringUtils.isNotEmpty(imageURL)) {
            imageId = imageURL.substring(imageURL.lastIndexOf("/") + 1, imageURL.length());
        }
        return imageId;
    }

    private ArrayList<String> getMandatoryArtifactProperties() {
        ArrayList<String> propertyKeys = new ArrayList<String>();

        propertyKeys.add("registry.lifecycle.WebAppLifeCycle.state");
        propertyKeys.add("registry.LC.name");
        propertyKeys.add("registry.realpath");
        propertyKeys.add("registry.user");
        propertyKeys.add("registry.Aspects");
        propertyKeys.add("registry.link");
        propertyKeys.add("registry.mount");
        propertyKeys.add("registry.LC.name.WebAppLifeCycle");
        propertyKeys.add("resource.source");
        return propertyKeys;
    }

    public void migrateArtifactLifecycleHistory(Registry registry, APIIdentifier apiIdentifier) {
        String appLcHistoryPath = Constants.REGISTRY_ARTIFACT_LIFECYCLE_HISTORY_OLD + "provider_" +
                apiIdentifier.getProviderName() + "_" + apiIdentifier.getApiName() + "_" + apiIdentifier.getVersion() + "_webapp";
        try {
            Resource resource = registry.get(appLcHistoryPath);
            JSONObject lcHistoryContent = XML.toJSONObject(new String((byte[]) resource.getContent()));
            //   JSONObject lcHistoryElements = lcHistoryContent.getJSONObject("lifecycleHistory").getJSONObject("item");

        } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
            log.error("Error occurred while retrieving lifecycle history registry resource : " + appLcHistoryPath, e);
        } catch (JSONException e) {
            log.error("Error occurred while parsing lifecycle history resource content", e);
        }
    }

    private void updateTenantStoreConfiguration() throws APPMMigrationException {
        log.info("Tenant store configuration migration is started");
        for (Tenant tenant : tenantsArray) {
            try {
                if (log.isDebugEnabled()) {
                    log.info("Migrating " + Constants.MIGRATION_TENANT_STORE_CONFIG + "configuration for tenant " +
                            tenant.getDomain() + " is started.");
                }
                registryService.startTenantFlow(tenant);
                String storeConfig =
                        ResourceUtil.getResourceContent(registryService.getConfigRegistryResource(
                                Constants.MIGRATION_TENANT_STORE_CONFIG));
                JSONObject storeConfigJSONObject = new JSONObject(storeConfig);
                storeConfigJSONObject.getJSONArray("assets").put("site");
                registryService.updateConfigRegistryResource(Constants.MIGRATION_TENANT_STORE_CONFIG,
                        storeConfigJSONObject.toString());
                if (log.isDebugEnabled()) {
                    log.info("Migrating " + Constants.MIGRATION_TENANT_STORE_CONFIG + "configuration for tenant " +
                            tenant.getDomain() + " is completed.");
                }
            } catch (RegistryException e) {
                handleException("Error occurred while migrating tenant store configuration in registry path " +
                        Constants.MIGRATION_TENANT_STORE_CONFIG, e);
            } catch (UserStoreException e) {
                handleException("Error occurred while migrating tenant store configuration in registry path " +
                        Constants.MIGRATION_TENANT_STORE_CONFIG, e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Tenant store configuration migration is completed successfully");
    }

    private void signUpConfigurationMigration() throws APPMMigrationException {

        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);
                //Resource externalStoreResource = registry.get(APIConstants.EXTERNAL_API_STORES_LOCATION);
                String config = ResourceUtil.getResourceContent(registryService.getGovernanceRegistryResource(
                        AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION));
                String modifiedConfig = modifySignUpConfiguration(config);
                registryService.updateGovernanceRegistryResource(AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION,
                        modifiedConfig);
            } catch (RegistryException e) {
                handleException("Error occurred while updating signup configuration in registry for tenant "
                        + tenant.getDomain(), e);
                try {
                    registryService.rollbackGovernanceRegistryTransaction();
                } catch (org.wso2.carbon.registry.core.exceptions.RegistryException ex) {
                    handleException("Error occurred while rolling back registry transaction to update signup " +
                            "configuration for tanant " + tenant.getDomain(), ex);
                } catch (UserStoreException ex) {
                    handleException("Error occurred while rolling back registry transaction to update signup " +
                            "configuration for tanant " + tenant.getDomain(), ex);
                }
            } catch (UserStoreException e) {
                handleException("Error occurred while updating signup configuration in registry for tenant "
                        + tenant.getDomain(), e);
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    private String modifySignUpConfiguration(String configXml) throws APPMMigrationException {

        Writer stringWriter = new StringWriter();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource configInputSource = new InputSource();
            configInputSource.setCharacterStream(new StringReader(configXml.trim()));
            Document doc = builder.parse(configInputSource);
            NodeList nodes = doc.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROOT);
            if (nodes.getLength() > 0) {
                // iterate through sign-up role list
                Element roleListParent = (Element) ((Element) nodes.item(0)).
                        getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLES_ELEM).item(0);

                NodeList rolesEl = roleListParent.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLE_ELEM);
                for (int i = 0; i < rolesEl.getLength(); i++) {
                    Element tmpEl = (Element) rolesEl.item(i);
                    Element permissionElement = (Element) tmpEl.getElementsByTagName(AppMConstants.SELF_SIGN_UP_REG_ROLE_PERMISSIONS).item(0);
                    if (permissionElement == null) {
                        Element externalRole =
                                (Element) tmpEl.getElementsByTagName(
                                        AppMConstants.SELF_SIGN_UP_REG_ROLE_IS_EXTERNAL).item(0);
                        Element newElement = doc.createElement(AppMConstants.SELF_SIGN_UP_REG_ROLE_PERMISSIONS);
                        //Set default permissions into config
                        newElement.setTextContent("/permission/admin/login,/permission/admin/manage/webapp/subscribe");
                        tmpEl.insertBefore(newElement, externalRole);
                    }
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));

        } catch (SAXException e) {
            handleException("Error occurred while parsing signup configuration.", e);
        } catch (IOException e) {
            handleException("Error occurred while reading the sign up configuration document. " +
                    "Please check the existence of signup configuration file in registry.", e);
        } catch (ParserConfigurationException e) {
            handleException("Error occurred while trying to build the signup configuration xml document", e);
        } catch (TransformerException e) {
            handleException("Error occurred while saving modified signup configuration xml document", e);
        }

        return stringWriter.toString();
    }

    private void migrateRxts() throws APPMMigrationException {
        log.info("Rxt migration is started for App Manager 1.2.0");
        migrateRxt(Constants.WEBAPP_RXT);
        migrateRxt(Constants.MOBILEAPP_RXT);
        log.info("Rxt migration is completed successfully for App Manager 1.2.0");
    }

    /**
     * Adds new rxt fields and updates the rxt
     *
     * @throws APPMMigrationException
     */
    private void migrateRxt(String rxtType) throws APPMMigrationException {
        log.info("Rxt migration for " + rxtType + "s is started.");
        String rxtFileName = rxtType + ".rxt";
        String rxtDir = CarbonUtils.getCarbonHome() + Constants.MIGRATION_RXT_LOCATION + rxtFileName;

        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);

                if (log.isDebugEnabled()) {
                    log.info("Migrating " + rxtFileName + " for tenant " + tenant.getDomain() + " is started.");
                }
                String rxt = FileUtil.readFileToString(rxtDir);
                registryService.updateRXTResource(rxtFileName, rxt);
                if (log.isDebugEnabled()) {
                    log.info("Migrating " + rxtFileName + " for tenant " + tenant.getDomain() + " is completed successfully.");
                }

            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                handleException("Error occurred while accessing " + rxtType + " artifacts in registry for tenant " +
                        tenant.getDomain(), e);
            } catch (IOException e) {
                handleException("Error occurred while reading " + rxtFileName + " from " + rxtDir + "for tenant " +
                        tenant.getDomain(), e);
            } catch (UserStoreException e) {
                handleException("Error while updating " + rxtFileName + " in the registry for tenant " + tenant.getDomain(), e);
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Rxt migration for " + rxtType + "s is completed successfully.");
    }

    /*public void updateWebappMaps(GenericArtifact[] artifacts, Registry registry,
                                 HashMap<String, MigratingWebApp> defaultVersionedWebApps, HashMap<String, MigratingWebApp> oldWebappMap) {

        try {
            for (GenericArtifact artifact : artifacts) {

                WebApp webApp = AppManagerUtil.getAPI(artifact, registry);
                if (webApp == null) {
                    continue;
                }

                String createdTimeStamp = artifact.getAttribute("overview_createdtime");
                createdTimeStamp.replaceFirst("^0+(?!$)", "");
                long epochTimeStamp = Long.parseLong(createdTimeStamp);

                MigratingWebApp migratingWebApp = new MigratingWebApp(webApp);
                migratingWebApp.setCreatedTime(new Date(epochTimeStamp));

                if (defaultVersionedWebApps.containsKey(migratingWebApp.getAppName())) {
                    MigratingWebApp currentDefaultWebapp = defaultVersionedWebApps.get(migratingWebApp.getAppName());
                    if ((currentDefaultWebapp.isPublished() && migratingWebApp.isPublished()
                            && migratingWebApp.getCreatedTime().after(currentDefaultWebapp.getCreatedTime()) ||
                            (migratingWebApp.isPublished() && !currentDefaultWebapp.isPublished()) ||
                            (!currentDefaultWebapp.isPublished() && !migratingWebApp.isPublished()
                                    && migratingWebApp.getCreatedTime().after(currentDefaultWebapp.getCreatedTime())))) {
                        defaultVersionedWebApps.put(migratingWebApp.getAppName(), migratingWebApp);

                    }
                } else {
                    defaultVersionedWebApps.put(migratingWebApp.getAppName(), migratingWebApp);
                }


                //Update old version Map
                if (oldWebappMap.containsKey(migratingWebApp.getAppName())) {
                    MigratingWebApp currentOldWebapp = oldWebappMap.get(migratingWebApp.getAppName());
                    if (migratingWebApp.getCreatedTime().before(currentOldWebapp.getCreatedTime())) {
                        oldWebappMap.put(migratingWebApp.getAppName(), migratingWebApp);
                    }
                } else {
                    oldWebappMap.put(migratingWebApp.getAppName(), migratingWebApp);
                }

            }
        } catch (GovernanceException e) {
            log.error("Error occurred while reading artifact attribute values", e);
        } catch (AppManagementException e) {
            log.error("Error occurred while retrieving webapp", e);
        }
    }*/

    private Map<String, MigratingWebApp> getOldWebAppVersionMap(GenericArtifact[] artifacts) {
        Map<String, MigratingWebApp> oldWebappMap = new HashMap<>();
        try {
            for (GenericArtifact artifact : artifacts) {

                WebApp webApp = AppManagerUtil.getAPI(artifact);
                if (webApp == null) {
                    continue;
                }

                String createdTimeStamp = artifact.getAttribute(AppMConstants.API_OVERVIEW_CREATED_TIME);
                createdTimeStamp.replaceFirst("^0+(?!$)", "");
                long epochTimeStamp = Long.parseLong(createdTimeStamp);

                MigratingWebApp migratingWebApp = new MigratingWebApp(webApp);
                migratingWebApp.setCreatedTime(new Date(epochTimeStamp));
                //Update old version Map
                if (oldWebappMap.containsKey(migratingWebApp.getAppName())) {
                    MigratingWebApp currentOldWebapp = oldWebappMap.get(migratingWebApp.getAppName());
                    if (migratingWebApp.getCreatedTime().before(currentOldWebapp.getCreatedTime())) {
                        oldWebappMap.put(migratingWebApp.getAppName(), migratingWebApp);
                    }
                } else {
                    oldWebappMap.put(migratingWebApp.getAppName(), migratingWebApp);
                }

            }
        } catch (GovernanceException e) {
            log.error("Error occurred while reading artifact attribute values", e);
        } catch (AppManagementException e) {
            log.error("Error occurred while retrieving webapp", e);
        }
        return oldWebappMap;
    }


    private static void handleException(String msg, Throwable t) throws APPMMigrationException {
        throw new APPMMigrationException(msg, t);
    }

}
