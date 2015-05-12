/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.appmanager.integration.ui;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.wso2.carbon.appmanager.integration.ui.Util.*;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.AppCreateRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.api.clients.security.SecurityAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkProperties;
import org.wso2.carbon.automation.core.utils.frameworkutils.productvariables.ProductVariables;
import org.wso2.carbon.automation.utils.esb.ESBTestCaseUtils;
import org.wso2.carbon.automation.utils.esb.EndpointGenerator;
import org.wso2.carbon.automation.utils.esb.StockQuoteClient;
import org.wso2.carbon.automation.utils.services.ServiceDeploymentUtil;
import org.wso2.carbon.security.mgt.stub.config.SecurityAdminServiceSecurityConfigExceptionException;
import org.wso2.carbon.sequences.stub.types.SequenceEditorException;


import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Base class for integration tests. Taken from the WSO2 API manager. This uses Objects related
 * to API manager because the testing framework does not contain stuff related to APP manager
 * (ex EnvironmentBuilder builder = new EnvironmentBuilder().am(userId);)
 */
public abstract class APPManagerIntegrationTest {
    protected Log log = LogFactory.getLog(getClass());
    protected StockQuoteClient axis2Client;
    protected EnvironmentVariables amServer;
    protected UserInfo userInfo;
    protected OMElement synapseConfiguration = null;
    protected ESBTestCaseUtils esbUtils;
    private List<String> proxyServicesList = null;
    private List<String> sequencesList = null;
    private List<String> endpointsList = null;
    private List<String> localEntryList = null;
    private List<String> messageProcessorsList = null;
    private List<String> messageStoresList = null;
    private List<String> sequenceTemplateList = null;
    private List<String> apiList = null;
    private List<String> priorityExecutorList = null;
    protected ApplicationProperties appProp = null;
    private Map<String, String> requestHeaders = new HashMap<String, String>();
    private String tier = null;


    protected void init() throws Exception {
        init(2);

    }

    protected void init(int userId) throws Exception {
        axis2Client = new StockQuoteClient();
        userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().am(userId);
        amServer = builder.build().getAm();
        esbUtils = new ESBTestCaseUtils();
        appProp = new ApplicationProperties();

    }

    protected void cleanup() throws Exception {
        try {
            if (synapseConfiguration != null) {
                esbUtils.deleteArtifact(synapseConfiguration, amServer.getBackEndUrl(), amServer.getSessionCookie());
                if (ExecutionEnvironment.stratos.name().equalsIgnoreCase(getExecutionEnvironment()) || isClusterEnabled()) {

                    long deploymentDelay = FrameworkFactory.getFrameworkProperties(
                            ProductConstant.ESB_SERVER_NAME).getEnvironmentVariables().getDeploymentDelay();
                    Thread.sleep(deploymentDelay);
                    Iterator<OMElement> proxies = synapseConfiguration.getChildrenWithLocalName("proxy");
                    while (proxies.hasNext()) {
                        String proxy = proxies.next().getAttributeValue(new QName("name"));

                        Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                                , "UnDeployment Synchronizing failed in workers");
                        Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                                , "UnDeployment Synchronizing failed in workers");
                        Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                                , "UnDeployment Synchronizing failed in workers");
                    }
                }
            }

            deleteProxyServices();

            deleteSequences();

            deleteEndpoints();

            deleteMessageProcessors();

            deleteMessageStores();

            deleteSequenceTemplates();

            deleteLocalEntries();

            deleteApi();

            deletePriorityExecutors();

        } finally {
            synapseConfiguration = null;
            proxyServicesList = null;
            messageProcessorsList = null;
            proxyServicesList = null;
            sequencesList = null;
            endpointsList = null;
            localEntryList = null;
            apiList = null;
            priorityExecutorList = null;
            if (axis2Client != null) {
                axis2Client.destroy();
            }
            axis2Client = null;
            userInfo = null;
            amServer = null;
            esbUtils = null;

        }
    }

