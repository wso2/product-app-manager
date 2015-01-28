package com.pizzashack.client.web;


import com.pizzashack.client.dto.Token;
import org.apache.http.HttpResponse;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class TokenManager {
	
    private HTTPClient httpClient;    

    public TokenManager() {
        httpClient = new HTTPClient();
    }

    public String getToken(String samlTokenId){
        String submitUrl = PizzaShackWebConfiguration.getInstance().getLoginURL();
        String consumerKey = PizzaShackWebConfiguration.getInstance().getConsumerKey();
        String consumerSecret = PizzaShackWebConfiguration.getInstance().getConsumerSecret();

        /*
        APIName need to be the alias name given when registering API in App-Manager
         */
        String apiName = "pizzashack";
        try {
            String applicationToken = consumerKey + ":" + consumerSecret;
            BASE64Encoder base64Encoder = new BASE64Encoder();
            applicationToken = "Basic " + base64Encoder.encode(applicationToken.getBytes()).trim();
            String payload = "grant_type=SAML2&scope=" + samlTokenId + "," + apiName;            
            HttpResponse httpResponse = httpClient.doPost(submitUrl,applicationToken, payload, "application/x-www-form-urlencoded");
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
            	return null;
            }
            return httpClient.getResponsePayload(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
