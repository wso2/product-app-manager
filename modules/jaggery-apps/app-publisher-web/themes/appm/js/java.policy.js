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
 * Load all the available Java Policies List and the Mapping for given Application Id
 * @param applicationUUID
 */
function loadAvailableJavaPolicies(applicationUUID) {
    $.ajax({
        url: '/publisher/api/entitlement/get/all/available/java/policy/handlers/details/list/' + applicationUUID,
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
                    policyProperties: data[i].policyProperties,
                    applicationId: data[i].applicationId
                };
                javaPolicyArray.push(obj);
            }
            updateJavaPolicyPartial();

        },
        error: function () {
        }
    });
}

/**
 * draw java policies list
 */
function updateJavaPolicyPartial() {
    var enabledStatus;  //if policy is mandatory make it wont allow user to un-tick the particular java policy
    var checkedStatus; //if policy is mandatory always tick the java policy

    $.each(javaPolicyArray, function (index, obj) {
        if (obj != null) {

            if (obj.isMandatory) {
                //always check and and disable the mandatory fields
                enabledStatus = 'disabled';
                checkedStatus = 'checked';
            } else {
                enabledStatus = 'enabled';

                //tick the saved mappings
                if (obj.applicationId == null) {
                    checkedStatus = "";
                }
                else {
                    checkedStatus = "checked";
                }
            }

            $('#javaPolicy_tbody').append(
                "<tr>" +
                "<td>" + obj.displayName + "</td>" +
                "<td>" + obj.description + "</td>" +
                "<td><input  class='javaPolicy-opt-val' data-javaPolicy-id='" + obj.javaPolicyId +
                "'  type='checkbox' " + enabledStatus +
                " " + checkedStatus + "></td>" +
                "</tr>");

        }
    });

    //draw java policy items
    $("#javaPolicy_tbody").trigger("draw");

    //update the url pattern wise policy group drop downs
    $('.javaPolicy-opt-val').each(function () {
        if( $(this).prop('checked'))
        {
           // alert($(this).attr('data-javaPolicy-id'));
            //todo: javaPolicyArray[index].isChecked =true and write the whole object to a data field

        }
    });

}
