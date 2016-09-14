/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.integration.common.clients;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.info.stub.RegistryExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceResourceServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceStub;
import org.wso2.carbon.registry.resource.stub.beans.xsd.*;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;

import javax.activation.DataHandler;
import java.rmi.RemoteException;


public class ResourceAdminServiceClient {
    private static final Log log = LogFactory.getLog(ResourceAdminServiceClient.class);

    private final String serviceName = "ResourceAdminService";
    private ResourceAdminServiceStub resourceAdminServiceStub;

    private static final String MEDIA_TYPE_WSDL = "application/wsdl+xml";
    private static final String MEDIA_TYPE_WADL = "application/wadl+xml";
    private static final String MEDIA_TYPE_SCHEMA = "application/x-xsd+xml";
    private static final String MEDIA_TYPE_POLICY = "application/policy+xml";
    private static final String MEDIA_TYPE_SWAGGER = "application/swagger+json";
    private static final String MEDIA_TYPE_GOVERNANCE_ARCHIVE = "application/vnd.wso2.governance-archive";

    public ResourceAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        resourceAdminServiceStub = new ResourceAdminServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, resourceAdminServiceStub);
    }

    public String getProperty(String resourcePath, String key)
            throws RemoteException, ResourceAdminServiceExceptionException {

        return resourceAdminServiceStub.getProperty(resourcePath, key);

    }

    public void updateTextContent(String path, String content)
            throws RemoteException, ResourceAdminServiceExceptionException {
        try {
            resourceAdminServiceStub.updateTextContent(path, content);
        } catch (RemoteException e) {
            log.error("Cannot edit the content of the resource : " + e.getMessage());
            throw new RemoteException("Edit content error : ", e);

        } catch (ResourceAdminServiceExceptionException e) {
            log.error("Cannot edit the content of the resource : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("Get version error : ", e);

        }
    }

    public String getTextContent(String path)
            throws RemoteException, ResourceAdminServiceExceptionException {
        String content = null;
        try {
            content = resourceAdminServiceStub.getTextContent(path);
        } catch (RemoteException e) {
            log.error("Unable get content : " + e.getMessage());
            throw new RemoteException("Restore version error : ", e);
        } catch (ResourceAdminServiceExceptionException e) {
            log.error("GetTextContent Error : " + e.getMessage());
            throw new ResourceAdminServiceExceptionException("GetTextContent Error :  ", e);
        }
        return content;
    }

}