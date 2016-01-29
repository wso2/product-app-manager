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
package org.wso2.appmanager.integration.utils.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * Sample request :
 * policyGroupName=Default&throttlingTier=Bronze&userRoles=Internal/everyone,Internal/store-admin
 * &anonymousAccessToUrlPattern=&policyGroupId=5&objPartialMappings=[]&policyGroupDesc=tes des
 */
public class PolicyGroupRequest extends AbstractRequest {
    private PolicyGroup policyGroup;

    public PolicyGroupRequest(PolicyGroup policyGroup) {
        this.policyGroup = policyGroup;
    }

    @Override
    public void setAction() {

    }

    @Override
    public void init() {
        addParameter("policyGroupName", policyGroup.getPolicyGroupName());
        addParameter("throttlingTier", policyGroup.getThrottlingTier());
        addParameter("userRoles", StringUtils.join(policyGroup.getUserRoles(), ","));
        addParameter("anonymousAccessToUrlPattern", policyGroup.getAnonymousAccessToUrlPattern());
        addParameter("objPartialMappings", "[" + policyGroup.getObjPartialMappings() + "]");
        addParameter("policyGroupDesc", policyGroup.getPolicyGroupDesc());

    }
}
