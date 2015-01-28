/*
 * ​Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.​
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

package org.wso2.carbon.appmanager.tests.sample;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.tests.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.tests.util.APPMPublisherRestClient;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.appmanager.tests.util.bean.AppCreateRequest;

public class CreateAndPublishTest extends APPManagerIntegrationTest {
	private APPMPublisherRestClient appmPublisher;

	// move to base class
	private String publisherURLHttp;
	private String appId;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init(0);
		if (isBuilderEnabled()) {
			publisherURLHttp = getServerURLHttp();
		} else {
		}

		appmPublisher = new APPMPublisherRestClient(publisherURLHttp);
		appmPublisher.login(userInfo.getUserName(), userInfo.getPassword());
	}

	@Test(groups = { "wso2.appmanager" }, description = "App create test ")
	public void createApplicationTestCase() throws Exception {
		AppCreateRequest appRequest = new AppCreateRequest();
		HttpResponse response = appmPublisher.createApp(appRequest);
		JSONObject jsonObject = new JSONObject(response.getData());
		appId = (String) jsonObject.get("id");
		Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatch");

	}

	@Test(groups = { "wso2.appmanager" }, description = "App publisher test ")
	public void publishApplicationTestCase() throws Exception {
		HttpResponse response = appmPublisher.publishApp(appId);
		Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatch");
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}
}
