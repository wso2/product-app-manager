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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.appmgt.api.APIProvider;
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
import org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
import org.wso2.carbon.appmgt.impl.utils.RESTAPIAdminClient;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.dto.SynapseDTO;
import org.wso2.carbon.appmgt.migration.client.internal.ServiceHolder;
import org.wso2.carbon.appmgt.migration.util.*;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.session.UserRegistry;
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
import javax.xml.XMLConstants;
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
import java.nio.charset.Charset;
import java.util.*;

/**
 * This class contains all the methods which is used to migrate Webapps from App Manager 1.0.0 to App Manager 1.1.0.
 * The migration performs in database, registry and file system
 */

public class MigrationClientImpl implements MigrationClient {

    private static final Log log = LogFactory.getLog(MigrationClientImpl.class);
    private List<Tenant> tenantsArray;
    private MigrationDBCreator migrationDBCreator;
    RegistryService registryService;
    APIProvider apiProvider;

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
     * @throws APPMMigrationException
     */
    @Override
    public void databaseMigration() throws APPMMigrationException {
        migrationDBCreator.migrateDatabaseTables();
    }

    /**
     * Initialize received data source
     *
     * @param dataSourceName : Data source name needs to be initialized
     * @return DataSource
     * @throws org.wso2.carbon.appmgt.api.AppManagementException if an error occurs while initializing the data source
     */
    public static DataSource initializeDataSource(String dataSourceName) throws APPMMigrationException {
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

    /**
     * This method is used to migrate all registry resources
     * This migrates webapp rxts
     *
     * @throws org.wso2.carbon.appmgt.migration.APPMMigrationException
     */
    @Override
    public void registryResourceMigration() throws APPMMigrationException {
        log.info("Registry resource migration for App Manager 1.2.0 started.");
        migrateRxts();
        migrateLifeCycles();
        registryArtifactMigration();
        externalStoreMigration();
        updateTenantStoreConfiguration();
    }

    @Override
    public void synapseFileSystemMigration() throws APPMMigrationException {
        synapseAPIMigration();
    }

    public void deployDefaultSynapseConfigurations(final HashMap<String, MigratingWebApp> migratingWebApps) throws APPMMigrationException {
        Thread deployerThread = new Thread() {
            @Override
            public void run() {
                boolean done = false;
                while (!done) {
                    try {
                        deployDefaultWebAppSynapseConfigs(migratingWebApps);
                        done = true;

                    } catch (Throwable e) {
                        log.warn("Retrying");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                        }

                    }
                }
            }
        };
        deployerThread.setDaemon(true);
        deployerThread.setName("DefaultRESTApiDeployer");
        deployerThread.start();
    }

    private void deployDefaultWebAppSynapseConfigs(HashMap<String, MigratingWebApp> migratingWebApps)
            throws APPMMigrationException {

        for (String webappName : migratingWebApps.keySet()) {
            MigratingWebApp MigratingWebApp = migratingWebApps.get(webappName);
            if (MigratingWebApp.isPublished() && !MigratingWebApp.getWebApp().getSkipGateway()) {
                deployDefaultSynapseConfig(MigratingWebApp.getWebApp());
            }
        }

    }

    public void deployDefaultSynapseConfig(WebApp webapp) throws APPMMigrationException {

        APITemplateBuilder builder = null;
        String tenantDomain = null;

        String provider = webapp.getId().getProviderName().replace("-AT-", "@");
        tenantDomain = MultitenantUtils.getTenantDomain(provider);
        try {
            builder = getAPITemplateBuilder(webapp);
            AppManagerConfiguration config = ServiceReferenceHolder.getInstance()
                    .getAPIManagerConfigurationService()
                    .getAPIManagerConfiguration();
            List<Environment> environments;
            environments = config.getApiGatewayEnvironments();
            for (Environment environment : environments) {
                RESTAPIAdminClient client = new RESTAPIAdminClient(webapp.getId(), environment);
                if (client.getNonVersionedWebAppData(tenantDomain) != null) {
                    client.updateNonVersionedWebApp(builder, tenantDomain);
                } else {
                    client.addNonVersionedWebApp(builder, tenantDomain);
                }
            }

        } catch (AxisFault axisFault) {
            log.warn("Cannot contact AuthenticationAdmin Service. Retrying");
            throw new APPMMigrationException("Cannot contact Admin Service", axisFault);
        }

    }

