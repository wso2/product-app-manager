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


policyPartialsArray = new Array(); // xacml policy details array
var editedpolicyPartialId = 0; //if 1 then edit else save

$(document).ready(function () {
    //load default xacml policy condition
    getXacmlPolicyTemplate();

    // Get shared partials
    $.ajax({
        url: '/publisher/api/entitlement/get/shared/policy/partial/list',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                policyPartialsArray.push({
                    id: data[i].partialId,
                    policyPartialName: data[i].partialName,
                    policyPartial: data[i].partialContent,
                    isShared: data[i].isShared,
                    author: data[i].author,
                    description: data[i].description
                });
            }
            updatePolicyPartial();
        },
        error: function () {
        }
    });
    $('#policy-name').select();
});


//clear and reset controls
function resetControls() {
    editedpolicyPartialId = 0;
    getXacmlPolicyTemplate();
    $('#policy-desc').val("");
    $('#policy-name').prop("readonly", false);
    $('#policy-name').val("");
    $('#policy-name').select();

}

//load XACML template
function getXacmlPolicyTemplate() {
    $.ajax({
        url: '/publisher/api/xacmlpolicy',
        type: 'GET',

        dataType: "text",
        success: function (response) {
            if (response != null) {
                $('#policy-content').val(response);
            }
        },
        error: function (response) {
            alert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}

//new button click event
$(document).on("click", "#btn-policy-new", function () {
    resetControls();
});

//validate event
$(document).on("click", "#btn-policy-partial-validate", function () {
    var policyContent = $('#policy-content').val();
    var policyName = $('#policy-name').val();

    if (policyName == "") {
        alert("Policy name cannot be blank", "alert-danger");
        return;
    }
    if (policyContent == "") {
        alert("Policy content cannot be blank", "alert-danger");
        return;
    }
    validatePolicyPartial(policyContent, showMessageAfterValidation, displayValidationRequestException);

});

//save event
$(document).on("click", "#btn-policy-save", function () {
    var policyContent = $('#policy-content').val();
    var policyName = $('#policy-name').val();

    if (policyName == "") {
        alert("Policy name cannot be blank", "alert-danger");
        return;
    }
    if (policyContent == "") {
        alert("Policy content cannot be blank", "alert-danger");
        return;
    }
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation, displayValidationRequestException);
    resetControls();
});

//validate the condition
function validatePolicyPartial(policyPartial, onSuccess, onError) {

    $.ajax({
        url: '/publisher/api/entitlement/policy/validate',
        type: 'POST',
        async: false,
        contentType: 'application/x-www-form-urlencoded',
        data: {"policyPartial": policyPartial},
        success: onSuccess,
        error: onError
    });
}


function continueAddingEntitlementPolicyPartialAfterValidation(response) {
    var response = JSON.parse(response);
    if (response.success) {
        response = response.response;
        if (response.isValid) {
            savePolicyPartial();
            //alert("Policy is valid.", "alert-success");
        } else {
            alert("Policy is not valid.", "alert-danger");
        }

    } else {
        alert("Could not complete validation.", "alert-danger");
    }
}


function showMessageAfterValidation(response) {
    var response = JSON.parse(response);
    if (response.success) {
        response = response.response;
        if (response.isValid) {
            alert("Policy is valid.", "alert-success");
        } else {
            alert("Policy is not valid.", "alert-danger");
        }

    } else {
        alert("Could not complete validation.", "alert-danger");
    }
}


function displayValidationRequestException() {
    alert('Error occured while validating the policy', 'error');
}


function savePolicyPartial() {

    var policyPartial = $('#policy-content').val();
    var policyPartialName = $('#policy-name').val();
    var policyPartialDesc = $('#policy-desc').val();

    var provider = "";
    var isSharedPartial = true;
    if (editedpolicyPartialId == 0) { //add

        $.ajax({
            url: '/publisher/api/entitlement/policy/partial/save',
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            async: false,
            data: {
                "policyPartialName": policyPartialName,
                "policyPartial": policyPartial,
                "isSharedPartial": isSharedPartial,
                "policyPartialDesc": policyPartialDesc
            },
            success: function (data) {
                var returnedId = JSON.parse(data).response.id;
                editedpolicyPartialId = returnedId;
                policyPartialsArray.push({
                    id: returnedId,
                    policyPartialName: policyPartialName,
                    policyPartial: policyPartial,
                    isShared: isSharedPartial,
                    author: provider,
                    description: policyPartialDesc
                });
                updatePolicyPartial()
                alert("Policy Saved Successfully");
                $('#policy-name').prop("readonly", true);
            },
            error: function () {
            }
        });

    } else { // update
        var policyPartialObj;

        $.each(policyPartialsArray, function (index, obj) {
            if (obj != null && obj.id == editedpolicyPartialId) {
                policyPartialObj = obj;
                return false; // break
            }

        });

        $.ajax({
            async: false,
            url: '/publisher/api/entitlement/get/apps/associated/to/xacml/policy/id/' + editedpolicyPartialId,
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                console.info(JSON.stringify(response));
                var apps = "";
                if (response.length != 0) {
                    // construct and show the  the warning message with app names which use this partial before update
                    for (var i = 0; i < response.length; i++) {
                        var j = i + 1;
                        apps = apps + j + ". " + response[i].appName + "\n";
                    }

                    var msg = "policy " + policyPartialName + " is used in following apps " +
                        apps +
                        "Are you sure you want to modify the policy " + policyPartialName + "?";

                    var conf = confirm(msg);
                    if (conf == true) {
                        updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial, policyPartialDesc);
                    }
                }
                else {
                    updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial, policyPartialDesc);
                }


            },
            error: function (response) {
            }
        });


    }


}


function updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial, policyPartialDesc) {
    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/update',
        type: 'PUT',
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        data: JSON.stringify({
            "id": editedpolicyPartialId,
            "policyPartial": policyPartial,
            "isSharedPartial": isSharedPartial,
            "policyPartialDesc": policyPartialDesc
        }),
        success: function (data) {
            if (JSON.parse(data)) {
                $.each(policyPartialsArray, function (index, obj) {
                    if (obj != null && obj.id == editedpolicyPartialId) {
                        policyPartialsArray[index].policyPartialName = policyPartialName;
                        policyPartialsArray[index].policyPartial = policyPartial;
                        policyPartialsArray[index].isShared = isSharedPartial;
                        policyPartialsArray[index].description = policyPartialDesc

                    }
                });
                updatePolicyPartial();
                alert("Policy Updated Successfully");
                resetControls();
            } else {
                alert("Couldn't modify .This partial is being used by web apps ");
            }
        },
        error: function () {
        }
    });
}


function updatePolicyPartial() {
    $('#policyPartialsTable tbody').html("");
    $.each(policyPartialsArray, function (index, obj) {
        if (obj != null) {

            if (obj.isShared) {
                var policyDesc = obj.description;
                if (policyDesc == null) {
                    policyDesc = "";
                }

                $('#policyPartialsTable tbody').append('<tr><td>' + obj.policyPartialName + '</td> <td>' +
                policyDesc + '</td> <td><a data-target="#entitlement-policy-editor" ' +
                'data-toggle="modal" data-policy-id="' + obj.id + '" class="policy-edit-button">' +
                '<i class="icon-edit"></i></a> &nbsp;<a  data-policy-name="' + obj.policyPartialName +
                '"  data-policy-id="' + obj.id + '" class="policy-delete-button"><i class="icon-trash"></i>' +
                '</a></td></tr>');

            }

        }
    });

}

//edit event
$(document).on("click", ".policy-edit-button", function () {
    var policyId = $(this).data("policyId");
    editedpolicyPartialId=policyId;
    $('#policy-content').val("");
    $('#policy-name').val("");
    $('#policy-desc').val("");

    $.each(policyPartialsArray, function (index, obj) {
        if (obj != null && obj.id == policyId) {
            $('#policy-name').val(obj.policyPartialName);
            $('#policy-name').prop("readonly", true);
            $('#policy-desc').val(obj.description);
            $('#policy-content').val(obj.policyPartial);


        }
    });
});


//delete event


$(document).on("click", ".policy-delete-button", function () {

    var policyName = $(this).data("policyName");
    var policyId = $(this).data("policyId");
    var policyPartial;
    var arrayIndex;
    var conf;
    $.each(policyPartialsArray, function (index, obj) {
        if (obj != null && obj.id == policyId) {
            policyPartial = obj;
            arrayIndex = index;
            return false; // break
        }

    });

    if (policyPartial.isShared) {
        $.ajax({
            async: false,
            url: '/publisher/api/entitlement/get/apps/associated/to/xacml/policy/id/' + policyId,
            type: 'GET',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                var apps = "";
                if (response.length != 0) {
                    // construct and show the  the warning message with app names which use this partial before delete
                    for (var i = 0; i < response.length; i++) {
                        var j = i + 1;
                        apps = apps + j + ". " + response[i].appName + "\n";

                    }
                    var msg = "You cannot delete the policy " + policyName + " because it is been used in following apps\n\n" +
                        apps;
                    alert(msg);
                    return;

                } else {
                    conf = confirm("Are you sure you want to delete the policy " + policyName + "?");
                }

            },
            error: function (response) {

            }
        });

    }

    if (conf == true) {

        $.ajax({

            url: '/publisher/api/entitlement/policy/partial/' + policyId,
            type: 'DELETE',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                var success = JSON.parse(response);
                if (success) {
                    delete policyPartialsArray[arrayIndex];
                    updatePolicyPartial();


                } else {
                    alert("Couldn't delete the partial.This partial is being used by web apps  ");
                }

            },
            error: function (response) {
                alert('Error occured while fetching entitlement policy content', 'error');
            }
        });

    }

});
