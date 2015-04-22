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


var editedPolicyGroup = 0; //contains status (if edit or save)
var policyGroupsArray = new Array(); //policy group related details array
var policyGroupBlock; //contains html formatted options list of Policy Groups
var policyPartialsArray = new Array();

$('#userRoles').tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
    theme: 'facebook',
    preventDuplicates: true,
    onAdd: function (role) {
    },
    onDelete: function (role) {
    }
});

/** validate data before saving
 *
 * @param policyGroupName :Policy Group Name
 * @returns {boolean} :if successfully validated returns true else returns false
 */
function validate(policyGroupName) {
    var result = true;
    if (policyGroupName == "") {
        showPolicyGroupNotification(($('#lblPolicyGroupName').text() + " field cannot be blank"), "alert-danger");
        result = false;
    }
    return result;
}

/**
 * Save policy group
 * @param policyGroupName :Policy Group Name
 * @param throttlingTier :Throttling Tier
 * @param anonymousAccessToUrlPattern : if anonymous access allowed for the related url pattern/verb
 * @param userRoles : User Roles
 * @param appliedXacmlRules : Applied XACML rules.
 * @param policyGroupDesc : Policy Group DEscription
 * @param isSaveAndClose : check if the call is from the save and close button
 */
function insertPolicyGroup( policyGroupName, throttlingTier, anonymousAccessToUrlPattern, userRoles, appliedXacmlRules, isSaveAndClose ,policyGroupDesc) {

    $.ajax({
        async: false,
        url: '/publisher/api/entitlement/policy/partial/policyGroup/save',
        type: 'POST',
        data: {
            "policyGroupName": policyGroupName,
            "throttlingTier": throttlingTier,
            "userRoles": userRoles,
            "anonymousAccessToUrlPattern": anonymousAccessToUrlPattern,
            "objPartialMappings": JSON.stringify(appliedXacmlRules),
            "policyGroupDesc" :policyGroupDesc
        },
        success: function (data) {
            editedPolicyGroup = JSON.parse(data).response.id;
            var policyPartialsMapping = [];
            
            for(var i=0; i < appliedXacmlRules.length; i++){
                var POLICY_PARTIAL_ID = appliedXacmlRules[i];
                policyPartialsMapping.push({
                    "POLICY_GRP_ID": editedPolicyGroup,
                    "POLICY_PARTIAL_ID": POLICY_PARTIAL_ID
                });
            }
            policyGroupsArray.push({
                policyGroupId: editedPolicyGroup,
                policyGroupName: policyGroupName,
                throttlingTier: throttlingTier,
                anonymousAccessToUrlPattern: anonymousAccessToUrlPattern,
                userRoles: userRoles,
                policyPartials: appliedXacmlRules,
                policyGroupDesc: policyGroupDesc

            });
            //Policy Group partial update
            updatePolicyGroupPartial(policyGroupsArray);
            $('#policy-group-editor #policyGroupName').prop("readonly", true);
            showPolicyGroupNotification("Policy Group - " + policyGroupName + " saved successfully ", "alert-success");

            //close the modal if the call is from Save and close button
            if (isSaveAndClose) {
                $("#policy-group-editor").modal('hide');
            }
        },
        error: function () {
            showPolicyGroupNotification("Error occurred while saving the Policy Group data");
        }
    });
}


/**
 * Update policy group
 * @param policyGroupName :Policy Group Name
 * @param throttlingTier :Throttling Tier
 * @param anonymousAccessToUrlPattern : if anonymous access allowed for the related url pattern/verb
 * @param userRoles : User Roles
 * @param objPartialMappings : Object which contains XACML policy partial details arrays
 * @param isSaveAndClose : check if the call is from the save and close button
 */