    /**
     * This method dynamically returns the mandatory and selected java policy handlers list for given app
     *
     * @param api :WebApp class which contains details about web applications
     * @return :handlers list with properties to be applied
     * @throws AppManagementException on error
     */
    private APITemplateBuilder getAPITemplateBuilder(WebApp api) throws APPMMigrationException {
        APITemplateBuilderImpl velocityTemplateBuilder = new APITemplateBuilderImpl(api);
        AppMDAO appMDAO = new AppMDAO();

        //List of JavaPolicy class which contains policy related details
        List<JavaPolicy> policies = new ArrayList<JavaPolicy>();
        //contains properties related to relevant policy and will be used to generate the synapse api config file
        Map<String, String> properties;
        int counterPolicies; //counter :policies

        try {
            //fetch all the java policy handlers details which need to be included to synapse api config file
            policies = appMDAO.getMappedJavaPolicyList(api.getUUID(), true);
            //loop through each policy
            for (counterPolicies = 0; counterPolicies < policies.size(); counterPolicies++) {
                if (policies.get(counterPolicies).getProperties() == null) {
                    //if policy doesn't contain any properties assign an empty map and add java policy as a handler
                    velocityTemplateBuilder.addHandler(policies.get(counterPolicies).getFullQualifiName(),
                            Collections.EMPTY_MAP);
                } else {
                    //contains properties related to all the policies
                    org.json.simple.JSONObject objPolicyProperties;
                    properties = new HashMap<String, String>();

                    //get property JSON object related to current policy in the loop
                    objPolicyProperties = policies.get(counterPolicies).getProperties();

                    //if policy contains any properties, run a loop and assign them
                    Set<String> keys = objPolicyProperties.keySet();
                    for (String key : keys) {
                        properties.put(key, objPolicyProperties.get(key).toString());
                    }
                    //add policy as a handler and also the relevant properties
                    velocityTemplateBuilder.addHandler(policies.get(counterPolicies).getFullQualifiName(), properties);
                }
            }

        } catch (AppManagementException e) {
            handleException("Error occurred while adding java policy handlers to Application : " +
                    api.getId().toString(), e);
        }
        return velocityTemplateBuilder;
    }

    private void synapseAPIMigration() throws APPMMigrationException {
        for (Tenant tenant : tenantsArray) {
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
        }
    }

    /**
     * Migrates and update lifecycle resources
     *
     * @throws APPMMigrationException
     */
    private void migrateLifeCycles() throws APPMMigrationException {
        log.info("Life Cycle migration is started");
        for (Tenant tenant : tenantsArray) {
            registryService.startTenantFlow(tenant);
            String apiLifeCycleXMLPath = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "resources" +
                    File.separator + Constants.LIFE_CYCLES_FOLDER + File.separator +
                    Constants.WEBAPP_LIFECYCLE + ".xml";

            String apiLifeCycle = null;
            final String webappLifeCycleRegistryPath = RegistryConstants.LIFECYCLE_CONFIGURATION_PATH +
                    Constants.WEBAPP_LIFECYCLE;
            try {
                apiLifeCycle = IOUtils.toString(new FileInputStream(new File(apiLifeCycleXMLPath)));
                registryService.updateConfigRegistryResource(webappLifeCycleRegistryPath, apiLifeCycle);
            } catch (FileNotFoundException e) {
                handleException("Error occurred while updating the lifecycle :" + Constants.WEBAPP_LIFECYCLE +
                        Constants.WEBAPP_LIFECYCLE + ".xml file cannot be found at : " + apiLifeCycleXMLPath, e);
            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                handleException("Error occurred while updating " + Constants.WEBAPP_LIFECYCLE +
                        " resource in config registry for tenant : " + tenant.getId() +
                        " (" + tenant.getDomain() + ").", e);
            } catch (UserStoreException e) {
                handleException("Error occurred while retrieving config registry for tenant : " +
                        tenant.getId() + " (" + tenant.getDomain() + ") .", e);
            } catch (IOException e) {
                handleException("Error occurred while reading " + Constants.WEBAPP_LIFECYCLE + ".xml from " +
                        apiLifeCycleXMLPath, e);
            } finally {
                registryService.endTenantFlow();
            }
        }
        log.info("Life Cycle migration is completed.");
    }

