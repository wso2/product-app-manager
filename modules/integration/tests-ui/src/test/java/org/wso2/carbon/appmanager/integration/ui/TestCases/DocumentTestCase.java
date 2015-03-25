/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.DocumentRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class contains test cases for adding,updating,deleting and check existence of documents.
 */
public class DocumentTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private String appName;
    private String appVersion;
    private APPMPublisherRestClient appMPublisher;
    private DocumentRequest docReq;
    private ResourceAdminServiceClient resourceAdminServiceStub;
    private String docPathPrefix;
    private WebDriver driver;

    private static final String IN_LINE_DOC_PREFIX = "InlineTestDocument";
    private static final String URL_DOC_PREFIX = "URLTestDocument";
    private static final String FILE_DOC_PREFIX = "FileTestDocument";
    private static final String HOW_TO = "how to";
    private static final String SAMPLES_AND_SDK = "samples";
    private static final String PUBLIC_FORUM = "public forum";
    private static final String SUPPORT_FORUM = "support forum";
    private static final String OTHER = "other";


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        ApplicationInitializingUtil baseUtil;
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.testApplicationCreation("doc");
        baseUtil.testApplicationPublish();
        appName = ApplicationInitializingUtil.appName;
        appVersion = ApplicationInitializingUtil.version;
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMPublisher = new APPMPublisherRestClient(getServerURLHttp());

        appMPublisher.login(username, password);
        docReq = new DocumentRequest(appName, appVersion);
        docPathPrefix = "/_system/governance/appmgt/applicationdata/provider/" + username
                + "/" + appName + "/" + appVersion + "/documentation/";
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(amServer.getBackEndUrl(), amServer.getSessionCookie());
    }

    @Test(groups = {"wso2.appmanager.document"}, description = "add and update \"how to\" " +
            "type documentation test")
    public void addAndUpdateHowToTypeDoc() throws Exception {
        //add and update "how to-inline" doc
        String docType = HOW_TO;
        String docName = IN_LINE_DOC_PREFIX + docType;
        String filePath = ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "documentTestCase/test1.jag";

        //add inline document
        addInlineDocument(docName, docType);
        //add inline document content
        String content = "test content";
        addInlineDocumentContent(docName, content);
        updateInlineDocument(docName, docType);
        checkInlineDocInRegistry(docName, content);

        //add and update "how to-URL" doc
        docName = URL_DOC_PREFIX + docType;
        addURLDocument(docName, docType, "http://www.howtotest.com");
        updateURLDocument(docName, docType, "http://www.howtotestupdated.com");
        checkURLDocInRegistry(docName);

        //add and update "how to-file" doc
        docName = FILE_DOC_PREFIX + docType;
        addFileDocument(docName, docType, filePath);
        updateFileDocument(docName, docType, filePath);
        checkFileDocInRegistry(docName, "test1.jag", filePath);
    }

    @Test(groups = {"wso2.appmanager.document"}, description = "add and update \"Samples&SDK\" " +
            "type documentation test")
    public void addAndUpdateSamplesTypeDoc() throws Exception {
        //add and update"Samples & SDK" -inline type docs
        String docType = SAMPLES_AND_SDK;
        String docName = IN_LINE_DOC_PREFIX + docType;
        String filePath = ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "documentTestCase/test2.jag";

        addInlineDocument(docName, docType);
        //add inline document content
        String content = "test content";
        addInlineDocumentContent(docName, content);
        updateInlineDocument(docName, docType);
        checkInlineDocInRegistry(docName, content);

        //add and update "Samples & SDK" -url type docs
        docName = URL_DOC_PREFIX + docType;
        addURLDocument(docName, docType, "http://www.sampletest.com");
        updateURLDocument(docName, docType, "http://www.sampletestupdated.com");
        checkURLDocInRegistry(docName);

        //add and update "Samples & SDK" -file type docs
        docName = FILE_DOC_PREFIX + docType;
        addFileDocument(docName, docType, filePath);
        updateFileDocument(docName, docType, filePath);
        checkFileDocInRegistry(docName, "test2.jag", filePath);
    }

    @Test(groups = {"wso2.appmanager.document"}, description = "add and update \"public forum\" type " +
            "documentation test")
    public void addAdnUpdatePublicForumDoc() throws Exception {
        String docType = PUBLIC_FORUM;
        String docName = URL_DOC_PREFIX + docType;
        addURLDocument(docName, docType, "http://www.publichforum.com");
        updateURLDocument(docName, docType, "http://www.publichforumupdated.com");
        checkURLDocInRegistry(docName);
    }

    @Test(groups = {"wso2.appmanager.document"}, description = "add and update \"support forum\" type" +
            " documentation test")
    public void addAndUpdateSupportForumDoc() throws Exception {
        String docType = SUPPORT_FORUM;
        String docName = URL_DOC_PREFIX + docType;
        addURLDocument(docName, docType, "http://www.supportforum.com");
        updateURLDocument(docName, docType, "http://www.supportforumudpated.com");
        checkURLDocInRegistry(docName);
    }

    @Test(groups = {"wso2.appmanager.document"}, description = "add and update \"other\" type documentation test")
    public void addAndUpdateOtherTypeDoc() throws Exception {

        String docType = OTHER;
        String docName = IN_LINE_DOC_PREFIX + docType;
        String filePath = ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "documentTestCase/test3.jag";

        //add & update "other-inline"  doc
        addInlineDocument(docName, docType);
        //add inline document content
        String content = "test content";
        addInlineDocumentContent(docName, content);
        updateInlineDocument(docName, docType);
        checkInlineDocInRegistry(docName, content);

        //add & update "other-url"  doc
        docName = URL_DOC_PREFIX + docType;
        addURLDocument(docName, docType, "www.google.com");
        updateURLDocument(docName, docType, "www.updateddurl.com");
        checkURLDocInRegistry(docName);

        //add & update "other-file"  doc
        docName = FILE_DOC_PREFIX + docType;
        addFileDocument(docName, docType, filePath);
        updateFileDocument(docName, docType, filePath);
        checkFileDocInRegistry(docName, "test3.jag", filePath);
    }


    @Test(groups = {"wso2.appmanager.document"}, dependsOnMethods = {"addAndUpdateOtherTypeDoc",
            "addAndUpdateSupportForumDoc", "addAdnUpdatePublicForumDoc", "addAndUpdateHowToTypeDoc",
            "addAndUpdateSamplesTypeDoc"}, description = "check the added documents in store")
    public void checkAvailabilityInStore() throws Exception {
        driver = BrowserManager.getWebDriver();
        String appId = ApplicationInitializingUtil.appId;
        APPMStoreUIClient storeUIClient = new APPMStoreUIClient();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        //login
        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        //wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Web Applications")));
        driver.findElement(By.linkText("Web Applications")).click();
        // There is delay  for app to appear in store
        long timeBefore = System.nanoTime();
        long timeElapsed = 0;
        while (true) {
            Thread.sleep(1000);
            if (driver.getPageSource().contains(appId)) {
                driver.findElement(By.cssSelector("a[href*='" + appId + "']")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Documentation")));
                driver.findElement(By.linkText("Documentation")).click();

                // check the availability of doc in store by their name
                // check "how to" type docs
                String errorMessage = " is not visible in store";
                String docName = IN_LINE_DOC_PREFIX + HOW_TO;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = URL_DOC_PREFIX + HOW_TO;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = FILE_DOC_PREFIX + HOW_TO;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                //check samples&sdk type docs
                docName = IN_LINE_DOC_PREFIX + SAMPLES_AND_SDK;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = URL_DOC_PREFIX + SAMPLES_AND_SDK;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = FILE_DOC_PREFIX + SAMPLES_AND_SDK;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                //check "other" type docs
                docName = IN_LINE_DOC_PREFIX + OTHER;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = URL_DOC_PREFIX + OTHER;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                docName = FILE_DOC_PREFIX + OTHER;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                //check "public forum" type doc
                docName = URL_DOC_PREFIX + PUBLIC_FORUM;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                //check "private forum" type doc
                docName = URL_DOC_PREFIX + SUPPORT_FORUM;
                assertTrue(driver.getPageSource().contains(docName), docName + errorMessage);
                break;
            } else if (timeElapsed >= 6.0e+10) {
                assertTrue(false, "Application " + appName + " is not visible in store");
            }
            timeElapsed = System.nanoTime() - timeBefore;
            driver.navigate().refresh();
        }

    }


    @Test(groups = {"wso2.appmanager.document"}, dependsOnMethods = {"checkAvailabilityInStore"})
    public void deleteDocumentTestCase() throws Exception {
        //delete "how to" type doc
        String docType = HOW_TO;
        String docName = IN_LINE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = URL_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = FILE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);

        //delete "samples&sdk" type doc
        docType = SAMPLES_AND_SDK;
        docName = IN_LINE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = URL_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = FILE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);

        //delete "other" type doc
        docType = OTHER;
        docName = IN_LINE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = URL_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
        docName = FILE_DOC_PREFIX + docType;
        deleteDocument(docName, docType);

        //delete "public forum" type doc
        docType = PUBLIC_FORUM;
        docName = URL_DOC_PREFIX + docType;
        deleteDocument(docName, docType);

        //delete "support forum" type doc
        docType = SUPPORT_FORUM;
        docName = URL_DOC_PREFIX + docType;
        deleteDocument(docName, docType);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        if (driver != null) driver.quit();
    }

    /**
     * This method checks the existence of Inline document artifact and its content in registry
     * if artifact or its content is not found in the given path registry exception will be thrown
     *
     * @param docName    Document Name
     * @param docContent Content of the document
     * @throws Exception
     */
    private void checkInlineDocInRegistry(String docName, String docContent) throws Exception {
        // check for existence of doc artifact
        resourceAdminServiceStub.getResource(docPathPrefix + docName);
        // check for the content
        String content = resourceAdminServiceStub.getTextContent(docPathPrefix + "contents/" + docName);
        boolean success = (content != null && content.equals(docContent));
        assertTrue(success, "Content mismatch for doc:" + docName);
    }

    /**
     * This method checks the existence of URl document artifact
     * if artifact is not found in the given path registry exception will be thrown
     *
     * @param docName Document Name
     * @throws Exception
     */
    private void checkURLDocInRegistry(String docName) throws Exception {
        resourceAdminServiceStub.getResource(docPathPrefix + docName);
        assertTrue(true);
    }

    /**
     * This method checks the existence of File document artifact and its file in registry
     * if artifact or its file is not found in the given path registry exception will be thrown
     * if file exist check for its content.
     *
     * @param docName  Document Name
     * @param fileName File Name
     * @param filePath File path
     * @throws Exception
     */
    private void checkFileDocInRegistry(String docName, String fileName, String filePath) throws Exception {
        resourceAdminServiceStub.getResource(docPathPrefix + docName);
        String fileContent = resourceAdminServiceStub.getTextContent(docPathPrefix + "files/" + fileName);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine);
            }
            String content = sb.toString();
            assertTrue(content.equals(fileContent), "File content mismatch fo doc:" + docName);
        } finally {
            if (br != null) br.close();
        }

    }

    /**
     * This method adds content for Inline document
     *
     * @param docName Document Name
     * @param content Document Content
     * @throws Exception
     */
    private void addInlineDocumentContent(String docName, String content) throws Exception {
        HttpResponse response = appMPublisher.addInlineDocumentContent(appName, appVersion, username, docName, content);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while adding content to inline doc:" + docName);
    }

    /**
     * This method adds new Inline document
     *
     * @param docName Document Name
     * @param docType Document Type("how to","samples","other")
     * @throws Exception
     */
    private void addInlineDocument(String docName, String docType) throws Exception {
        docReq.setMode("");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("inline");
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocInline");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while adding doc:" + docName + " of type :" + docType);
    }

    /**
     * This method adds URL document
     *
     * @param docName Document Name
     * @param docType Document Type("how to","samples","other","public forum","support forum")
     * @param url     URL
     * @throws Exception
     */
    private void addURLDocument(String docName, String docType, String url) throws Exception {
        docReq.setMode("");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("url");
        docReq.setDocUrl(url);
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocURL");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while adding doc:" + docName + " of type :" + docType);
    }

    /**
     * This method add File Document
     *
     * @param docName  Document Name
     * @param docType  Document Type("how to","samples","other")
     * @param filePath File path
     * @throws Exception
     */
    private void addFileDocument(String docName, String docType, String filePath) throws Exception {
        docReq.setMode("");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("file");
        docReq.setDocLocation(filePath);
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocFile");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while adding doc:" + docName + " of type :" + docType);
    }

    /**
     * Update the Inline document
     *
     * @param docName Document Name
     * @param docType Document Type("how to","samples","other")
     * @throws Exception
     */
    private void updateInlineDocument(String docName, String docType) throws Exception {
        docReq.setMode("Update");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("inline");
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocInline");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while updating doc:" + docName + " of type :" + docType);
    }

    /**
     * update the URL document
     *
     * @param docName Document Name
     * @param docType Document Type("how to","samples","other","public forum","support forum")
     * @param url     URL
     * @throws Exception
     */
    private void updateURLDocument(String docName, String docType, String url) throws Exception {
        docReq.setMode("Update");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("url");
        docReq.setDocUrl(url);
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocURL");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while updating doc:" + docName + " of type :" + docType);
    }

    /**
     * update the File Document
     *
     * @param docName  Document Name
     * @param docType  Document Type("how to","samples","other")
     * @param filePath File path
     * @throws Exception
     */
    private void updateFileDocument(String docName, String docType, String filePath) throws Exception {
        docReq.setMode("Update");
        docReq.setDocName(docName);
        docReq.setDocType(docType);
        docReq.setSourceType("file");
        docReq.setSummary("New summary");
        docReq.setDocLocation(filePath);
        if (OTHER.equalsIgnoreCase(docType)) {
            docReq.setNewType("TestDocFile");
        }
        HttpResponse response = appMPublisher.addDocument(docReq);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while updating doc:" + docName + " of type :" + docType);
    }

    /**
     * Delete the document
     *
     * @param docName Document Name
     * @param docType Document Type("how to","samples","other","public forum","support forum")
     * @throws Exception
     */
    private void deleteDocument(String docName, String docType) throws Exception {
        HttpResponse response = appMPublisher.deleteDocument(appName, appVersion, username, docName, docType);
        JSONObject responseObj = new JSONObject(response.getData());
        assertFalse((Boolean) responseObj.get("error"), "Error while deleting doc:" + docName + " of type :" + docType);
    }
}
