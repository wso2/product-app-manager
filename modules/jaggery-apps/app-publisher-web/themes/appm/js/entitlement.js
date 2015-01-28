var entitlementPolicies = new Array();

policyPartialsArray = new Array();

var editedpolicyPartialId = 0;

var saveAndClose = false;

var tags =[];
function showEntitlementError(text){
    $('#notification-text').show();
    $('#notification-text-data').html(text);
}
function hideEntitlementError() {
    $('#notification-text').hide();
}
function completeAfter(cm, pred) {
    var cur = cm.getCursor();
    if (!pred || pred()) setTimeout(function() {
        if (!cm.state.completionActive)
            cm.showHint({completeSingle: false});
    }, 100);
    return CodeMirror.Pass;
}

function completeIfAfterLt(cm) {
    return completeAfter(cm, function() {
        var cur = cm.getCursor();
        return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
    });
}

function completeIfInTag(cm) {
    return completeAfter(cm, function() {
        var tok = cm.getTokenAt(cm.getCursor());
        if (tok.type == "string" && (!/['"]/.test(tok.string.charAt(tok.string.length - 1)) || tok.string.length == 1)) return false;
        var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
        return inner.tagName;
    });
}

var editor = CodeMirror.fromTextArea(document.getElementById("policy-content"), {
    mode: "xml",
    lineNumbers: true,
    extraKeys: {
        "'<'": completeAfter,
        "'/'": completeIfAfterLt,
        "' '": completeIfInTag,
        "'='": completeIfInTag,
        "Ctrl-Space": "autocomplete"
    },
    hintOptions: {schemaInfo: tags}
});





// UI events
$(document).on("click", "#btn-policy-save", function () {

    saveAndClose = false;
    var policyContent = editor.getValue();

    var policyName = $('#entitlement-policy-editor #policy-name').val();

    if(policyContent == "" || policyName == ""){
        showEntitlementError("fields cannot be blank");
        return;
    }
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation, displayValidationRequestException);


});

$(document).on("click", "#btn-policy-partial-validate", function () {


    var policyContent = editor.getValue();
    var policyName = $('#entitlement-policy-editor #policy-name').val();

    if(policyContent == "" || policyName == ""){
        showEntitlementError("fields cannot be blank");
        return;
    }

    saveAndClose = false;
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation, displayValidationRequestException);


})

$(document).on("click", "#btn-policy-save-and-close", function () {

    // $('#entitlement-policy-editor #save-and-close').val("YES");
    saveAndClose = true;
    var policyContent = editor.getValue();
    var policyName = $('#entitlement-policy-editor #policy-name').val();

    if(policyContent == "" || policyName == ""){
        showEntitlementError("fields cannot be blank");
        return;
    }

    saveAndClose = true;
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation,
        displayValidationRequestException);


    // $("#entitlement-policy-editor").modal('hide');
    //editor.setValue("");

});


$(document).on("click", ".mclose-button", function () {
    editor.setValue("");
});


function continueAddingEntitlementPolicyPartialAfterValidation(response){

    var response = JSON.parse(response);

    if(response.success){
        response = response.response; // Abusing the name response :-P

        if(response.isValid){
            savePolicyPartial();

            showEntitlementError("Policy is valid.");

            $('.wr-overview-policies').css('display', 'block');
            $('.wr-overview-policies').parent().find('i').removeClass('icon-chevron-sign-right');
            $('.wr-overview-policies').parent().find('i').addClass('icon-chevron-sign-down');


            if(saveAndClose){
                $("#entitlement-policy-editor").modal('hide');
                // alert("hi");
            }

            return;
        }else{
            showEntitlementError("Policy is not valid.");
        }

    }else{
        showEntitlementError("Could not complete validation.");
    }


}

function shouldCloseAfterSave(){
    return $('#entitlement-policy-editor #save-and-close').val() == "YES";
}

