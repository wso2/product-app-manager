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
var javaPolicyIndexArray = []; //used to maintain the selected Java Policy ID's list

/**
 * Load all the available Java Policies List and the Mapping for given Application Id
 * @param applicationUUID
 * @param isGlobalPolicy :if application level policy - true else if resource level policy - false
 */
function loadAvailableJavaPolicies(applicationUUID, isGlobalPolicy) {
    $.ajax({
        url: '/publisher/api/entitlement/get/all/available/java/policy/handlers/details/list/' + applicationUUID + '/' + isGlobalPolicy,
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
    var checkedStatus = "";
    $.each(javaPolicyArray, function (index, obj) {
        if (obj != null) {

            //if policy is saved, tick the java policy checkbox
            if (obj.applicationId != null) {
                checkedStatus = "checked";
                //push the id's of saved policies to array
                javaPolicyIndexArray.push(obj.javaPolicyId);
            }

            //draw div's to each policy
            $('#divJavaPolicies').empty().append(
                "<div class='form-group'> " +
                "<label class='control-label col-sm-4'> " + obj.displayName + "</label> " +
                "<div class='col-md-1'>" +
                "<input  class='javaPolicy-opt-val' data-javaPolicy-id='" + obj.javaPolicyId + "'  type='checkbox' "
                + checkedStatus + ">" +
                "</div>" +
                "</div>");
        }
    });

    //store the list of Java Policy id's (used in save operation to map the application wise assigned java policies)
    $('#uritemplate_javaPolicyIds').val(JSON.stringify(javaPolicyIndexArray));

    //draw java policy items
    $("#javaPolicy_tbody").trigger("draw");


}


//this event triggers when an dynamic optional java policy get clicked. Need to maintain the selected policy id list
$(document).on("click", ".javaPolicy-opt-val", function () {
    var javaPolicyId = parseInt($(this).attr("data-javaPolicy-id"));
    var itemIndex = javaPolicyIndexArray.indexOf(javaPolicyId); //index of clicked (current) item

    if ($(this).prop("checked")) {
        //add item if checked and if not exists in the array
        if (itemIndex == -1) {
            javaPolicyIndexArray.push(javaPolicyId);
        }
    }
    else {
        //remove item if unchecked and if exists in the array
        if (itemIndex > -1) {
            javaPolicyIndexArray.splice(itemIndex, 1);
        }
    }

    //update hidden field which used in save operation to get the mapped java policies
    $('#uritemplate_javaPolicyIds').val(JSON.stringify(javaPolicyIndexArray));

});