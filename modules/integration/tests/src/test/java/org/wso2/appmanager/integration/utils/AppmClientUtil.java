package org.wso2.appmanager.integration.utils;


import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

public class AppmClientUtil {

    //todo: need to implement this considering sso scenario
    public static HttpResponse invoke(String userName, String password, String url) {
        HttpResponse httpResponse = new HttpResponse("Ok", 200);
        httpResponse.getResponseCode();
        return httpResponse;
    }
}
