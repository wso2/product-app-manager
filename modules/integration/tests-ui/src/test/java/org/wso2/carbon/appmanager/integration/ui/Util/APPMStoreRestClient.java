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

package org.wso2.carbon.appmanager.integration.ui.Util;


import org.wso2.carbon.appmanager.integration.ui.Util.Bean.SubscriptionRequest;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class APPMStoreRestClient {
	private String backEndUrl;
	private Map<String, String> requestHeaders = new HashMap<String, String>();
    public String session;

	public APPMStoreRestClient(String backEndUrl) {
		this.backEndUrl = backEndUrl;
		if (requestHeaders.get("Content-Type") == null) {
			this.requestHeaders.put("Content-Type", "application/json");
		}
	}

	/**
	 * logs in to the user store
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public HttpResponse login(String userName, String password)
			throws Exception {

		this.requestHeaders.put("Content-Type", "application/json");
		HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
				+ "/store/apis/user/login"), "{\"username\":" + "\"" + userName
				+ "\"" + ",\"password\":" + "\"" + password + "\"" + "}",
				requestHeaders);

		/*
		 * On Success {"error" : false, "message" : null} status code 200 On
		 * Failure {"error" : true, "message" : null} status code 200
		 */
		if (response.getResponseCode() == 200) {

			// if error == true this will return an exception then test fail!
			VerificationUtil.checkErrors(response);

		    session = getSession(response.getHeaders());
			if (session == null) {
				throw new Exception("No session cookie found with response");
			}
			setSession(session);
			return response;
		} else {
			System.out.println(response);
			throw new Exception("User Login failed> " + response.getData());
		}

	}

	/**
	 * logs out from the user store	 
	 * @return HttpResponse
	 * @throws Exception
	 */
	public HttpResponse logout() throws Exception {
		this.requestHeaders.put("Content-Type", "text/html");

		HttpResponse response = HttpRequestUtil.doGet(backEndUrl
				+ "/store/logout", requestHeaders);

		if (response.getResponseCode() == 200) {
			this.requestHeaders.clear();
			return response;
		} else {
			System.out.println(response);
			throw new Exception("User Logout failed> " + response.getData());
		}

	}


    /**
     * Subscribe application
     *
     * @param subscriptionRequest
     * @return
     * @throws Exception
     */
    public HttpResponse subscribeForApplication(SubscriptionRequest subscriptionRequest)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl+"/store/resources/webapp/v1/subscription/app")
                ,subscriptionRequest.generateRequestParameters()
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            System.out.println("SUBCRIPTION RESPONCE IS = "+response.getResponseCode());
            return response;
        } else {
            throw new Exception("Application Subscription failed> " + response.getData());
        }
    }

    /**
     * Unsubscribe application
     *
     * @param unsubscriptionRequest
     * @return
     * @throws Exception
     */

    public HttpResponse unsubscribeForApplication(SubscriptionRequest unsubscriptionRequest,String appType)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost
                (new URL(backEndUrl+"/store/resources/"+appType+"/v1/unsubscription/app")
                ,unsubscriptionRequest.generateRequestParameters()
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("Application Unsubscription failed> " + response.getData());
        }
    }

    /**
	 * register new user
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public HttpResponse register(String userName, String password)
			throws Exception {

		HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
				+ "/store/apis/user/register"), "{\"username\":" + "\""
				+ userName + "\"" + ",\"password\":" + "\"" + password + "\""
				+ "}", requestHeaders);

		if (response.getResponseCode() == 200) {

			// if error == true this will return an exception then test fail!
			VerificationUtil.checkErrors(response);

			String session = getSession(response.getHeaders());
			if (session == null) {
				throw new Exception("No session cookie found with response");
			}
			setSession(session);
			return response;
		} else {
			System.out.println(response);
			throw new Exception("User registration failed> "
					+ response.getData());
		}

	}

	/**
	 * return all the tags related to the user
	 * 
	 * @return
	 * @throws Exception
	 */
	public HttpResponse getAllTags() throws Exception {
		checkAuthentication();
		HttpResponse response = HttpRequestUtil.doGet(backEndUrl
				+ "/store/apis/tag", requestHeaders);
		if (response.getResponseCode() == 200) {
			// VerificationUtil.checkErrors(response);
			return response;
		} else {
			throw new Exception("Get all tags failed> " + response.getData());
		}

	}

	/**
	 * get the selected tag info
	 * 
	 * @param tagName
	 * @return
	 * @throws Exception
	 */
	public HttpResponse getTag(String tagName) throws Exception {
		checkAuthentication();
		requestHeaders
				.put("X-Caramel-Data",
						"{\"title\":null,\"header\":[\"header\"],\"body\":[\"assets\",\"sort-assets\"]}");
		
		HttpResponse response = HttpUtil.doGet(backEndUrl
				+ "/store/assets/webapp/?tag=" + tagName, requestHeaders);
		if (response.getResponseCode() == 200) {
			// VerificationUtil.checkErrors(response);
			return response;
		} else {
			throw new Exception("Get Api Information failed> "
					+ response.getData());
		}
	}

	/**
	 * Rate a given application with a given rating value
	 * @param id application id
	 * @param appType application type
	 * @param ratingValue rating value (0-5)
	 * @return response with average rating value and user rating value
	 * @throws Exception
	 */
	public HttpResponse rateApplication(String id, String appType, int ratingValue) throws Exception {
		checkAuthentication();
		HttpResponse response = HttpRequestUtil.doGet(backEndUrl + "/store/apis/rate?id=" +
				id + "&type=" + appType + "&value=" + ratingValue, requestHeaders);
		return response;
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

	private String getSession(Map<String, String> responseHeaders) {
		return responseHeaders.get("Set-Cookie");
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