function displayValidationRequestException(){
    showAlert('Error occured while validating the policy', 'error');
}

function deleteEntitlementPolicy (resourceIndex) {

    // Clear policy id hidden field.
    var policyIdElementName = "uritemplate_entitlementPolicyId" + resourceIndex;
    $('#resource_tbody input[name="' + policyIdElementName + '"]').val(null);

    // Remove policy content.
    entitlementPolicies[resourceIndex] = null;
}

function invalidateEntitlementPolicy(resourceIndex){
    entitlementPolicies.splice(resourceIndex, 1);
};

function preparePolicyEditor(resourceIndex){

    $("#entitlement-policy-editor #resource-index").val(resourceIndex);

    // Populate exiting content.
    var policy = entitlementPolicies[resourceIndex];

    var policyContent = "";
    if(policy){
        policyContent = policy["content"];

        if(!policyContent){
            policyContent = "";
        }
        setPolicyContent(policyContent);
    }else{
        var policyId = getPolicyId(resourceIndex);
        var policyContent = fetchPolicyContent(policyId);
        if(policyContent != null){
            var policy = new Object();
            policy["id"] = policyId;
            policy["content"] = policyContent;
            entitlementPolicies[resourceIndex] = policy;
        }
    }
}

function addEntitlementPolicy(){

    var policyContent = editor.getValue(); //$('#entitlement-policy-editor #policy-content').val();
    var resourceIndex = $("#entitlement-policy-editor #resource-index").val();

    // Set policy Id. TODO : Optimize for policy edit.
    var policyId = getPolicyId(resourceIndex);
    var policyIdElementName = "uritemplate_entitlementPolicyId" + resourceIndex;
    $('#resource_tbody input[name="' + policyIdElementName + '"]').val(policyId);

    var policy = new Object();
    policy["id"] = policyId;
    policy["content"] = policyContent;

    entitlementPolicies[resourceIndex] = policy;
}

function getPolicyId(resourceIndex){

    // Check whether there is a policy id.
    var policyIdElementName = "uritemplate_entitlementPolicyId" + resourceIndex;
    var policyId = $('#resource_tbody input[name="' + policyIdElementName + '"]').val();

    if(!policyId){
        policyId = createPolicyId();
    }

    return policyId;

}

function createPolicyId(){
    return "appm_" + createGuid();
}