function updatePolicyGroup(policyGroupName, throttlingTier, anonymousAccessToUrlPattern, userRoles, objPartialMappings, isSaveAndClose, policyGroupDesc) {
    $.ajax({
        async: false,
        url: '/publisher/api/entitlement/policy/partial/policyGroup/details/update',
        type: 'POST',
        data: {
            "policyGroupName": policyGroupName,
            "throttlingTier": throttlingTier,
            "userRoles": userRoles,
            "anonymousAccessToUrlPattern": anonymousAccessToUrlPattern,
            "policyGroupId": editedPolicyGroup,
            "objPartialMappings": JSON.stringify(objPartialMappings),
            "policyGroupDesc" :policyGroupDesc
        },
        success: function (data) {
            var policyPartialsMapping = [];

            for(var i=0; i < objPartialMappings.length; i++){
                var ruleId = objPartialMappings[i];
                policyPartialsMapping.push({
                    "POLICY_GRP_ID": editedPolicyGroup,
                    "POLICY_PARTIAL_ID": ruleId
                });
            }
            for(var i=0; i<policyGroupsArray.length;i++){
                if(policyGroupsArray[i].policyGroupId == editedPolicyGroup){
                    policyGroupsArray[i].throttlingTier= throttlingTier;
                    policyGroupsArray[i].anonymousAccessToUrlPattern=anonymousAccessToUrlPattern;
                    policyGroupsArray[i].userRoles=userRoles;
                    policyGroupsArray[i].policyPartials= JSON.stringify(policyPartialsMapping);
                    policyGroupsArray[i].policyGroupDesc= policyGroupDesc;
                }
            }
            updatePolicyGroupPartial(policyGroupsArray);
            showPolicyGroupNotification("Policy Group - " + policyGroupName + " updated successfully", "alert-success");

            //close the modal if the call is from Save and close button
            if (isSaveAndClose) {
                $("#policy-group-editor").modal('hide');
            }
        },
        error: function () {
            showPolicyGroupNotification("Error occurred while saving the Policy Group data");
        }
    });
}

//save button click event
$(document).on("click", "#btn-policy-group-save", function () {
    savePolicyGroupData(false);
});

//save and close button click event
$(document).on("click", "#btn-policy-group-save-and-close", function () {
    savePolicyGroupData(true);
});


/**
 * save or update data
 * @param isSaveAndClose : check if the call is from the save and close button
 */
function savePolicyGroupData(isSaveAndClose) {
    //Policy Group Name
    var policyGroupName = $('#policy-group-editor #policyGroupName').val().trim();
    //Throttling Tier
    var throttlingTier = $('#policy-group-editor #throttlingTier').val();
    //if anonymous access allowed for the related url pattern/verb
    var anonymousAccessToUrlPattern = $('#policy-group-editor #anonymousAccessToUrlPattern').val();
    //User Roles
    var userRoles = $('#policy-group-editor #userRoles').val();

    var policyGroupDesc = $('#policy-group-editor #policyGroupDescription').val();

    hidePolicyGroupNotification();

    // Get the applied XACML rule.
    // The backend logic support multiple rules per group.
    // But in this stage we go with only a single rule per group.
    var appliedXacmlRuleIds = [];
    var appliedXacmlRuleId = $('#policy-group-editor #xacml-rule').val();

    if(appliedXacmlRuleId && appliedXacmlRuleId != "-1"){
        appliedXacmlRuleIds.push(parseInt(appliedXacmlRuleId));
    }

    var result;
    if (validate(policyGroupName)) {
        // editedPolicyGroup : 0 > then insert else update
        if (editedPolicyGroup == 0) {
            insertPolicyGroup( policyGroupName, throttlingTier, anonymousAccessToUrlPattern, userRoles, appliedXacmlRuleIds, isSaveAndClose , policyGroupDesc);
        }
        else {
            updatePolicyGroup(policyGroupName, throttlingTier, anonymousAccessToUrlPattern, userRoles, appliedXacmlRuleIds, isSaveAndClose, policyGroupDesc);
        }
    }
}

//policy group edit button click event
$(document).on("click", ".policy-group-edit-button", function () {
    var policyGroupId = $(this).attr('data-policy-id');
    editedPolicyGroup = policyGroupId;
    $('#policy-group-editor #policyGroupName').prop("readonly", true);
    hidePolicyGroupNotification();
    //handling edit view on partial
    $.each(policyGroupsArray, function (index, obj) {
        if (obj != null && obj.policyGroupId == policyGroupId) {
            $('#policy-group-editor #policyGroupName').val(obj.policyGroupName);
            $('#policy-group-editor #policyGroupDescription').val(obj.policyGroupDesc);
            $("#policy-group-editor #throttlingTier").val(obj.throttlingTier);
            $("#policy-group-editor #anonymousAccessToUrlPattern").val(JSON.stringify(obj.anonymousAccessToUrlPattern));
            $('#policy-group-editor #userRoles').val(obj.userRoles);
            //clear all checkbox
            $('.policy-group-xacml-rule').each(function () {
                $(this).prop('checked', false)
            });
            //generate token input method
            $('#userRoles').tokenInput("clear");
            if (obj.userRoles != '') {
                var roletoken = obj.userRoles.split(',');
            } else {
                var roletoken = [];
            }

            for (var i = 0; i < roletoken.length; i++) {
                $('#userRoles').tokenInput("add", {id: roletoken[i], name: roletoken[i]});
            }

            var selectedVal = $('#anonymousAccessToUrlPattern').val();
            if (selectedVal == "true") {
                $('.authPolicies').hide(200);
            } else {
                $('.authPolicies').show(200);
            }
            
            prepareXACMLRulesDropdown();
            
            // Set the applied XACML policy as the selected item in the drop down.
            var appliedXacmlRules = JSON.parse(obj.policyPartials);
            if(appliedXacmlRules && appliedXacmlRules.length > 0){
                $('#policy-group-editor #xacml-rule').val(appliedXacmlRules[0]['POLICY_PARTIAL_ID'].toString());
            }

        }
    });
});

