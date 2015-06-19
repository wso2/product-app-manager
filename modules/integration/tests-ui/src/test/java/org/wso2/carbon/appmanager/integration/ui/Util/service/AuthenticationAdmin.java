package org.wso2.carbon.appmanager.integration.ui.Util.service;

import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;

/**
 * Created by ushan on 6/11/15.
 */
public class AuthenticationAdmin {
    private static final String SERVICE_NAME = "AuthenticationAdmin";

    public static String getCookie(String endpoint) {

        String cookie = null;
        try {
            AuthenticationAdminStub authAdminStub = new AuthenticationAdminStub(endpoint+SERVICE_NAME);
            boolean loginSuccess = authAdminStub.login("admin", "admin", "localhost");

            if (loginSuccess) {
                ServiceContext serviceContext = authAdminStub._getServiceClient().getLastOperationContext().getServiceContext();
                cookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cookie;
    }
}