function createGuid()
{
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function fetchPolicyContent(policyId){

    $.ajax({
        url: '/publisher/api/entitlement/policy/'+policyId,
        type: 'GET',
        contentType: 'application/json',
        success: function(response) {
            if(response != null){
                setPolicyContent(response)
            }
        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}

function validatePolicyPartial(policyPartial, onSuccess, onError){

    $.ajax({
        url: '/publisher/api/entitlement/policy/validate',
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data:{"policyPartial":policyPartial},
        success: onSuccess,
        error: onError
    });
}

function savePolicyPartial() {

    var policyPartial = editor.getValue();
    var policyPartialName = $('#entitlement-policy-editor #policy-name').val();
    var provider = $('#overview_provider').val();
    var isSharedPartial = false;
    if ($('#shared-partial').is(':checked')) {
        isSharedPartial = true;
    }

    if (editedpolicyPartialId == 0) { //add

        $.ajax({
            url: '/publisher/api/entitlement/policy/partial/save',
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            data: {
                "policyPartialName": policyPartialName,
                "policyPartial": policyPartial,
                "isSharedPartial": isSharedPartial
            },
            success: function (data) {
                var returnedId = JSON.parse(data).response.id;
                editedpolicyPartialId = returnedId;
                policyPartialsArray.push({
                    id: returnedId,
                    policyPartialName: policyPartialName,
                    policyPartial: policyPartial,
                    isShared: isSharedPartial,
                    author: provider
                });
                updatePolicyPartial()
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

        if (policyPartialObj.isShared) {
            $.ajax({
                async: false,
                url: '/publisher/api/entitlement/get/apps/associated/to/policy/' + editedpolicyPartialId,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                success: function (response) {

                    var apps = "";
                    if (response.length != 0) {
                        // construct and show the  the warning message with app names which use this partial before update
                        for (var i = 0; i < response.length; i++) {
                            var j = i + 1;
                            apps = apps + j + ". " + response[i].appName + "\n";
                        }

                        var msg = "policy " + policyPartialName + " is used in following apps\n\n" +
                            apps +
                            "\nAre you sure you want to modify the policy " + policyPartialName + "?";

                        var conf = confirm(msg);
                        if (conf == true) {
                            updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial);
                        }
                    }

                },
                error: function (response) {
                }
            });

        } else {
            updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial);
        }


    }


}

function updateModifiedPolicyPartial(editedpolicyPartialId, policyPartialName, policyPartial, isSharedPartial) {
    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/update',
        type: 'PUT',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify({
            "id": editedpolicyPartialId,
            "policyPartial": policyPartial,
            "isSharedPartial": isSharedPartial
        }),
        success: function (data) {
            if (JSON.parse(data)) {
                $.each(policyPartialsArray, function (index, obj) {
                    if (obj != null && obj.id == editedpolicyPartialId) {
                        policyPartialsArray[index].policyPartialName = policyPartialName;
                        policyPartialsArray[index].policyPartial = policyPartial;
                        policyPartialsArray[index].isShared = isSharedPartial;
                        updatePolicyPartial();
                    }
                });
            } else {
                alert("Couldn't modify .This partial is being used by web apps ");
            }
        },
        error: function () {
        }
    });
}

function getEntitlementPolicyPartial(policyPartialId){

    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/getContent/'+policyPartialId,
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function(response) {

            showAlert(response)

        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });


}

function getApplicationPolicyPartialList(applicationId){



    $.ajax({
        url: '/publisher/api/entitlement/policy/partialList/'+applicationId,
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function(response) {

            showAlert(response)

        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}

function deleteEntitlementPolicyPartial(policyPartialId){

    $.ajax({
        url: '/publisher/api/entitlement/policy/partial/delete/'+policyPartialId,
        type: 'DELETE',
        contentType: 'application/json',
        dataType: 'json',
        success: function(response) {

            showAlert(response)

        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}

function deleteApplicationPolicyPartialMapping(applicationId,policyPartialId){

    $.ajax({
        url: '/publisher/api/entitlement/policy/app/partial/'+applicationId+'/'+policyPartialId,
        type: 'DELETE',
        contentType: 'application/json',
        dataType: 'json',
        success: function(response) {

            showAlert(response)

        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}

function saveApplicationPolicyPartialMapping(applicationId, partialIdList){

    $.ajax({
        url: '/publisher/api/entitlement/partials',
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data:{"applicationId":applicationId,"partialIdList":partialIdList},
        success: function(data){

        },
        error: function(){}
    });
}

function setPolicyContent(policyContent){
    editor.setValue(policyContent);
    //$('#entitlement-policy-editor #policy-content').val(policyContent);
}


function updatePolicyPartial() {
    $('#policyPartialsTable tbody').html("");
    $(".policy-partial-dropdown").html("");
    var policyPartialIndexArray = [];
    var provider = $('#overview_provider').val();

    var policyPartialDropdownText = "";

    policyPartialDropdownText = ("\
               <li><table> \
               <tr> \
               <th  style='padding-left:10px; width:80px'>Policy</th> \
               <th  style='padding-left:10px; width:50px'>Permit</th> \
               <th  style='padding-left:10px; width:50px'>Deny</th> \
               </tr> \
              ");


    $.each(policyPartialsArray, function (index, obj) {
        if (obj != null) {

            if (obj.isShared) {
                if (provider == obj.author) {
                    $('#policyPartialsTable tbody').append('<tr><td>' + obj.policyPartialName + '</td><td>YES</td><td>' + obj.author + '</td><td><a data-target="#entitlement-policy-editor" data-toggle="modal" data-policy-id="' + obj.id + '" class="policy-edit-button"><i class="icon-edit"></i></a> &nbsp;<a  data-policy-name="' + obj.policyPartialName + '"  data-policy-id="' + obj.id + '" class="policy-delete-button"><i class="icon-trash"></i></a></td></tr>');
                } else {
                    $('#policyPartialsTable tbody').append('<tr><td>' + obj.policyPartialName + '</td><td>YES</td><td>' + obj.author + '</td><td><a data-target="#entitlement-policy-editor" data-toggle="modal" data-policy-id="' + obj.id + '" class="policy-edit-button"><i class="icon-info-sign"></i></td></tr>');
                }
            } else {
                $('#policyPartialsTable tbody').append('<tr><td>' + obj.policyPartialName + '</td><td>NO</td><td>' + obj.author + '</td><td><a data-target="#entitlement-policy-editor" data-toggle="modal" data-policy-id="' + obj.id + '" class="policy-edit-button"><i class="icon-edit"></i></a> &nbsp;<a  data-policy-name="' + obj.policyPartialName + '"  data-policy-id="' + obj.id + '" class="policy-delete-button"><i class="icon-trash"></i></a></td></tr>');
            }
            policyPartialDropdownText += ("\
               <tr style='padding-bottom: 20px'> \
               <td style='padding-left:10px; width:80px'>" + obj.policyPartialName + "</td> \
               <td style='padding-left: 30px;'><input class='policy-allow-cb policy-allow-cb"+ obj.id +"' data-policy-id='" + obj.id + "' type='checkbox'></td> \
               <td style='padding-left: 30px;'> <input class='policy-deny-cb policy-deny-cb"+ obj.id +"' data-policy-id='" + obj.id + "'  type='checkbox'></td> \
                </tr>");

            policyPartialIndexArray.push(obj.id);

        }




    });

    policyPartialDropdownText += ("</table></li>");
    $(".policy-partial-dropdown").html(policyPartialDropdownText);




    $.each(policyPartialsArray, function (index, obj) {
        var uriTemplates = $('.uritemplate_entitlementPolicyPartialMappings_text').length



        $('.uritemplate_entitlementPolicyPartialMappings_text').each(function(i, obj) {
            var values = JSON.parse($(this).val());

            if(values) {
                for (var j = 0; j < values.length; j++) {
                    if(values[j]){

                        if (values[j].effect == "Permit") {
                            $('#dropdown_entitlementPolicyPartialMappings' + (uriTemplates - i - 1) + " .policy-allow-cb" + values[j].entitlementPolicyPartialId).prop('checked', true);
                        } else if (values[j].effect == "Deny") {

                            $('#dropdown_entitlementPolicyPartialMappings' + (uriTemplates - i - 1) + " .policy-deny-cb" + values[j].entitlementPolicyPartialId).prop('checked', true);
                        }

                    }

                }
            }

        });


    });





    if(policyPartialsArray ==  null || policyPartialsArray.length == 0 ){
        $(".dropdown").hide();
    }else{
        $(".dropdown").show()
    }

    $('#uritemplate_policyPartialIds').val(JSON.stringify(policyPartialIndexArray));


}


$(document).on("click", "#btn-add-xacml-policy", function () {

    editedpolicyPartialId = 0;
    $('#entitlement-policy-editor #policy-name').prop("readonly", false);

    $('#entitlement-policy-editor #policy-content').val("");
    // editor.setValue("dhdfh");
    getXacmlPolicyTemplate();

    $('#entitlement-policy-editor #policy-name').val("");
    hideEntitlementError();

});

function getXacmlPolicyTemplate(){

    $.ajax({
        url: '/publisher/api/xacmlpolicy',
        type: 'GET',

        dataType: "text",
        success: function(response) {
            if(response != null){
                editor.setValue(response);
                $('#entitlement-policy-editor #policy-content').val(response);
            }
        },
        error: function(response) {
            showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });
}


$(document).on("click", ".policy-edit-button", function () {
    var provider = $('#overview_provider').val();
    var policyId = $(this).data("policyId");
    editor.setValue("");
    $('#entitlement-policy-editor #policy-name').val("");
    hideEntitlementError();


    $.each(policyPartialsArray, function (index, obj) {
        if (obj != null && obj.id == policyId) {
            $('#entitlement-policy-editor #policy-name').val(obj.policyPartialName);
            $('#entitlement-policy-editor #policy-name').prop("readonly", true);

            editor.setValue(obj.policyPartial);

            if (obj.isShared) { // Shared partial
                if (obj.isShared && provider != obj.author) {
                    //hide the buttons if not author
                    $('#entitlement-policy-editor .modal-footer').hide();
                } else {
                    //select the check box and show the buttons for author of partial
                    $('#entitlement-policy-editor .modal-footer #shared-partial').selected(true);
                    $('#entitlement-policy-editor .modal-footer').show();
                }
            } else { // private partial
                //uncheck the checkbox
                $('#entitlement-policy-editor .modal-footer #shared-partial').selected(false);
                $('#entitlement-policy-editor .modal-footer').show();
            }
        }
    });


    editedpolicyPartialId = policyId;


});


$(document).on("click", ".policy-delete-button", function () {
4
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
            url: '/publisher/api/entitlement/get/apps/associated/to/policy/' + policyId,
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
                    var msg = "policy " + policyName + " is used in following apps\n\n" +
                        apps +
                        "\nAre you sure you want to delete the policy " + policyName + "?";
                    conf = confirm(msg);


                } else {
                    conf = confirm("Are you sure you want to delete the policy " + policyName + "?");
                }

            },
            error: function (response) {

            }
        });

    } else {
        conf = confirm("Are you sure you want to delete the policy " + policyName + "?");
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
                showAlert('Error occured while fetching entitlement policy content', 'error');
            }
        });

    }

});



$(document).on("click", ".policy-deny-cb", function () {

    $(this).parent().siblings('td').children( ".policy-allow-cb" ).prop('checked', false);
    policyId = $(this).data( "policyId");
    resourcesId = $(this).parent().parent().parent().parent().parent().parent().data( "resourceId");
    updatePolcyparialForresource(resourcesId);

});


$(document).on("click", ".policy-allow-cb", function () {
    $(this).parent().siblings('td').children( ".policy-deny-cb" ).prop('checked', false);
    policyId = $(this).data( "policyId");
    resourcesId = $(this).parent().parent().parent().parent().parent().parent().data( "resourceId");
    updatePolcyparialForresource(resourcesId);
});


function updatePolcyparialForresource(resourcesId){

    policyArray = [];

    $('#dropdown_entitlementPolicyPartialMappings'+ resourcesId +' li table tr').each(function(i, li) {

        if($(this).find('.policy-allow-cb').prop('checked')){
            policyArray.push({"entitlementPolicyPartialId":$(this).find('.policy-allow-cb').data( "policyId"), "effect":"Permit"});
        }

        if($(this).find('.policy-deny-cb').prop('checked')){
            policyArray.push({"entitlementPolicyPartialId":$(this).find('.policy-deny-cb').data( "policyId"), "effect":"Deny"});
        }

    });


    $('#uritemplate_entitlementPolicyPartialMappings'+ resourcesId).val(JSON.stringify(policyArray));

}




$('#entitlement-policy-editor').on('shown.bs.modal', function() {
    editor.refresh();
});



$(document).on("click", ".btn-reset", function () {

    policyPartialsArray = new Array();
    updatePolicyPartial();
    $('#roles').tokenInput("clear");
    $('#tag-test').tokenInput("clear");


});