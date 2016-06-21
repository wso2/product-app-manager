/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.appmanager.integration.test.cases.store.webapp.audit.logs;

import org.testng.annotations.*;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.APPMStoreRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.WebAppUtil;
import org.wso2.appmanager.integration.utils.bean.PolicyGroup;
import org.wso2.appmanager.integration.utils.bean.SubscriptionRequest;
import org.wso2.appmanager.integration.utils.bean.WebApp;
import org.wso2.appmanager.integration.utils.bean.WebAppResource;
import org.wso2.appmanager.integration.utils.restapi.base.AppMIntegrationBaseTest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.utils.FileManager;
import org.wso2.carbon.integration.common.utils.mgt.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.assertTrue;

public class StoreAuditLogsTestCase extends AppMIntegrationBaseTest {
    ServerConfigurationManager serverConfigurationManager;

    private String backEndUrl;
    private static AutomationContext appMServer;
    private APPMStoreRestClient appmStorerRestClient;
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "StoreAuditLogsTestCase";
    private String adminUserName;
    private String adminPassword;
    private String appVersion = "1.0.0";
    private String contextPrefix = "/";
    private String carbonHome;
    private int tenantId;
    private String tenantAwareUsername;
    private String auditLogFileLocation;
    private WebApp webApp;


    @Factory(dataProvider = "userModeDataProvider")
    public StoreAuditLogsTestCase(TestUserMode userMode) {
        this.userMode = userMode;
    }

    @DataProvider
    public static Object[][] userModeDataProvider() {
        return new Object[][]{
                new Object[]{TestUserMode.SUPER_TENANT_ADMIN}
        };
    }

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init(userMode);
        if (TestUserMode.SUPER_TENANT_ADMIN == userMode) {
            serverConfigurationManager = new ServerConfigurationManager(publisherContext);
            serverConfigurationManager.restartGracefully();
        }
        carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        File log4jProperties = new File(carbonHome + File.separator + "repository" + File.separator + "conf" +
                File.separator + "log4j.properties");
        applyProperty(log4jProperties, "log4j.logger.org.apache.synapse.transport.http.wire", "DEBUG");

    }

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmStorerRestClient = new APPMStoreRestClient(backEndUrl);
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        User adminUser = appMServer.getSuperTenant().getTenantAdmin();
        adminUserName = adminUser.getUserName();
        adminPassword = adminUser.getPassword();
        tenantId = -1234;
        auditLogFileLocation = carbonHome + File.separator + "repository" + File.separator + "logs" +
                File.separator + "audit.log";
        tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(adminUserName);
        appmPublisherRestClient.login(adminUserName, adminPassword);
        String appId = createWebApp(appName);
        appmPublisherRestClient.publishWebApp(appId);
        appmPublisherRestClient.logout();
        appmStorerRestClient.login(adminUserName, adminPassword);
        SubscriptionRequest adminSubscriptionRequest = new SubscriptionRequest(appName, adminUserName,
                appVersion);
        appmStorerRestClient.subscribeForApplication(adminSubscriptionRequest);
        appmStorerRestClient.unsubscribeForApplication(adminSubscriptionRequest);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Store Login Audit logs Test Case")
    public void testPublisherLoginAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(carbonHome + File.separator + "repository" + File.separator + "logs" +
                File.separator + "audit.log");
//        boolean isUserLoginAuditLogExists = fileContent.contains("INFO -  Initiator : admin@carbon.super | Action : Login");
//        assertTrue(isUserLoginAuditLogExists);

    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Subscription Audit logs Test Case")
    public void testAppCreateAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppCreateAuditLogExists =
                fileContent.contains(buildAuditLogs("NewAssetAdded", AppmTestConstants.WEB_APP, getAuditLogSubjectId()));
        assertTrue(isUserAppCreateAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Unsubscription Audit logs Test Case")
    public void testAppUpdateAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppUpdateAuditLogExists =
                fileContent.contains(buildAuditLogs("AssetUpdated", AppmTestConstants.WEB_APP, getAuditLogSubjectId()));
        assertTrue(isUserAppUpdateAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Delete Audit logs Test Case")
    public void testAppDeleteAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppDeleteAuditLogExists =
                fileContent.contains(buildAuditLogs("AssetDeleted", AppmTestConstants.WEB_APP, getAuditLogSubjectId()));
        assertTrue(isUserAppDeleteAuditLogExists);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        //super.cleanUp();
        if (TestUserMode.SUPER_TENANT_ADMIN == userMode) {
            serverConfigurationManager.restoreToLastConfiguration();
        }
    }

    /**
     * Apply the given property and restart the server to
     *
     * @param srcFile
     * @param key
     * @param value
     * @throws Exception
     */
    private void applyProperty(File srcFile, String key, String value) throws Exception {
        File destinationFile = new File(srcFile.getName());
        Properties properties = new Properties();
        properties.load(new FileInputStream(srcFile));
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(destinationFile), null);
        serverConfigurationManager.applyConfigurationWithoutRestart(destinationFile);
    }

    public String createWebApp(String appName) throws Exception {

        PolicyGroup defaultPolicyGroup = WebAppUtil.createDefaultPolicy();
        HttpResponse response = appmPublisherRestClient.addPolicyGroup(defaultPolicyGroup);
        String policyId = WebAppUtil.getPolicyId(response);
        List<WebAppResource> webAppResources = WebAppUtil.createDefaultResources(policyId);
        webApp = WebAppUtil.createBasicWebApp(adminUserName, appName, contextPrefix + appName, appVersion,
                "http://wso2.com/", webAppResources);
        appmPublisherRestClient.createWebApp(webApp);
        return webApp.getAppId();
    }

    private String buildAuditLogs(String action, String subject, String subjectId) {
        String auditLog = "\"Action\" : \"" + action +
                " \",\"TenantID\" : \"" + tenantId + "\",\"UserName\" : \"" +
                tenantAwareUsername + " \", \"Subject\" : \"" +
                subject + "\", \"SubjectID\" : \"" + subjectId + "\"";
        return auditLog;
    }

    private String getAuditLogSubjectId(){
        return  "{providerName='" + adminUserName + "', apiName='" + appName + "', version='" + appVersion + "'}";
    }

}
