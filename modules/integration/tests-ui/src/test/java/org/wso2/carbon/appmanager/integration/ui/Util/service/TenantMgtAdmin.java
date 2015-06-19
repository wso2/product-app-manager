package org.wso2.carbon.appmanager.integration.ui.Util.service;

import org.apache.axis2.client.Options;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceStub;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.rmi.RemoteException;

/**
 * Created by ushan on 6/11/15.
 */
public class TenantMgtAdmin {
    public static String addTenant(String cookie, String endpoint, TenantInfoBean tenantInfoBean)
            throws RemoteException, TenantMgtAdminServiceExceptionException {

        TenantMgtAdminServiceStub tenantMgtStub = null;
        String tenantMgtEndpoint = endpoint + "TenantMgtAdminService";

        tenantMgtStub = new TenantMgtAdminServiceStub(tenantMgtEndpoint);
        Options option = tenantMgtStub._getServiceClient().getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

         return tenantMgtStub.addTenant(tenantInfoBean);
    }

}