//    protected String getUserRoles(){
//
//    }

    protected String getMainSequenceURL() {
        String mainSequenceUrl = amServer.getServiceUrl();
        if (mainSequenceUrl.endsWith("/services")) {
            mainSequenceUrl = mainSequenceUrl.replace("/services", "");
        }
        System.out.println("service url = " + mainSequenceUrl);
        return mainSequenceUrl + "/";

    }

    protected String getMainSequenceURLHttps() {
        String mainSequenceUrl = amServer.getSecureServiceUrl();
        if (mainSequenceUrl.endsWith("/services")) {
            mainSequenceUrl = mainSequenceUrl.replace("/services", "");
        }
        return mainSequenceUrl + "/";

    }

    protected String getServerURLHttp() {
        return getServerBackendUrlHttp();
    }

    protected String getServerURLHttps() {
        String serverUrl = FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME).getProductVariables().getBackendUrl();
        if (serverUrl.endsWith("/services")) {
            serverUrl = serverUrl.replace("/services", "/services/");
        }
        return serverUrl;
    }

    protected String getProxyServiceURLHttp(String proxyServiceName) {
        return amServer.getServiceUrl() + "/" + proxyServiceName;
    }

    protected String getApiInvocationURLHttp(String apiContext) {
        if (isBuilderEnabled()) {
            return getMainSequenceURL() + apiContext;
        } else {
            return getGatewayServerURLHttp() + apiContext;
        }
    }

    protected String getApiInvocationURLHttps(String apiContext) {
        if (isBuilderEnabled()) {
            return getMainSequenceURLHttps() + apiContext;
        } else {
            return getGatewayServerURLHttps() + apiContext;
        }
    }

    protected String getProxyServiceURLHttps(String proxyServiceName) {
        return amServer.getSecureServiceUrl() + "/" + proxyServiceName;
    }

    protected void loadSampleESBConfiguration(int sampleNo) throws Exception {
        OMElement synapseSample = esbUtils.loadSampleESBConfiguration(sampleNo);
        updateESBConfiguration(synapseSample);

    }

    protected void loadESBConfigurationFromClasspath(String relativeFilePath) throws Exception {
        relativeFilePath = relativeFilePath.replaceAll("[\\\\/]", File.separator);
        OMElement synapseConfig = esbUtils.loadClasspathResource(relativeFilePath);
        updateESBConfiguration(synapseConfig);

    }

    protected void updateESBConfiguration(OMElement synapseConfig) throws Exception {

        if (synapseConfiguration == null) {
            synapseConfiguration = synapseConfig;
        } else {
            Iterator<OMElement> itr = synapseConfig.cloneOMElement().getChildElements();
            while (itr.hasNext()) {
                synapseConfiguration.addChild(itr.next());
            }
        }
        esbUtils.updateESBConfiguration(setEndpoints(synapseConfig), amServer.getBackEndUrl(), amServer.getSessionCookie());

        if (ExecutionEnvironment.stratos.name().equalsIgnoreCase(getExecutionEnvironment()) || isClusterEnabled()) {
            long deploymentDelay = FrameworkFactory.getFrameworkProperties(
                    ProductConstant.ESB_SERVER_NAME).getEnvironmentVariables().getDeploymentDelay();
            Thread.sleep(deploymentDelay);
            Iterator<OMElement> proxies = synapseConfig.getChildrenWithLocalName("proxy");
            while (proxies.hasNext()) {
                String proxy = proxies.next().getAttributeValue(new QName("name"));

                Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                        , "Deployment Synchronizing failed in workers");
                Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                        , "Deployment Synchronizing failed in workers");
                Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxy), deploymentDelay)
                        , "Deployment Synchronizing failed in workers");
            }
        }
    }

    protected void addProxyService(OMElement proxyConfig) throws Exception {
        String proxyName = proxyConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isProxyServiceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyName)) {
            esbUtils.deleteProxyService(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyName);
        }
        if (proxyServicesList == null) {
            proxyServicesList = new ArrayList<String>();
        }
        proxyServicesList.add(proxyName);
        esbUtils.addProxyService(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(proxyConfig));

        if (ExecutionEnvironment.stratos.name().equalsIgnoreCase(getExecutionEnvironment())) {
            long deploymentDelay = FrameworkFactory.getFrameworkProperties(
                    ProductConstant.AM_SERVER_NAME).getEnvironmentVariables().getDeploymentDelay();

            Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                    , "Deployment Synchronizing failed in workers");
            Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                    , "Deployment Synchronizing failed in workers");
            Assert.assertTrue(isProxyWSDlExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                    , "Deployment Synchronizing failed in workers");

        }
    }

    protected void isProxyDeployed(String proxyServiceName) throws Exception {
        Assert.assertTrue(esbUtils.isProxyDeployed(amServer.getBackEndUrl(), amServer.getSessionCookie(),
                proxyServiceName), "Proxy Deployment failed or time out");
    }

    protected void deleteProxyService(String proxyServiceName) throws Exception {
        if (esbUtils.isProxyServiceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyServiceName)) {
            esbUtils.deleteProxyService(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyServiceName);
            Assert.assertTrue(esbUtils.isProxyUnDeployed(amServer.getBackEndUrl(), amServer.getSessionCookie(),
                    proxyServiceName), "Proxy Deletion failed or time out");
        }
        if (proxyServicesList != null && proxyServicesList.contains(proxyServiceName)) {
            proxyServicesList.remove(proxyServiceName);
        }
    }

    protected void deleteSequence(String sequenceName)
            throws SequenceEditorException, RemoteException {
        if (esbUtils.isSequenceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequenceName)) {
            esbUtils.deleteSequence(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequenceName);
        }
        if (sequencesList != null && sequencesList.contains(sequenceName)) {
            sequencesList.remove(sequenceName);
        }
    }

    protected void addSequence(OMElement sequenceConfig) throws Exception {
        String sequenceName = sequenceConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isSequenceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequenceName)) {
            esbUtils.deleteSequence(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequenceName);
        }
        esbUtils.addSequence(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(sequenceConfig));
        if (sequencesList == null) {
            sequencesList = new ArrayList<String>();
        }
        sequencesList.add(sequenceName);
    }

    protected void addEndpoint(OMElement endpointConfig)
            throws Exception {
        String endpointName = endpointConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isSequenceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), endpointName)) {
            esbUtils.deleteEndpoint(amServer.getBackEndUrl(), amServer.getSessionCookie(), endpointName);
        }
        esbUtils.addEndpoint(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(endpointConfig));
        if (endpointsList == null) {
            endpointsList = new ArrayList<String>();
        }
        endpointsList.add(endpointName);

    }

    protected void addLocalEntry(OMElement localEntryConfig) throws Exception {
        String localEntryName = localEntryConfig.getAttributeValue(new QName("key"));
        if (esbUtils.isLocalEntryExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntryName)) {
            esbUtils.deleteLocalEntry(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntryName);
        }
        esbUtils.addLocalEntry(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntryConfig);

        if (localEntryList == null) {
            localEntryList = new ArrayList<String>();
        }
        localEntryList.add(localEntryName);
    }

    protected void addMessageProcessor(OMElement messageProcessorConfig) throws Exception {
        String messageProcessorName = messageProcessorConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isMessageProcessorExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageProcessorName)) {
            esbUtils.deleteMessageProcessor(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageProcessorName);
        }
        esbUtils.addMessageProcessor(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(messageProcessorConfig));
        if (messageProcessorsList == null) {
            messageProcessorsList = new ArrayList<String>();
        }
        messageProcessorsList.add(messageProcessorName);
    }

    protected void addMessageStore(OMElement messageStoreConfig) throws Exception {
        String messageStoreName = messageStoreConfig.getAttributeValue(new QName("name"));
        if (esbUtils.isMessageStoreExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageStoreName)) {
            esbUtils.deleteMessageStore(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageStoreName);
        }
        esbUtils.addMessageStore(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(messageStoreConfig));
        if (messageStoresList == null) {
            messageStoresList = new ArrayList<String>();
        }
        messageStoresList.add(messageStoreName);
    }

    protected void addSequenceTemplate(OMElement sequenceTemplate) throws Exception {
        String name = sequenceTemplate.getAttributeValue(new QName("name"));
        if (esbUtils.isSequenceTemplateExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), name)) {
            esbUtils.deleteSequenceTemplate(amServer.getBackEndUrl(), amServer.getSessionCookie(), name);
        }
        esbUtils.addSequenceTemplate(amServer.getBackEndUrl(), amServer.getSessionCookie(), setEndpoints(sequenceTemplate));

        if (sequenceTemplateList == null) {
            sequenceTemplateList = new ArrayList<String>();
        }
        sequenceTemplateList.add(name);
    }

    protected void addApi(OMElement api) throws Exception {
        String apiName = api.getAttributeValue(new QName("name"));
        if (esbUtils.isApiExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), apiName)) {
            esbUtils.deleteApi(amServer.getBackEndUrl(), amServer.getSessionCookie(), apiName);
        }
        esbUtils.addAPI(amServer.getBackEndUrl(), amServer.getSessionCookie(), api);

        if (apiList == null) {
            apiList = new ArrayList<String>();
        }
        apiList.add(apiName);
    }

    protected void addPriorityExecutor(OMElement priorityExecutor) throws Exception {
        String executorName = priorityExecutor.getAttributeValue(new QName("name"));
        if (esbUtils.isPriorityExecutorExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), executorName)) {
            esbUtils.deletePriorityExecutor(amServer.getBackEndUrl(), amServer.getSessionCookie(), executorName);
        }
        esbUtils.addPriorityExecutor(amServer.getBackEndUrl(), amServer.getSessionCookie(), priorityExecutor);

        if (priorityExecutorList == null) {
            priorityExecutorList = new ArrayList<String>();
        }
        priorityExecutorList.add(executorName);
    }

    protected void applySecurity(String serviceName, int policyId, String[] userGroups)
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
            InterruptedException {
        SecurityAdminServiceClient securityAdminServiceClient =
                new SecurityAdminServiceClient(amServer.getBackEndUrl(), amServer.getSessionCookie());
        if (FrameworkFactory.getFrameworkProperties(ProductConstant.ESB_SERVER_NAME).getEnvironmentSettings().is_runningOnStratos()) {

            securityAdminServiceClient.applySecurity(serviceName, policyId + "", userGroups,
                    new String[]{"service.jks"}, "service.jks");
        } else {
            securityAdminServiceClient.applySecurity(serviceName, policyId + "", userGroups,
                    new String[]{"wso2carbon.jks"}, "wso2carbon.jks");
        }
        log.info("Security Scenario " + policyId + " Applied");

        Thread.sleep(1000);

    }

    protected OMElement replaceEndpoints(String relativePathToConfigFile, String serviceName,
                                         String port)
            throws XMLStreamException, FileNotFoundException {
        String config = esbUtils.loadClasspathResource(relativePathToConfigFile).toString();
        config = config.replace("http://localhost:" + port + "/services/" + serviceName,
                getBackEndServiceUrl(serviceName));

        return AXIOMUtil.stringToOM(config);
    }

    private void deleteMessageProcessors() {
        if (messageProcessorsList != null) {
            Iterator<String> itr = messageProcessorsList.iterator();
            while (itr.hasNext()) {
                String messageProcessor = itr.next();
                try {
                    if (esbUtils.isMessageProcessorExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageProcessor)) {
                        esbUtils.deleteMessageProcessor(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageProcessor);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Message Processor. " + e.getMessage());
                }
            }
            messageProcessorsList.clear();
        }
    }

    private void deleteMessageStores() {
        if (messageStoresList != null) {
            Iterator<String> itr = messageStoresList.iterator();
            while (itr.hasNext()) {
                String messageStore = itr.next();
                try {
                    if (esbUtils.isMessageStoreExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageStore)) {
                        esbUtils.deleteMessageStore(amServer.getBackEndUrl(), amServer.getSessionCookie(), messageStore);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Message store. " + e.getMessage());
                }
            }
            messageStoresList.clear();
        }
    }

    private void deleteSequences() {
        if (sequencesList != null) {
            Iterator<String> itr = sequencesList.iterator();
            while (itr.hasNext()) {
                String sequence = itr.next();
                if (!sequence.equalsIgnoreCase("fault")) {
                    try {
                        if (esbUtils.isSequenceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequence)) {
                            esbUtils.deleteSequence(amServer.getBackEndUrl(), amServer.getSessionCookie(), sequence);
                        }
                    } catch (Exception e) {
                        Assert.fail("while undeploying Sequence. " + e.getMessage());
                    }
                }
            }
            sequencesList.clear();
        }
    }

    private void deleteProxyServices() {
        if (proxyServicesList != null) {
            Iterator<String> itr = proxyServicesList.iterator();
            while (itr.hasNext()) {
                String proxyName = itr.next();
                try {
                    if (esbUtils.isProxyServiceExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyName)) {
                        esbUtils.deleteProxyService(amServer.getBackEndUrl(), amServer.getSessionCookie(), proxyName);

                        if (ExecutionEnvironment.stratos.name().equalsIgnoreCase(getExecutionEnvironment())) {
                            long deploymentDelay = FrameworkFactory.getFrameworkProperties(
                                    ProductConstant.AM_SERVER_NAME).getEnvironmentVariables().getDeploymentDelay();

                            Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                                    , "Proxy UnDeployment Synchronizing failed in workers");
                            Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                                    , "Proxy UnDeployment Synchronizing failed in workers");
                            Assert.assertTrue(isProxyWSDlNotExist(getProxyServiceURLHttp(proxyName), deploymentDelay)
                                    , "Proxy UnDeployment Synchronizing failed in workers");

                        }
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Proxy. " + e.getMessage());
                }
            }
            proxyServicesList.clear();
        }
    }

    private void deleteEndpoints() {
        if (endpointsList != null) {
            Iterator<String> itr = endpointsList.iterator();
            while (itr.hasNext()) {
                String endpoint = itr.next();
                try {
                    if (esbUtils.isEndpointExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), endpoint)) {
                        esbUtils.deleteEndpoint(amServer.getBackEndUrl(), amServer.getSessionCookie(), endpoint);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Endpoint. " + e.getMessage());
                }
            }
            endpointsList.clear();
        }
    }

    private void deleteLocalEntries() {
        if (localEntryList != null) {
            Iterator<String> itr = localEntryList.iterator();
            while (itr.hasNext()) {
                String localEntry = itr.next();
                try {
                    if (esbUtils.isLocalEntryExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntry)) {
                        esbUtils.deleteLocalEntry(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntry);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying LocalEntry. " + e.getMessage());
                }
            }
            localEntryList.clear();
        }
    }

    private void deleteSequenceTemplates() {
        if (sequenceTemplateList != null) {
            Iterator<String> itr = sequenceTemplateList.iterator();
            while (itr.hasNext()) {
                String localEntry = itr.next();
                try {
                    if (esbUtils.isSequenceTemplateExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntry)) {
                        esbUtils.deleteSequenceTemplate(amServer.getBackEndUrl(), amServer.getSessionCookie(), localEntry);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Sequence Template. " + e.getMessage());
                }
            }
            sequenceTemplateList.clear();
        }
    }

    private void deleteApi() {
        if (apiList != null) {
            Iterator<String> itr = apiList.iterator();
            while (itr.hasNext()) {
                String api = itr.next();
                try {
                    if (esbUtils.isApiExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), api)) {
                        esbUtils.deleteApi(amServer.getBackEndUrl(), amServer.getSessionCookie(), api);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Api. " + e.getMessage());
                }
            }
            apiList.clear();
        }
    }

    private void deletePriorityExecutors() {
        if (priorityExecutorList != null) {
            Iterator<String> itr = priorityExecutorList.iterator();
            while (itr.hasNext()) {
                String executor = itr.next();
                try {
                    if (esbUtils.isPriorityExecutorExist(amServer.getBackEndUrl(), amServer.getSessionCookie(), executor)) {
                        esbUtils.deleteProxyService(amServer.getBackEndUrl(), amServer.getSessionCookie(), executor);
                    }
                } catch (Exception e) {
                    Assert.fail("while undeploying Priority Executor. " + e.getMessage());
                }
            }
            priorityExecutorList.clear();
        }
    }

    protected boolean isRunningOnStratos() {
        return FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME).getEnvironmentSettings().is_runningOnStratos();
    }

    protected String getResourceLocation() {
        return ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME);
    }

    protected String getBackEndServiceUrl(String serviceName) {
        return EndpointGenerator.getEndpointServiceUrl(serviceName);
    }

    protected OMElement setEndpoints(OMElement synapseConfig) throws XMLStreamException {
        if (isBuilderEnabled()) {
            return synapseConfig;
        }
        String config = replaceEndpoints(synapseConfig.toString());
        return AXIOMUtil.stringToOM(config);
    }

    protected DataHandler setEndpoints(DataHandler dataHandler)
            throws XMLStreamException, IOException {

        String config = readInputStreamAsString(dataHandler.getInputStream());
        config = replaceEndpoints(config);
        ByteArrayDataSource dbs = new ByteArrayDataSource(config.getBytes());
        return new DataHandler(dbs);
    }

    protected String[] getUserRole(String userId) {
        if (Integer.parseInt(userId) <= 1) {
            return new String[]{ProductConstant.ADMIN_ROLE_NAME};
        } else {
            return new String[]{ProductConstant.DEFAULT_PRODUCT_ROLE};
        }

    }

    public boolean isBuilderEnabled() {
        return FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME).getEnvironmentSettings().is_builderEnabled();
    }

    private boolean isClusterEnabled() {
        return FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME).getEnvironmentSettings().isClusterEnable();
    }

    private String getExecutionEnvironment() {
        return FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME).getEnvironmentSettings().executionEnvironment();
    }

    private boolean isProxyWSDlExist(String serviceUrl, long synchronizingDelay)
            throws Exception {
        return new ServiceDeploymentUtil().isServiceWSDlExist(serviceUrl, synchronizingDelay);

    }

    private boolean isProxyWSDlNotExist(String serviceUrl, long synchronizingDelay)
            throws Exception {

        return new ServiceDeploymentUtil().isServiceWSDlNotExist(serviceUrl, synchronizingDelay);

    }

    private String replaceEndpoints(String config) {
        String service = getBackEndServiceUrl("");

        config = config.replace("http://localhost:9000/services/"
                , service);
        config = config.replace("http://127.0.0.1:9000/services/"
                , service);
        return config;
    }

    private String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    private String getServerBackendUrlHttp() {
        FrameworkProperties frameworkProperties = FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME);
        boolean webContextEnabled = frameworkProperties.getEnvironmentSettings().isEnableCarbonWebContext();
        boolean portEnabled = frameworkProperties.getEnvironmentSettings().isEnablePort();

        ProductVariables amServerInfo = frameworkProperties.getProductVariables();
        String webContextRoot = amServerInfo.getWebContextRoot();
        String httpPort = amServerInfo.getHttpPort();
        String hostName = amServerInfo.getHostName();

        String url = "http://" + hostName;
        if (portEnabled && httpPort != null) {
            url = url + ":" + httpPort;
        }
        if (webContextEnabled && webContextRoot != null) {
            url = url + "/" + webContextRoot;
        }

        return url;
    }

    protected String getServerBackendUrlHttps() {
        FrameworkProperties frameworkProperties = FrameworkFactory.getFrameworkProperties(ProductConstant.AM_SERVER_NAME);
        boolean webContextEnabled = frameworkProperties.getEnvironmentSettings().isEnableCarbonWebContext();
        boolean portEnabled = frameworkProperties.getEnvironmentSettings().isEnablePort();

        ProductVariables amServerInfo = frameworkProperties.getProductVariables();
        String webContextRoot = amServerInfo.getWebContextRoot();
        String httpsPort = amServerInfo.getHttpsPort();
        String hostName = amServerInfo.getHostName();

        String url = "https://" + hostName;
        if (portEnabled && httpsPort != null) {
            url = url + ":" + httpsPort;
        }
        if (webContextEnabled && webContextRoot != null) {
            url = url + "/" + webContextRoot;
        }

        return url;
    }

    protected String getStoreServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.store.http.url").toString();
    }

    protected String getSSOProvider() {
        return ProductUrlGeneratorUtil.prop.get("ssoProvider").toString();
    }

    protected String getStoreServerURLHttps() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.store.https.url").toString();
    }

    protected String getPublisherServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.publisher.http.url").toString();
    }

    protected String getPublisherServerURLHttps() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.publisher.https.url").toString();
    }

    protected String getGatewayServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.gateway.http.url").toString();
    }

    protected String getGatewayServerURLHttps() {
        return ProductUrlGeneratorUtil.prop.get("appm.distributed.gateway.https.url").toString();
    }

    protected String getTestApplicationUsagePublisherServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.test.application.usage.http.url").toString();
    }

    protected String getTestApplicationStoreServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.test.application.store.http.url").toString();
    }

    protected String getTestApplicationPublisherServerURLHttp() {
        return ProductUrlGeneratorUtil.prop.get("appm.test.application.publisher.http.url").toString();
    }

    protected String getNetworkIPAddress() {
        String networkIpAddress = null;
        try {
            String localhost = InetAddress.getLocalHost().getHostAddress();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e.nextElement();
                if (ni.isLoopback())
                    continue;
                if (ni.isPointToPoint())
                    continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        if (!ip.equals(localhost)) {
                            networkIpAddress = ip;
                        }

                    }
                }
            }
        } catch (UnknownHostException e) {
            log.error("Error occurred due to an unknown host.", e);
        } catch (SocketException e) {
            log.error("Error occurred with Socket connections", e);
        }
        return networkIpAddress;
    }

    protected AppCreateRequest createSingleApp(String appName, String appDisplayName, String version, String transport,
                                               String hostURL, int hostPort, String tier, String partialId, String policyGroupId)
            throws Exception {

        AppCreateRequest appRequest = new AppCreateRequest();

        String webAppUrl = hostURL + ":" + hostPort + appProp.getPath();

        appRequest.setOverview_webAppUrl(webAppUrl);
        appRequest.setOverview_transports(transport);
        appRequest.setOverview_name(appName);
        appRequest.setOverview_displayName(appDisplayName);
        appRequest.setOverview_version(version);
        appRequest.setOverview_context("/" + appName);
        appRequest.setEntitlementPolicies("[]");
        appRequest.setUritemplate_policyPartialIds("["+partialId+"]");

        appRequest.setUritemplate_tier0(tier);
        appRequest.setUritemplate_tier1(tier);
        appRequest.setUritemplate_tier2(tier);
        appRequest.setUritemplate_tier3(tier);
        appRequest.setUritemplate_tier4(tier);

        appRequest.setUritemplate_userRoles0(appProp.getRole());
        appRequest.setUritemplate_userRoles1(appProp.getRole());
        appRequest.setUritemplate_userRoles2(appProp.getRole());
        appRequest.setUritemplate_userRoles3(appProp.getRole());
        appRequest.setUritemplate_userRoles4(appProp.getRole());

        appRequest.setUritemplate_httpVerb4("GET");
        appRequest.setUritemplate_urlPattern4("/images");

        appRequest.setUritemplate_entitlementPolicyPartialMappings4
                ("[{\"entitlementPolicyPartialId\":"+partialId+",\"effect\":\"Permit\"}]");
        appRequest.setUritemplate_entitlementPolicyPartialMappings3("[]");
        appRequest.setUritemplate_entitlementPolicyPartialMappings2("[]");
        appRequest.setUritemplate_entitlementPolicyPartialMappings1("[]");
        appRequest.setUritemplate_entitlementPolicyPartialMappings0("[]");


        appRequest.setTags(appProp.getTags());

        appRequest.setAutoConfig("on");

        appRequest.setProviders(getSSOProvider());
        appRequest.setSso_ssoProvider(getSSOProvider());

        appRequest.setSso_singleSignOn("Enabled");
        appRequest.setClaims("http://wso2.org/claims/role");
        appRequest.setClaimPropertyCounter("1");
        appRequest.setSso_idpProviderUrl("http://localhost:9773/samlsso/");

        appRequest.setUritemplate_policygroupid0(policyGroupId);
        appRequest.setUritemplate_policygroupid1(policyGroupId);
        appRequest.setUritemplate_policygroupid2(policyGroupId);
        appRequest.setUritemplate_policygroupid3(policyGroupId);
        appRequest.setUritemplate_policygroupid4(policyGroupId);

        appRequest.setUritemplate_policyGroupIds("[" + policyGroupId + "]");
        appRequest.setUritemplate_javaPolicyIds("[1]");

        return appRequest;
    }

    public String convertXMLFileToString(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * this method validate the method
     *
     * @param policyPartial
     * @return
     * @throws Exception
     */
    public HttpResponse validatePolicy(String policyPartial) throws Exception {

        String payLoad = "policyPartial=" + URLEncoder.encode(policyPartial, "UTF-8");

        this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response =
                HttpRequestUtil.doPost(new URL(amServer.getBackEndUrl() +
                        "/publisher/api/entitlement/policy/validate"), payLoad,
                        requestHeaders);
        if (response.getResponseCode() == 200) {

            return response;
        } else {
            System.out.println(response);
            throw new Exception("App creation failed> " + response.getData());
        }
    }

    /**
     * this method to save  the method
     *
     * @param policyPartialName
     * @param policyPartial
     * @return
     * @throws Exception
     */
    public HttpResponse savePolicy(String policyPartialName, String policyPartial) throws Exception {

        String payLoad = "policyPartialName=" + URLEncoder.encode(policyPartialName, "UTF-8") + "&policyPartial=" +
                URLEncoder.encode(policyPartial, "UTF-8");

        this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response =
                HttpRequestUtil.doPost(new URL(amServer.getBackEndUrl() +
                        "/publisher/api/entitlement/policy/partial/save"), payLoad,
                        requestHeaders);
        if (response.getResponseCode() == 200) {

            // if ok == false this will return an exception then test fail!
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            System.out.println(response);
            throw new Exception("App creation failed> " + response.getData());
        }
    }


}
