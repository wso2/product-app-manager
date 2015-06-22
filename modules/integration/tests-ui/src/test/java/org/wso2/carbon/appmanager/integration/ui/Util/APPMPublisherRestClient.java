/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.Util;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.*;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class APPMPublisherRestClient {
	private String backEndUrl;
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private WebDriver driver;
    private Logger log = Logger.getLogger("APPMPublisherRestClient");


	public APPMPublisherRestClient(String backEndUrl) throws MalformedURLException {
		this.backEndUrl = backEndUrl;
		if (requestHeaders.get("Content-Type") == null) {
			this.requestHeaders.put("Content-Type", "application/json");
		}

	    //driver = BrowserManager.getWebDriver();
		driver = new FirefoxDriver();
		driver.get(backEndUrl + "/publisher/login");
	}

	/**
	 * logs in to the publisher
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String login(String userName, String password) throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
		// find element username
		WebElement usernameEle = driver.findElement(By.id("username"));
		// fill user name
		usernameEle.sendKeys(userName);
		// find element password
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
		WebElement passwordEle = driver.findElement(By.id("password"));
		// fill element
		passwordEle.sendKeys(password);
		// find submit button and click on it.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary")));
		driver.findElement(By.className("btn-primary")).click();
		// get the current
		String redirectedUrl = driver.getCurrentUrl();
		// parsing url
		URL aURL = new URL(redirectedUrl);

		Set<org.openqa.selenium.Cookie> allCookies = driver.manage().getCookies();
		for (org.openqa.selenium.Cookie loadedCookie : allCookies) {

			// get /publisher cookie
			if (loadedCookie.getPath().equals("/publisher/")) {
				this.setSession(loadedCookie.toString());
				 System.out.println(loadedCookie.toString());
			}
		}

		String urlPath = aURL.getPath();

		//driver.quit();

		if ((urlPath.equals("/publisher/assets/webapp/"))) {
			return "logged in";
		} else {
			return "not logged in";
		}

	}

    public  String logout() throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
                        "/publisher/logout"),"",
                requestHeaders);
        if (response.getResponseCode() == 200) {
            this.requestHeaders.clear();
            return "log out";
        }
        return "log out failed";
    }


	/**
	 * creating an application
	 * 
	 * @param appRequest
	 *            - to create the payload
	 * @return response
	 * @throws Exception
	 */
	public HttpResponse createApp(AppCreateRequest appRequest) throws Exception {
		String payload = appRequest.generateRequestParameters();
		String roles = appRequest.getRoles();
		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
		                                                       "/publisher/asset/webapp"), payload,
		                                               requestHeaders);
		if (response.getResponseCode() == 200) {

			// if ok == false this will return an exception then test fail!
			VerificationUtil.checkAppCreateRes(response);
			JSONObject jsonObject = new JSONObject(response.getData());
			String appId = (String) jsonObject.get("id");
			
			if(!roles.equals("")){
				this.addRole(roles, appId);
			}

            if(!appRequest.getSso_ssoProvider().equals("")){
                this.addSSOProvider(appRequest);
            }
			
			String tag = appRequest.getTags();
			if(!tag.equals("")){
				this.addNewTag(appId, tag);
			}
			return response;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}
	/**
	 * Get total subscriber count by the web applications
	 * @param getSubscriberCountByAppRequest payload to get the subscriber count by web applications
	 * @return HTTP Response with subscriber count
	 * @throws Exception
	 */
	public HttpResponse getSubscriberCountByApp(GetStatisticRequest getSubscriberCountByAppRequest) throws Exception {
		checkAuthentication();
		String payload = getSubscriberCountByAppRequest.generateRequestParameters();
		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl +
								"/publisher/api/assets/statistics/webapp/getSubscriberCountByAPIs/"), payload,
						requestHeaders);
		if (response.getResponseCode() == 200) {

			return response;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}

	/**
	 * Get subscribed web applications by store users
	 * @param getSubscribedAppsByUsersRequest payload to get the subscribed web applications by store users
	 * @return HTTP response with subscribed applications with users
	 * @throws Exception
	 */
	public HttpResponse getSubscribedAppsByUsers(GetStatisticRequest getSubscribedAppsByUsersRequest) throws Exception {
		checkAuthentication();
		String payload = getSubscribedAppsByUsersRequest.generateRequestParameters();
		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl +
								"/publisher/api/assets/statistics/webapp/getSubscribedAPIsByUsers/"), payload,
						requestHeaders);
		if (response.getResponseCode() == 200) {

			return response;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}

	/**
	 * Get web applications usage by store user
	 * @param getAppUsageByUserRequest payload ro get web application usage by user
	 * @return HTTP Response with web application usage
	 * @throws Exception
	 */
	public HttpResponse getAppUsageByUser(GetStatisticRequest getAppUsageByUserRequest) throws Exception {
		checkAuthentication();
		String payload = getAppUsageByUserRequest.generateRequestParameters();
		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl +
								"/publisher/api/assets/statistics/webapp/getAPIUsageByUser/"), payload,
						requestHeaders);
		if (response.getResponseCode() == 200) {

			return response;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}

	/**
	 * Get web application response time
	 * @param getAppResponseTimeRequest payload to get web application response by time
	 * @return HTTP Response with web application response time
	 * @throws Exception
	 */
	public HttpResponse getAppResponseTime(GetStatisticRequest getAppResponseTimeRequest) throws Exception {
		checkAuthentication();
		String payload = getAppResponseTimeRequest.generateRequestParameters();
		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl +
								"/publisher/api/assets/statistics/webapp/getAPIResponseTime/"), payload,
						requestHeaders);
		if (response.getResponseCode() == 200) {

			return response;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}

	public String getWebappTrackingId(String appId) throws Exception {
		checkAuthentication();
		HttpResponse response =
				HttpRequestUtil.doGet(backEndUrl +
						"/publisher/api/asset/webapp/" +
						appId, requestHeaders);
		if (response.getResponseCode() == 200) {
			JSONObject jsonObject = new JSONObject(response.getData());

			org.json.JSONArray jsonArray = new org.json.JSONArray( jsonObject.get("fields").toString());
			JSONObject trackingIdObj = new JSONObject(jsonArray.get(10).toString());
			return trackingIdObj.get("value").toString();

		} else {
			System.out.println(response);
			throw new Exception("Error occurred while retrieving tracking id of webapp with id :" + appId);
		}
	}

	/**
     * this method validate the method
     * @param policyPartial
     * @return
     * @throws Exception
     */
    public HttpResponse validatePolicy(String policyPartial) throws Exception {

        String payLoad = "policyPartial="+ URLEncoder.encode(policyPartial,"UTF-8");

        this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response =
                HttpRequestUtil.doPost(new URL(backEndUrl +
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
     * @param policyPartialName
     * @param policyPartial
     * @return
     * @throws Exception
     */
    public HttpResponse savePolicy(String policyPartialName,String policyPartial) throws Exception {

        String payLoad = "policyPartialName="+URLEncoder.encode(policyPartialName,"UTF-8")+"&policyPartial="+
                URLEncoder.encode(policyPartial,"UTF-8");

        this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response =
                HttpRequestUtil.doPost(new URL(backEndUrl +
                        "/publisher/api/entitlement/policy/partial/save"), payLoad,
                        requestHeaders);
        if (response.getResponseCode() == 200) {
            return response;
        } else {
            System.out.println(response);
            throw new Exception("App creation failed> " + response.getData());
        }
    }
 
	/**
	 * this method adds the roles to an application
	 * @param roles
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	private HttpResponse addRole(String roles, String appId) throws Exception {
		String role = roles;
		this.requestHeaders.put("Content-Type", "application/json");
		HttpResponse response =
		                        HttpUtil.doPost(new URL(backEndUrl + "/publisher/asset/webapp/id/" +
		                                                appId + "/permissions"),
		                                        "[{\"role\":\"" + role +
		                                                "\",\"permissions\":[\"GET\",\"PUT\",\"DELETE\",\"AUTHORIZE\"]}]",
		                                        requestHeaders);
		if (response.getResponseCode() == 200) {

			return response;
		} else {
			System.out.println(response);
			throw new Exception("Add role failed> " + response.getData());
		}
	}

	/**
	 * this method add the policy group for the app and return the policy group id
	 * @param policyPartial
	 * @param anonymousAccessToUrlPattern
	 * @param policyGroupName
	 * @param throttlingTier
	 * @param objPartialMappings
	 * @param policyGroupDesc
	 * @return policyGroupId
	 * @throws Exception
	 */
	public String savePolicyGroup(String policyPartial, String anonymousAccessToUrlPattern, String policyGroupName,
								  String throttlingTier, String objPartialMappings, String policyGroupDesc) throws Exception {

		String payLoad = "policyPartial="+ URLEncoder.encode(policyPartial,"UTF-8") +
				"anonymousAccessToUrlPattern=" + anonymousAccessToUrlPattern +
				"&policyGroupName=" + policyGroupName +
				"&throttlingTier=" + throttlingTier +
				"&objPartialMappings=" + objPartialMappings +
				"&policyGroupDesc=" + policyGroupDesc +
				"&userRoles";

		this.requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");

		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl +
								"/publisher/api/entitlement/policy/partial/policyGroup/save"), payLoad,
						requestHeaders);
		if (response.getResponseCode() == 200) {
			JSONObject saveObject = new JSONObject(response.getData());
			String policyGroupId = saveObject.optJSONObject("response").getString("id");
			return policyGroupId;
		} else {
			System.out.println(response);
			throw new Exception("App creation failed> " + response.getData());
		}
	}


	/**
     * this method adds the ssoprovider for the app
     * @param appCreateRequest
     * @return
     * @throws Exception
     */
    private HttpResponse addSSOProvider(AppCreateRequest appCreateRequest) throws Exception {

        String provider = appCreateRequest.getSso_ssoProvider();
        String logOutUrl = appCreateRequest.getOverview_logoutUrl();
        if (logOutUrl==null){
            logOutUrl = "";
        }
        String claims = appCreateRequest.getClaims();
        String appName = appCreateRequest.getOverview_name();
        String version = appCreateRequest.getOverview_version();
        String transport = appCreateRequest.getOverview_transports();
        String context = appCreateRequest.getOverview_context();
        String requestBody = "{\"provider\":\"" + provider +
                "\",\"logout_url\":\""+logOutUrl+
                "\",\"claims\":[\""+claims+"\"],\"app_name\":\""+appName+
                "\",\"app_verison\":\""+version+
                "\",\"app_transport\":\""+transport+
                "\",\"app_context\":\""+context+
                "\"}";
        System.out.println(requestBody);

        this.requestHeaders.put("Content-Type", "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(backEndUrl + "/publisher/api/sso/addConfig"),requestBody,
                        requestHeaders);
        if (response.getResponseCode() == 200) {

            return response;
        } else {
            System.out.println(response);
            throw new Exception("Add role failed> " + response.getData());
        }
    }

	/***
	 * publish an application which is in created state
	 * @param appId
	 *            - application id
	 * @return -response
	 * @throws Exception
	 */
	public HttpResponse publishApp(String appId,String appType) throws Exception {
		changeState(appId, "Submit%20for%20Review",appType);
		changeState(appId, "Approve",appType);
		HttpResponse response = changeState(appId, "Publish",appType);
		return response;
	}

    /***
     * unpublish an application which is in created state
     * @param appId
     *            - application id
     * @param  appType
     *             - type of the appliction
     * @return -response
     * @throws Exception
     */
    public HttpResponse unPublishApp(String appId,String appType) throws Exception {
        // publish to unpublish
        HttpResponse response = changeState(appId, "Unpublish",appType);
        return response;
    }

    /*
     * Application deletion request
     */

    public HttpResponse deleteApp(String appId) throws Exception{

        ///publisher/api/asset/delete/{type}/{id}

        //TODO delte the app and do gett. check for null
        this.requestHeaders.put("Content-Type","application/json");
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
                "/publisher/api/asset/delete/webapp/"+appId+""),"",requestHeaders);

        if (response.getResponseCode() == 200) {
            return response;
        } else {
            throw new Exception("App deletion failed>" + response.getData());
        }
    }





	/**
	 * this method gives  the current life cycle state of the application	 
	 * @param appId -application id	
	 * @return -response 
	 * @throws Exception
	 */
	public HttpResponse getCurrentState(String appId,String  appType) throws Exception {
        HttpResponse response =
		                        HttpRequestUtil.doGet(backEndUrl +
		                                              "/publisher/api/lifecycle/subscribe/"+appType+"/" +
		                                              appId, requestHeaders);
		if (response.getResponseCode() == 200) {

			// if subscribed == true this will return an exception then test
			VerificationUtil.checkCurrentAppState(response);
			return response;
		} else {
			System.out.println(response);
			throw new Exception("Get current state  failed> " + response.getData());
		}
	}

	/***
	 * change the life cycle state from current state to next state	 * 
	 * @param appId
	 * @param toSate
	 * @return
	 * @throws Exception
	 */
	public HttpResponse changeState(String appId, String toSate,String appType) throws Exception {
		this.requestHeaders.put("Content-Type", "");
		HttpResponse response =
		                        HttpUtil.doPut(new URL(backEndUrl + "/publisher/api/lifecycle/" +
		                                               toSate + "/"+appType+"/" + appId), "",
		                                       requestHeaders);

		if (response.getResponseCode() == 200) {
			// if status != ok this will return an exception then test fail!
			VerificationUtil.checkAppStateChange(response);
			return response;
		} else {
			System.out.println(response);
			throw new Exception("Change state failed> " + response.getData());
		}
	}

	/**
	 * add a new tag
	 * @param id application id	
	 * @param tagName tag name
	 * @return
	 * @throws Exception
	 */
	public HttpResponse addNewTag(String id, String tagName)throws Exception {
		checkAuthentication();
		requestHeaders.put("Content-Type", "application/json");
		HttpResponse response = HttpUtil.doPut(new URL(backEndUrl
				+ "/publisher/api/tag/webapp/" + id), "{\"tags\":[\" " + tagName + " \"]}", requestHeaders);
	
		if (response.getResponseCode() == 200) {
			//VerificationUtil.checkErrors(response);
			return response;
		} else {
			throw new Exception("Get Api Information failed> "
					+ response.getData());
		}

	}
	
	/**
	 * delete tag
	 * @param id application id
	 * @param tagName tag name
	 * @return
	 * @throws Exception
	 */
	public HttpResponse deleteTag(String id, String tagName)throws Exception {
		checkAuthentication();
		requestHeaders.put("Content-Type", "application/json");
		HttpResponse response = HttpUtil.doDelete(new URL(backEndUrl
				+ "/publisher/api/tag/webapp/" + id + "/" + tagName), requestHeaders);
	
		if (response.getResponseCode() == 200) {
			//VerificationUtil.checkErrors(response);
			return response;
		} else {
			throw new Exception("Get Api Information failed> "
					+ response.getData());
		}

	}


	public void setHttpHeader(String headerName, String value) {
		this.requestHeaders.put(headerName, value);
	}

	public String getHttpHeader(String headerName) {
		return this.requestHeaders.get(headerName);
	}

	public void removeHttpHeader(String headerName) {
		this.requestHeaders.remove(headerName);
	}

	private String setSession(String session) {
		return requestHeaders.put("Cookie", session);
	}

	/**
	 * method to check whether user is logged in
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean checkAuthentication() throws Exception {
		if (requestHeaders.get("Cookie") == null) {
			throw new Exception("No Session Cookie found. Please login first");
		}
		return true;
	}

    /**
     * Add document
     *
     * @param docRequest DocumentRequest object
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addDocument(DocumentRequest docRequest) throws Exception {
        checkAuthentication();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost =
                new HttpPost(backEndUrl + "/publisher/api/doc");
        httppost.setHeader("Cookie", requestHeaders.get("Cookie"));
        httppost.setEntity(docRequest.generateMulipartEnitity());
        org.apache.http.HttpResponse res = httpclient.execute(httppost);
        HttpResponse response = HttpUtil.convertResponse(res);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("Failed to add File type document to the api:" + docRequest.getApiName());
        }

    }

    /**
     *
     * @param appName APP Name
     * @param appVersion APP Version
     * @param provider Prover Name
     * @param docName Document Name
     * @param content Inline document content
     * @return
     * @throws Exception
     */
    public HttpResponse addInlineDocumentContent(String appName,String appVersion,String provider,String docName,
                                         String content) throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
                "/publisher/api/doc")
                , "action=addInlineContent" + "&provider=" + provider +
                "&apiName=" + appName + "&version=" + appVersion + "&docName="
                + docName + "&content=" + content
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("API Subscription failed> " + response.getData());
        }

    }

    /**
     * @param appName  APP Name
     * @param appVersion  APP Version
     * @param provider APP provider
     * @param docName  Document Name
     * @param docType  Document Type
     * @return
     * @throws Exception
     */
    public HttpResponse deleteDocument(String appName, String appVersion, String provider,
                                       String docName, String docType) throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
                "/publisher/api/doc")
                , "action=removeDocumentation" + "&provider=" + provider +
                "&apiName=" + appName + "&version=" + appVersion + "&docName="
                + docName + "&docType=" + docType
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("API Subscription failed> " + response.getData());
        }
    }

	/**
	 * Returns the HTTP response for the discover request
	 * @param appDiscoveryListRequest
	 * @return
	 * @throws Exception
	 */
	public HttpResponse getDiscoverableApplications(AppDiscoveryListRequest appDiscoveryListRequest) throws Exception {
		checkAuthentication();
		String payload = appDiscoveryListRequest.generatePostData();
		requestHeaders.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response =
				HttpRequestUtil.doPost(new URL(backEndUrl
								+ "/publisher/assets/discover/webapp/"),
						payload, requestHeaders);
		if (response.getResponseCode() == 200) {
			return response;
		} else {
			throw new Exception("App discovery failed> " + response.getData());
		}
	}

    public String createMobileApplicatopn(MobileApplicationBean mobileApplicationBean) throws IOException {
        this.requestHeaders.remove("Content-Type");
        return HttpUtil.doPostMultiData
                (backEndUrl+"/publisher/api/asset/mobileapp",mobileApplicationBean,this.requestHeaders);
    }

}