    private void registryArtifactMigration() throws APPMMigrationException {
        log.info("Registry artifact migration has started");
        for (Tenant tenant : tenantsArray) {
            registryService.startTenantFlow(tenant);
            if (log.isDebugEnabled()) {
                log.debug("Starting registry artifact migration for tenant " + tenant.getId() + "(" + tenant.getDomain() + ")");
            }
            try {

                Registry registry = registryService.getGovernanceRegistry();
                GenericArtifact[] artifacts = registryService.getGenericWebappArtifacts();
                final HashMap<String, MigratingWebApp> defaultWebappMap = new HashMap<String, MigratingWebApp>();
                HashMap<String, MigratingWebApp> oldWebappMap = new HashMap<String, MigratingWebApp>();
                //Search and retrieve default versioned webapp list and old versions of webapp list
                updateWebappMaps(artifacts, registry, defaultWebappMap, oldWebappMap);
                for (GenericArtifact artifact : artifacts) {

                    WebApp webapp = AppManagerUtil.getAPI(artifact, registry);
                    if (webapp == null) {
                        log.error("Cannot find corresponding web application for registry artifact " +
                                artifact.getAttribute("overview_name") + "-"
                                + artifact.getAttribute("overview_version") + "-" +
                                artifact.getAttribute("overview_provider") + " of tenant " + tenant.getId() +
                                "(" + tenant.getDomain() + ") in AM_DB");
                        continue;
                    }
                    APIIdentifier webappIdentifier = webapp.getId();
                    artifact.removeAttribute("overview_makeAsDefaultVersion");
                    if (defaultWebappMap.get(webappIdentifier.getApiName()).getVersion().equals(
                            artifact.getAttribute("overview_version"))) {
                        artifact.addAttribute("overview_makeAsDefaultVersion", "true");
                    } else {
                        artifact.addAttribute("overview_makeAsDefaultVersion", "false");
                    }

                    artifact.removeAttribute("overview_oldVersion");
                    if (!webappIdentifier.getVersion().equals(oldWebappMap.get(webappIdentifier.getApiName()).getVersion())) {
                        artifact.addAttribute("overview_oldVersion", oldWebappMap.get(webappIdentifier.getApiName()).getVersion());
                    }
                    artifact.removeAttribute("overview_treatAsASite");
                    artifact.addAttribute("overview_treatAsASite", "FALSE");
                    artifact.removeAttribute("overview_treatAsSite");
                    artifact.addAttribute("overview_treatAsSite", "false");


                    String resourcePath = artifact.getPath();
                    Resource resource = registry.get(resourcePath);
                    Properties properties = resource.getProperties();

                    Iterator<Object> propertyKeySetItr = properties.keySet().iterator();
                    ArrayList<String> propertyLeyList = new ArrayList<String>();
                    while (propertyKeySetItr.hasNext()) {
                        Object key = propertyKeySetItr.next();
                        propertyLeyList.add(key.toString());
                    }

                    ArrayList<String> mandatoryPropertyList = getMandatoryArtifactProperties();
                    for (String propertyKey : propertyLeyList) {
                        if (!mandatoryPropertyList.contains(propertyKey)) {
                            resource.removeProperty(propertyKey);
                        }
                    }
                    //Update the registry artifact resource after removing the unwanted properties
                    registry.put(resourcePath, resource);
                }
                registryService.updateGenericAPIArtifacts(artifacts);

                migrationDBCreator.updateAppDefaultVersions(defaultWebappMap, String.valueOf(tenant.getId()));
                deployDefaultSynapseConfigurations(defaultWebappMap);


            } catch (UserStoreException e) {
                handleException("Error occurred while retrieving admin user details of tenant : " +
                        tenant.getId() + " (" + tenant.getDomain() + ").", e);
            } catch (AppManagementException e) {
                e.printStackTrace();
            } catch (GovernanceException e) {
                handleException("Error occurred while retrieving governance registry for tenant : " +
                        tenant.getId() + " (" + tenant.getDomain() + ").", e);
            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                handleException("Error occurred while retriving artifact registry for tenant : " + tenant.getId() +
                        " (" + tenant.getDomain() + ").", e);
            } finally {
                registryService.endTenantFlow();
                apiProvider = null;
            }
            if (log.isDebugEnabled()) {
                log.debug("End of registry artifact migration for tenant " + tenant.getId() +
                        "(" + tenant.getDomain() + ")");

            }
        }
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

    private void updateTenantStoreConfiguration() {

        String tenantStoreConfig = "/store/configs/store.json";

        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);
                String storeConfig =
                        ResourceUtil.getResourceContent(registryService.getConfigRegistryResource(tenantStoreConfig));
                JSONObject storeConfigJSONObject = new JSONObject(storeConfig);
                storeConfigJSONObject.getJSONArray("assets").put("site");
                registryService.updateConfigRegistryResource(tenantStoreConfig, storeConfigJSONObject.toString());
            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                e.printStackTrace();
            } catch (UserStoreException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    void externalStoreMigration() throws APPMMigrationException {

        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);
                //Resource externalStoreResource = registry.get(APIConstants.EXTERNAL_API_STORES_LOCATION);
                String config = ResourceUtil.getResourceContent(registryService.getGovernanceRegistryResource(AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION));
                String modifiedConfig = modifySignUpConfiguration(config);
                registryService.updateGovernanceRegistryResource(AppMConstants.SELF_SIGN_UP_CONFIG_LOCATION, modifiedConfig);
            } catch (RegistryException e) {
                handleException("Error occurred while accessing the registry", e);
                try {
                    registryService.rollbackGovernanceRegistryTransaction();
                } catch (org.wso2.carbon.registry.core.exceptions.RegistryException ex) {
                    handleException("Error occurred while accessing the registry", ex);
                } catch (UserStoreException ex) {
                    handleException("Error occurred while reading tenant information", ex);
                }
            } catch (UserStoreException e) {
                e.printStackTrace();
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    String modifySignUpConfiguration(String configXml) throws APPMMigrationException {

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
            handleException("Error occurred while parsing the xml document", e);
        } catch (IOException e) {
            handleException("Error occurred while reading the xml document. " +
                    "Please check for external API config file in the registry", e);
        } catch (ParserConfigurationException e) {
            handleException("Error occurred while trying to build the xml document", e);
        } catch (TransformerException e) {
            handleException("Error occurred while saving modified the xml document", e);
        }

        return stringWriter.toString();
    }