/**
 * Notification Alert
 * @param text : message text
 * @param alertType : alert type ('alert-success' or 'alert-danger')
 */
function showPolicyGroupNotification(text, alertType) {
    var alerttype = alertType,
        alerttext = $('#policyGroup-notification-text');

    if (alerttext.hasClass('alert-danger')) {
        alerttext.removeClass('alert-danger');
    } else {
        alerttext.removeClass('alert-success');
    }

    alerttext.addClass(alerttype);
    $('#policyGroup-notification-text').show();
    $('#policyGroup-notification-text-data').html(text);
}

/**
 * hide notification message
 */
function hidePolicyGroupNotification() {
    $('#policyGroup-notification-text').hide();
}

//policy group add button click event
$(document).on("click", "#btn-add-policy-group", function () {
    editedPolicyGroup = 0;
    $('#policy-group-editor #policyGroupName').val("");
    $('#policy-group-editor #policyGroupName').prop("readonly", false);
    $('#policy-group-editor #policyGroupDescription').val("");
    $('#policy-group-editor #throttlingTier').prop('selectedIndex', 0);
    $('#policy-group-editor #anonymousAccessToUrlPattern').prop('selectedIndex', 0);
    $('#policy-group-editor #userRoles').tokenInput("clear");
    prepareXACMLRulesDropdown();
    $('.authPolicies').show(200);
    hidePolicyGroupNotification();
});


/**
* If there is only one option ('none' option) in the drop down, add XACML rules to the drop down.
* Else reset the selection option to 'none'
*/
function prepareXACMLRulesDropdown(){
    if($('#policy-group-editor #xacml-rule option').length == 1){
        $.each(policyPartialsArray, function (index, obj) {
            if (obj != null) {
                $('#xacml-rule').append($('<option>', { 
                    value: obj.id,
                    text : obj.policyPartialName,
                    title: obj.description
                }));
            }
        });
    }else{
        $('#policy-group-editor #xacml-rule').val("-1");    
    }
}

/**
 * Policy Group partial update
 * This will update html after saving a policy group
 * @param policyGroupsArray
 */
function updatePolicyGroupPartial(policyGroupsArray) {
    $('#policyGroupsTable tbody').html('');
    var policyGroupIndexArray = [];

    $.each(policyGroupsArray, function (index, obj) {
        if (obj != null) {
            $('#policyGroupsTable tbody').append('<tr><td>' + obj.policyGroupName +
            '</td><td>'+ obj.policyGroupDesc +'</td><td><a data-target="#policy-group-editor" data-toggle="modal" data-policy-id="'
            + obj.policyGroupId + '" class="policy-group-edit-button"><i class="icon-edit"></i></a> &nbsp;' +
            '<a  data-policy-name="' + obj.policyGroupName + '"  data-policy-id="' + obj.policyGroupId +
            '" class="policy-group-delete-button"><i class="icon-trash"></i></a></td></tr>');

            policyGroupIndexArray.push(obj.policyGroupId);
        }
    });

    //store the list of policy group id's (will be used in save operation to map the application wise created policy groups)
    $('#uritemplate_policyGroupIds').val(JSON.stringify(policyGroupIndexArray));

    //formatted policy group option list block
    policyGroupBlock = drawPolicyGroupsDynamically();

    //update the url pattern wise policy group drop downs
    $('.policy_groups').each(function () {
        $(this).html(policyGroupBlock);
    });

    setPolicyGroupValue();
}

//handle policy group delete event
$(document).on("click", ".policy-group-delete-button", function () {
    //Policy Group Id
    var policyGroupId = $(this).attr('data-policy-id');
    //Policy Group Name
    var policyGroupName = $(this).attr('data-policy-name');
    //Application UUID
    var uuid = $("#uuid").val();
    //Application Id
    var applicationId = getApplicationId(uuid);
    deletePolicyGroup(applicationId, policyGroupId, policyGroupName);
});

