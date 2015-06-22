/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */
package org.wso2.carbon.appmanager.integration.ui.Util.service;

import org.apache.axis2.client.Options;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException;
import org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceStub;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.rmi.RemoteException;

/**
 * This class is use as a client for tenant admin service
 */
public class TenantMgtAdmin {
    /**
     * This method is use to add a new tenant
     *
     * @param cookie         - Authentication cookie
     * @param endpoint       - backend server url
     * @param tenantInfoBean - Tenant bean class object
     * @throws java.rmi.RemoteException,org.wso2.carbon.tenant.mgt.stub.TenantMgtAdminServiceExceptionException on error
     */
    public static String addTenant(String cookie, String endpoint, TenantInfoBean tenantInfoBean)
            throws RemoteException, TenantMgtAdminServiceExceptionException {

        String tenantMgtEndpoint = endpoint + "TenantMgtAdminService";
        TenantMgtAdminServiceStub tenantMgtStub = new TenantMgtAdminServiceStub(tenantMgtEndpoint);
        Options option = tenantMgtStub._getServiceClient().getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        return tenantMgtStub.addTenant(tenantInfoBean);
    }

}
