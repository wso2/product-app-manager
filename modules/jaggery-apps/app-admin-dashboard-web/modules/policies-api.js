/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


var apiProvider = jagg.module('manager').getAPIProviderObj();
var log = new Log();
var appMDAO = Packages.org.wso2.carbon.appmgt.impl.dao.AppMDAO;
var appMDAOObj = new appMDAO();
var apiUtil = Packages.org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
var apiUtil = new apiUtil();

/**
 * Get the list of Shared policy partials
 */
var getSharedPolicyList = function () {
    return apiProvider.getSharedPolicyPartialList();
};


var getXACMLPolicyTemplate = function () {
    return apiUtil.getXACMLPolicyTemplate();
};


var validateResult = function (policyContent) {
    var validationResult = validateEntitlementPolicyPartial(policyContent);
    var validationResultJson = {"isValid": validationResult.isValid()};
    return validationResultJson

}
/**
 * Validates the given policy content
 */
function validateEntitlementPolicyPartial(policyContent) {
    return apiProvider.validateEntitlementPolicyPartial(policyContent);
}


function savePolicy(policyPartialName, policyPartial, isSharedPartial, policyPartialDesc) {
    var partialId = saveEntitlementPolicyPartial(policyPartialName, policyPartial, isSharedPartial, policyPartialDesc);
    var response = {"id": partialId};
    return response;
}

/**
 * Saves the given policy content
 */
function saveEntitlementPolicyPartial(policyPartialName, policyPartial, isSharedPartial, policyPartialDesc) {
    return apiProvider.saveEntitlementPolicyPartial(policyPartialName, policyPartial, isSharedPartial, policyPartialDesc);
}


function updatePolicy(id, policyPartial, isSharedPartial, policyPartialDesc) {
    var isSuccess = updateEntitlementPolicyPartial(id, policyPartial, isSharedPartial, policyPartialDesc);
    return isSuccess;
}

/**
 * Update the given policy content
 */
function updateEntitlementPolicyPartial(policyPartialId, policyPartial, isSharedPartial, policyPartialDesc) {
    return apiProvider.updateEntitlementPolicyPartial(policyPartialId, policyPartial, isSharedPartial, policyPartialDesc);
}

/**
 * Get linked App names with the Policy
 */
function getAssociatedApps(policylId) {
    return apiProvider.getAssociatedAppsNameList(policylId);
}

function deletePolicy(policylId) {
    var isSuccess = deleteEntitlementPolicyPartial(policylId);
    return isSuccess;
}

/**
 * Delete the given policy partial
 */
function deleteEntitlementPolicyPartial(policyPartialId) {
    return apiProvider.deleteEntitlementPolicyPartial(policyPartialId);
}

var getCacheHitMiss= function (startDate,endDate) {
    return apiProvider.getcashHitMiss("__all_providers__",startDate,endDate);
};