    private void migrateRxts() throws APPMMigrationException {
        migrateRxt(Constants.WEBAPP_RXT);
        migrateRxt(Constants.MOBILEAPP_RXT);
    }

    /**
     * Adds new rxt fields and updates the rxt
     *
     * @throws APPMMigrationException
     */
    private void migrateRxt(String rxtType) throws APPMMigrationException {

        String rxtName = rxtType + ".rxt";
        String rxtDir = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "resources" + File.separator + "rxts" +
                File.separator + rxtName;

        for (Tenant tenant : tenantsArray) {
            try {
                registryService.startTenantFlow(tenant);

                log.info("Updating" + rxtName + "for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');
                //Update webapp.rxt file
                String rxt = FileUtil.readFileToString(rxtDir);
                registryService.updateRXTResource(rxtName, rxt);
                log.info("End Updating api.rxt for tenant " + tenant.getId() + '(' + tenant.getDomain() + ')');

            } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
                handleException("Error when accessing API artifact in registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } catch (IOException e) {
                handleException("Error occurred while reading " + rxtName + " from " + rxtDir + "for tenant " +
                        tenant.getId() + '(' + tenant.getDomain() + ')', e);
            } catch (UserStoreException e) {
                handleException("Error while updating " + rxtName + " in the registry for tenant " + tenant.getId() + '('
                        + tenant.getDomain() + ')', e);
            } finally {
                registryService.endTenantFlow();
            }
        }
    }

    public void updateWebappMaps(GenericArtifact[] artifacts, Registry registry,
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
    }


    private static void handleException(String msg, Throwable t) throws APPMMigrationException {
        throw new APPMMigrationException(msg, t);
    }

}
