/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.appmanager.tests.util;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.carbon.appmanager.tests.util.bean.AppCreateRequest;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class APPMPublisherRestClient {
	private String backEndUrl;
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private WebDriver driver;

	public APPMPublisherRestClient(String backEndUrl) throws MalformedURLException {
		this.backEndUrl = backEndUrl;
		if (requestHeaders.get("Content-Type") == null) {
			this.requestHeaders.put("Content-Type", "application/json");
		}

        driver = BrowserManager.getWebDriver();
        driver.get(backEndUrl + "/publisher/login");
    }

	/**
	 * logs in to the user store
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String login(String userName, String password) throws Exception {

		// find element username
		WebElement usernameEle = driver.findElement(By.id("username"));
		// fill user name
		usernameEle.sendKeys("admin");
		// find element password
		WebElement passwordEle = driver.findElement(By.id("password"));
		// fill element
		passwordEle.sendKeys("admin");
		// find submit button and click on it.
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
				// System.out.println(loadedCookie.toString());
			}
		}

		String urlPath = aURL.getPath();

		driver.quit();

		if ((urlPath.equals("/publisher/assets/webapp/"))) {
			return "logged in";
		} else {
			return "not logged in";
		}

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
		HttpResponse response =
		                        HttpRequestUtil.doPost(new URL(backEndUrl +
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

	/***
	 * publish an application which is in created state
	 * @param appId
	 *            - application id
	 * @return -response
	 * @throws Exception
	 */
	public HttpResponse publishApp(String appId) throws Exception {
		// created to in-review
		changeState(appId, "Submit");
		// in-review to publish
		HttpResponse response = changeState(appId, "Approve");
		return response;
	}

	/**
	 * this method gives  the current life cycle state of the application	 
	 * @param appId -application id	
	 * @return -response 
	 * @throws Exception
	 */
	public HttpResponse getCurrentState(String appId) throws Exception {
		HttpResponse response =
		                        HttpRequestUtil.doGet(backEndUrl +
		                                              "/publisher/api/lifecycle/subscribe/webapp/" +
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
	public HttpResponse changeState(String appId, String toSate) throws Exception {
		this.requestHeaders.put("Content-Type", "");
		HttpResponse response =
		                        HttpUtil.doPut(new URL(backEndUrl + "/publisher/api/lifecycle/" +
		                                               toSate + "/webapp/" + appId), "",
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

}