/**
 * Delete policy group by id
 * @param applicationId :Application Id
 * @param policyGroupId : PolicyGroup Id
 * @param policyGroupName :Policy Group Name
 */
function deletePolicyGroup(applicationId, policyGroupId, policyGroupName) {
    var arrayIndex; //deleted index of the array
    var groupPartial;
    var conf = false;

    $.ajax({
        async: false,
        url: '/publisher/api/entitlement/policyGroup/associate/url/pattern/list/to/avoid/delete/' + policyGroupId,
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function (response) {
            var urlPattern = "";
            if (response.length != 0) {

                // construct and show the  the warning message with app names which use this partial before delete
                for (var i = 0; i < response.length; i++) {
                    var j = i + 1;
                    urlPattern = urlPattern + j + ". URL Pattern: " + response[i].urlPattern + " , HTTP Verb: "
                    + response[i].httpMethod + "\n";

                }
                var msg = "You cannot delete the policy group " + policyGroupName +
                    " because it is been used in following URl patterns \n\n" +
                    urlPattern;
                alert(msg);
                return;

            } else {
                conf = confirm("Are you sure you want to delete the policy " + policyGroupName + "?");
            }

        },
        error: function (response) {

        }
    });


    if (conf) {
        $.each(policyGroupsArray, function (index, obj) {
            if (obj != null && obj.policyGroupId == policyGroupId) {
                groupPartial = obj;
                arrayIndex = index;
                return false; // break
            }
        });

        $.ajax({
            url: '/publisher/api/entitlement/policy/partial/policyGroup/details/delete/' + applicationId + '/' + policyGroupId,
            type: 'DELETE',
            success: function (data) {
                //to remove index and value from policy array
                for (var i in policyGroupsArray) {
                    if (i == arrayIndex) {
                        policyGroupsArray.splice(i, 1);
                        break;
                    }
                }
                updatePolicyGroupPartial(policyGroupsArray);
            },
            error: function () {
                alert("Couldn't delete the Policy Group " + policyGroupName + ". This Policy Group is being used by web apps  ")
            }
        });
    }
}

/**
 * Get Application Id by passing Application UUID
 * @param uuid : Application UUID
 * @returns {string} : Application Id
 */
function getApplicationId(uuid) {
    var appid = "-1";
    $.ajax({
        url: '/publisher/api/entitlement/get/webapp/id/from/entitlements/uuid/' + uuid,
        type: 'GET',
        contentType: 'application/json',
        async: false,
        success: function (id) {
            appid = id;
        },
        error: function () {
        }
    });
    return appid;
}


/**
 * Create the html formatted block of throttling tier list
 * @returns {string} : throttling tier options list
 */
function drawThrottlingTiersDynamically() {
    var strContent = "";
    tiers.reverse();
    for (var i = 0; i < tiers.length; i++) {
        strContent += "<option title='" + tiers[i].tierDescription + "' value='" + tiers[i].tierName + "' id='" + tiers[i].tierName + "'>" + tiers[i].tierDisplayName + "</option>";
    }
    return strContent;
}

/**
 * Create the html formatted block for policy group list
 * @returns {string} : policy group options list
 */
function drawPolicyGroupsDynamically() {
    var strContent = "";
    for (var i = 0; i < policyGroupsArray.length; i++) {
        strContent += "<option title='" + policyGroupsArray[i].policyGroupName + "' value='" + policyGroupsArray[i].policyGroupId + "' id='" + policyGroupsArray[i].policyGroupId + "'>" + policyGroupsArray[i].policyGroupName + "</option>";
    }
    return strContent;
}

function updatePolicyGroupPartialXACMLPolicies(uuid){
    var policyGroupsArrayTemp = [];
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
                        policyGroupsArrayTemp.push({
                            policyGroupId: data[i].policyGroupId,
                            policyGroupName: data[i].policyGroupName,
                            throttlingTier: data[i].throttlingTier,
                            anonymousAccessToUrlPattern: data[i].allowAnonymous,
                            userRoles: data[i].userRoles,
                            policyPartials: data[i].policyPartials,
                            policyGroupDesc: data[i].policyGroupDesc
                        })
                    }
                    policyGroupsArray = arrayUnique(policyGroupsArray.concat(policyGroupsArrayTemp));
                },
                error: function () {
                }
            });
        },
        error: function () {
        }
    });
}

/**
 * Use to merge array with unique
 * @returns {Array}
 */
function arrayUnique(array) {
    var a = array.concat();
    for(var i=0; i<a.length; ++i) {
        for(var j=i+1; j<a.length; ++j) {
            if(a[i] === a[j])
                a.splice(j--, 1);
        }
    }

    return a;
};