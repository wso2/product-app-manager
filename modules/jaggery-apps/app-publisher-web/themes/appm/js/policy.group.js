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


var editedPolicyGroup = 0; //if edit or save
var policyGroupsArray = new Array();

$(document).ready(function () {
    $("#throttlingTier").empty().append(throttlingTierControlBlock);
});


$('#userRoles').tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
    theme: 'facebook',
    preventDuplicates: true,
    onAdd: function (role) {

    },
    onDelete: function (role) {

    }
});

function savePolicyGroup() {
    var policyGroupName = $('#policyGroupName').val();
    var throttlingTier = $('#throttlingTier').val();
    var anonymousAccessToUrlPattern = $('#anonymousAccessToUrlPattern').val();
    var userRoles = $('#userRoles').val();

    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/policyGroup/save',
        type: 'POST',
        data: {
            "policyGroupName": policyGroupName,
            "throttlingTier": throttlingTier,
            "userRoles": userRoles,
            "anonymousAccessToUrlPattern": anonymousAccessToUrlPattern
        },
        success: function (data) {
            editedPolicyGroup = JSON.parse(data).response.id;
            policyGroupsArray.push({
                policyGroupId: returnedId,
                policyGroupName: policyGroupName,
                throttlingTier: throttlingTier,
                anonymousAccessToUrlPattern: anonymousAccessToUrlPattern,
                userRoles: userRoles
            });

            showPolicyGroupError("Policy Group "+editedPolicyGroup+" saved successfully ");
        },
        error: function () {
            showPolicyGroupError("Error occurred while saving the Policy Group data")
        }
    });
}


function updatePolicyGroup() {
    var policyGroupName = $('#policyGroupName').val();
    var throttlingTier = $('#throttlingTier').val();
    var anonymousAccessToUrlPattern = $('#anonymousAccessToUrlPattern').val();
    var userRoles = $('#userRoles').val(); 
    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/policyGroup/details/update',
        type: 'POST',
        data: {
            "policyGroupName": policyGroupName,
            "throttlingTier": throttlingTier,
            "userRoles": userRoles,
            "anonymousAccessToUrlPattern": anonymousAccessToUrlPattern,
            "policyGroupId":editedPolicyGroup
        },
        success: function (data) { 
            showPolicyGroupError("Policy Group "+editedPolicyGroup+" updated successfully");
        },
        error: function () {
            showPolicyGroupError("Error occurred while saving the Policy Group data")
        }
    });
}


$(document).on("click", "#btn-policy-save", function () {
    if (editedPolicyGroup == 0) {
        savePolicyGroup();
    }
    else {
        updatePolicyGroup();
    }
});

function showPolicyGroupError(text) {
    $('#policyGroup-notification-text').show();
    $('#policyGroup-notification-text-data').html(text);
}
function hidePolicyGroupError() {
    $('#policyGroup-notification-text').hide();
}


$(document).on("click", "#btn-add-policy-group", function () {
    hidePolicyGroupError();
});

$.each(policyGroupsArray, function (index, obj) {
    if (obj != null) {
        $('#policyGroupsTable tbody').append('<tr><td>' + obj.policyGroupId + '</td><td>' + obj.policyGroupName + '</td><td><a data-target="#entitlement-policy-editor" data-toggle="modal" data-policy-id="' + obj.policyGroupId + '" class="policy-edit-button"><i class="icon-edit"></i></a> &nbsp;<a  data-policy-name="' + obj.policyGroupName + '"  data-policy-id="' + obj.policyGroupId + '" class="policy-delete-button"><i class="icon-trash"></i></a></td></tr>');
    }
});

