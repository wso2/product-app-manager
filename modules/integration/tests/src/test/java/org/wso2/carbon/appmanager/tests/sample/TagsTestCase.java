package org.wso2.carbon.appmanager.tests.sample;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.tests.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.tests.util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.tests.util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.tests.util.bean.AppCreateRequest;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import com.jayway.jsonpath.JsonPath;

/**
 * Test tags and ratings on the user store
 * 
 */
public class TagsTestCase extends APPManagerIntegrationTest {

	private APPMPublisherRestClient appPublisher;
	private APPMStoreRestClient appStore;
	private String publisherURLHttp;
	private String storeURLHttp;

	@BeforeClass(alwaysRun = true)
	public void init() throws Exception {
		super.init(0);
		if (isBuilderEnabled()) {
			publisherURLHttp = getServerURLHttp();
			storeURLHttp = getServerURLHttp();

		} else {
			publisherURLHttp = getPublisherServerURLHttp();
			storeURLHttp = getStoreServerURLHttp();
		}
		appPublisher = new APPMPublisherRestClient(publisherURLHttp);
		appStore = new APPMStoreRestClient(storeURLHttp);

		appStore.login(userInfo.getUserName(), userInfo.getPassword());
		appPublisher.login(userInfo.getUserName(), userInfo.getPassword());

	}

	/**
	 * Create an application without a tag and check whether any tags are
	 * displayed in the store
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "wso2.appm" }, description = "Tags creation test case without tags")
	public void testCreateAppWithoutTagsTestCase() throws Exception {

		String appName = "testapp1";
		String appId = null;
		HttpResponse response = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;

		// create an app without a tag. the app store does not have any other
		// apps
		AppCreateRequest appRequest = new AppCreateRequest();
		appRequest.setOverview_name(appName);
		response = appPublisher.createApp(appRequest);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");
		jsonObject = new JSONObject(response.getData());
		appId = (String) jsonObject.get("id");

		// publish the app
		response = appPublisher.publishApp(appId);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");

		// check from the store
		response = appStore.getAllTags();
		jsonArray = new JSONArray(response.getData());
		Assert.assertEquals(jsonArray.length(), 0, "tags are not empty");
		
		

	}

	/**
	 * create an application with a tag during the app creation time and check
	 * whether tag is shown on the store
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "wso2.appm" }, description = "Tags creation test case with tags" , dependsOnMethods={"testCreateAppWithoutTagsTestCase"})
	public void testCreateAppWithTagsTestCase() throws Exception {

		
		String tag = "tag1";
		String appName = "testapp2";
		String appId = null;
		HttpResponse response = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;

		// create an app without a tag. the app store does not have any other
		// apps
		AppCreateRequest appRequest = new AppCreateRequest();
		appRequest.setOverview_name(appName);
		appRequest.setTags(tag);
		response = appPublisher.createApp(appRequest);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");
		jsonObject = new JSONObject(response.getData());
		appId = (String) jsonObject.get("id");

		// publish the app
		response = appPublisher.publishApp(appId);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");

		// check from the store
		response = appStore.getAllTags();

		boolean hasTag = false;
		jsonArray = new JSONArray(response.getData());
		// check whether the response contains the newly created tag
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jObj = jsonArray.getJSONObject(i);
			if (tag.equals(jObj.getString("name"))) {
				hasTag = true;
				break;
			}
		}
		Assert.assertEquals(hasTag, true,
				"Tag is not created during the app creation time");

	}

	/**
	 * test tags updates in the publisher side and validate it from the store.
	 * first create an application without any tags. then manipulate tags as an
	 * edit Here a tag is created and then it is deleted. both times check the
	 * visibility of that tag in user store
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "wso2.appm" }, description = "Tags update test case" ,dependsOnMethods={"testCreateAppWithoutTagsTestCase"})
	public void testTagsUpdateTestCase() throws Exception {
		
		String tag = "tag3";
		String appName = "testapp3";
		String appId = null;
		HttpResponse response = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;

		// create an app without a tag. the app store does not have any other
		// apps
		AppCreateRequest appRequest = new AppCreateRequest();
		appRequest.setOverview_name(appName);
		response = appPublisher.createApp(appRequest);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");
		jsonObject = new JSONObject(response.getData());
		appId = (String) jsonObject.get("id");

		// publish the app
		response = appPublisher.publishApp(appId);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");

		// add tags from the publisher side
		response = appPublisher.addNewTag(appId, tag);
		jsonObject = new JSONObject(response.getData());
		Assert.assertEquals(jsonObject.get("ok").toString(), "true",
				"Tag creation failed");

		// check from the store
		response = appStore.getAllTags();

		boolean hasTag = false;
		jsonArray = new JSONArray(response.getData());
		// check whether the response contains the newly created tag
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jObj = jsonArray.getJSONObject(i);
			if (tag.equals(jObj.getString("name"))) {
				hasTag = true;
				break;
			}
		}
		Assert.assertEquals(hasTag, true,
				"Store does not have the newly created tag");

		// delete the tag
		response = appPublisher.deleteTag(appId, tag);
		jsonObject = new JSONObject(response.getData());
		Assert.assertEquals(jsonObject.get("ok").toString(), "true",
				"Tag removal failed");

		// check from the store
		response = appStore.getAllTags();

		hasTag = false;
		jsonArray = new JSONArray(response.getData());
		// check whether the response contains the newly created tag
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jObj = jsonArray.getJSONObject(i);
			if (tag.equals(jObj.getString("name"))) {
				hasTag = true;
				break;
			}
		}
		Assert.assertEquals(hasTag, false, "Store still has the removed tag");

	}

	/**
	 * test information displayed on the store for a selected tag
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "wso2.appm" }, description = "Select a tag", dependsOnMethods={"testCreateAppWithoutTagsTestCase"})
	public void testSelectTagTestCase() throws Exception {
		
		String tag = "tag4";
		String appName = "testapp4";
		String appId = null;
		HttpResponse response = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;

		// create an app without a tag. the app store does not have any other
		// apps
		AppCreateRequest appRequest = new AppCreateRequest();
		appRequest.setOverview_name(appName);
		response = appPublisher.createApp(appRequest);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");
		jsonObject = new JSONObject(response.getData());
		appId = (String) jsonObject.get("id");

		// publish the app
		response = appPublisher.publishApp(appId);
		Assert.assertEquals(response.getResponseCode(), 200,
				"Response code mismatch");

		// add tags from the publisher side
		response = appPublisher.addNewTag(appId, tag);
		jsonObject = new JSONObject(response.getData());
		Assert.assertEquals(jsonObject.get("ok").toString(), "true",
				"Tag creation failed");

		//select a tag and evaluate the fields in the response
		response = appStore.getTag(tag);
		System.out.println(response.getData());
		String jsonPath = "$.body.assets.context.assets[0].id";
		String id  = JsonPath.read(response.getData(), jsonPath);
		
		Assert.assertEquals(id, appId,
				"correct app is not selected");
		
	}

	@AfterClass(alwaysRun = true)
	public void destroy() throws Exception {
		super.cleanup();
	}
}
