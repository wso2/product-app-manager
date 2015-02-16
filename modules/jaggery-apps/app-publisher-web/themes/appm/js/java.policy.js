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

var javaPolicyArray = new Array(); //contains Java Polices list


/**
 * Load all the available Java Policies List
 */
function loadAvailableJavaPolicies() {
    $.ajax({
        url: '/publisher/api/entitlement/get/all/available/java/policy/handlers/details/list',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var obj = {
                    javaPolicyId: data[i].javaPolicyId,
                    displayName: data[i].displayName,
                    fullQualifiedName: data[i].fullQualifiedName,
                    description: data[i].description,
                    displayOrder: data[i].displayOrder,
                    isMandatory: data[i].isMandatory,
                    policyProperties: data[i].policyProperties
                };
                javaPolicyArray.push(obj);
            }
            updateJavaPolicyPartial();

        },
        error: function () {
        }
    });
}

function updateJavaPolicyPartial(){

    //draw java policies list
    $.each(javaPolicyArray, function (index, obj) {
        if (obj != null) {
            console.info(obj)
            $('#javaPolicy_tbody').append(
                '<tr>' +
                '<td>' + obj.displayName + '</td>' +
                '<td>' + obj.description + '</td>' +
                '<td><input data-javaPolicy-id=' + obj.id + ' type=checkbox disabled checked></td>' +
                '</tr>');

            //policyGroupIndexArray.push(obj.policyGroupId);
        }
    });

    //draw java policy items
    $("#javaPolicy_tbody").trigger("draw");
}
