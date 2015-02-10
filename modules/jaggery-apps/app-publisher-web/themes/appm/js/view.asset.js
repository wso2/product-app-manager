/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

var policyGroupsArray = new Array(); //policy group related details array
var policyGroupsOptionsBlock = ""; //contains html formatted options list of Policy Groups

$(document).ready(function () {
    policyGroupsOptionsBlock = populatePolicyGroupBlock();
    //draw policy groups options list for each url pattern/verb
    $(".dropdownPolicyGroup").empty().append(policyGroupsOptionsBlock);
});

/**
 * populate policy group drop down list values
 * @returns {string} :html formatted options list of policy groups
 */
function populatePolicyGroupBlock() {
    var strContent = "";
    var uuid = $("#uuid").val();
    //get application id by passing the application uuid
    $.ajax({
        url: '/publisher/api/entitlement/get/webapp/id/from/entitlements/uuid/' + uuid,
        type: 'GET',
        async: false,
        contentType: 'application/json',
        success: function (id) {
            // get the entitlement policy groups
            $.ajax({
                url: '/publisher/api/entitlement/get/policy/Group/by/appId/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                async: false,
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        policyGroupsArray.push({
                            policyGroupId: data[i].policyGroupId,
                            policyGroupName: data[i].policyGroupName
                        })
                    }
                    //manually draw the option list
                    for (var i = 0; i < policyGroupsArray.length; i++) {
                        strContent += "<option title='" + policyGroupsArray[i].policyGroupName + "' value='" + policyGroupsArray[i].policyGroupId + "' id='" + policyGroupsArray[i].policyGroupId + "'>" + policyGroupsArray[i].policyGroupName + "</option>";
                    }


                },
                error: function () {
                }
            });
        },
        error: function () {
        }
    });
    return strContent;
}
