/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appmanager.integration.test.cases.publisher.webapp.audit.logs;

import org.testng.annotations.*;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.WebAppUtil;
import org.wso2.appmanager.integration.utils.bean.PolicyGroup;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class PublisherAuditLogsTestCase extends AppMIntegrationBaseTest {
    ServerConfigurationManager serverConfigurationManager;

    private String backEndUrl;
    private static AutomationContext appMServer;
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "PublisherAuditLogsTestCase";
    private String adminUserName;
    private String adminPassword;
    private String appVersion = "1.0.0";
    private String contextPrefix = "/";
    private String carbonHome;
    private int tenantId;
    private String auditLogFileLocation;
    private WebApp webApp;
    private String log4jPropertiesFileLocation;
    private String originalLog4jPropertiesFileContent;


    @Factory(dataProvider = "userModeDataProvider")
    public PublisherAuditLogsTestCase(TestUserMode userMode) {
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
            carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
            appendAuditLogsToLog4jProperties();
            serverConfigurationManager.restartGracefully();
        }
    }

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        User adminUser = appMServer.getSuperTenant().getTenantAdmin();
        adminUserName = adminUser.getUserName();
        adminPassword = adminUser.getPassword();
        tenantId = -1234;
        auditLogFileLocation = carbonHome + AppmTestConstants.AUDIT_LOGS_FILE_PATH;
        appmPublisherRestClient.login(adminUserName, adminPassword);
        String appId = createWebApp(appName);
        appmPublisherRestClient.editApp(webApp);
        appmPublisherRestClient.changeState(appId, AppmTestConstants.LifeCycleStatus
                .SUBMIT_FOR_REVIEW);
        appmPublisherRestClient.deleteApp(appId);
        appmPublisherRestClient.logout();
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Publisher Login Audit logs Test Case")
    public void testPublisherLoginAuditLogs() throws Exception {

        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserLoginAuditLogExists = fileContent.contains(
                buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.USER_LOGGED_IN, "", ""));
        assertTrue(isUserLoginAuditLogExists);

    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Create Audit logs Test Case")
    public void testAppCreateAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppCreateAuditLogExists = fileContent.contains(
                buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.NEW_ASSET_ADDED,
                        AppmTestConstants.WEB_APP,getAuditLogSubjectId()));
        assertTrue(isUserAppCreateAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Update Audit logs Test Case")
    public void testAppUpdateAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppUpdateAuditLogExists =
                fileContent.contains(buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.ASSET_UPDATED,
                        AppmTestConstants.WEB_APP, getAuditLogSubjectId()));
        assertTrue(isUserAppUpdateAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Lifecycle Change Audit logs Test Case")
    public void testAppLifecycleStateChangeAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppUpdateAuditLogExists =
                fileContent.contains(buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.LIFE_CYCLE_ACTION_PERFORMED +
                                AppmTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW, AppmTestConstants.WEB_APP,
                        getAuditLogSubjectId()));
        assertTrue(isUserAppUpdateAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : Webapp Delete Audit logs Test Case")
    public void testAppDeleteAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppDeleteAuditLogExists =
                fileContent.contains(buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.ASSET_DELETED,
                        AppmTestConstants.WEB_APP,getAuditLogSubjectId()));
        assertTrue(isUserAppDeleteAuditLogExists);
    }

    @Test(groups = {"wso2.appm"}, description = "AUDIT Logs : User logout Audit logs Test Case")
    public void testUserLogoutAuditLogs() throws Exception {
        String fileContent = FileManager.readFile(auditLogFileLocation);
        boolean isUserAppDeleteAuditLogExists =
                fileContent.contains(buildAuditLogs(adminUserName, AppmTestConstants.AuditLogActions.USER_LOGGED_OUT,
                        "Logout", ""));
        assertTrue(isUserAppDeleteAuditLogExists);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
    }

    private void appendAuditLogsToLog4jProperties() throws IOException {
        String auditLogsLog4jPropertiesFilePath = (new File(System.getProperty("user.dir"))).getParent() +
                File.separator + "src" + File.separator + "test" + File.separator +
                "resources" + File.separator + "audit-logs" + File.separator + "log4j.properties";
        log4jPropertiesFileLocation = carbonHome + AppmTestConstants.LOG4J_PROPERTY_FILE_PATH;
        originalLog4jPropertiesFileContent = FileManager.readFile(log4jPropertiesFileLocation);
        String auditLogsLog4jPropertiesFileContent = FileManager.readFile(auditLogsLog4jPropertiesFilePath);
        FileManager.writeToFile(log4jPropertiesFileLocation, originalLog4jPropertiesFileContent +
                auditLogsLog4jPropertiesFileContent);
    }

    private void removeAuditLogsFromLog4jProperties() throws Exception {
        FileManager.writeToFile(log4jPropertiesFileLocation, originalLog4jPropertiesFileContent);
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

    private String buildAuditLogs(String username, String action, String subject, String subjectId) {
        String auditLog = "\"Action\" : \"" + action +
                " \",\"TenantID\" : \"" + tenantId + "\",\"UserName\" : \"" +
                username + " \", \"Subject\" : \"" +
                subject + "\", \"SubjectID\" : \"" + subjectId + "\"";
        return auditLog;
    }

    private String getAuditLogSubjectId() {
        return "{providerName='" + adminUserName + "', apiName='" + appName + "', version='" + appVersion + "'}";
    }

}
