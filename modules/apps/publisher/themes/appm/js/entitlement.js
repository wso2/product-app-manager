var entitlementPolicies = new Array();

policyPartialsArray = new Array();

var editedpolicyPartialId = 0;

var saveAndClose = false;






var tags =[];

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
        alert("fields cannot be blank");
        return;
    }
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation,
                                                            displayValidationRequestException);


});

$(document).on("click", "#btn-policy-partial-validate", function () {


    var policyContent = editor.getValue();
    var policyName = $('#entitlement-policy-editor #policy-name').val();

    if(policyContent == "" || policyName == ""){
        alert("fields cannot be blank");
        return;
    }

    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation, function(){});
    saveAndClose = false;

})

$(document).on("click", "#btn-policy-save-and-close", function () {

   // $('#entitlement-policy-editor #save-and-close').val("YES");
    saveAndClose = true;
    var policyContent = editor.getValue();
    var policyName = $('#entitlement-policy-editor #policy-name').val();

    if(policyContent == "" || policyName == ""){
        alert("fields cannot be blank");
        return;
    }
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation,
        displayValidationRequestException);

    $("#entitlement-policy-editor").modal('hide');
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

            var validationErrorMessage = "Policy is valid."
            $('#entitlement-policy-editor #notification-text').text(validationErrorMessage);

            if(saveAndClose){
                $("#entitlement-policy-editor").modal('hide');
               // alert("hi");
            }

            return;
        }else{
            var validationErrorMessage = "Policy is not valid."
            $('#entitlement-policy-editor #notification-text').text(validationErrorMessage);
        }

    }else{
        var failureMessage = "Could not complete validation."
        $('#entitlement-policy-editor #notification-text').text(failureMessage);
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

function savePolicyPartial(){

    var policyPartial = editor.getValue(); //$('#entitlement-policy-editor #policy-content').val();
   // alert(policyPartial);
    var policyPartialName = $('#entitlement-policy-editor #policy-name').val();


    if(editedpolicyPartialId == 0){ //add

        $.ajax({
            url: '/publisher/api/entitlement/policy/partial/save',
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            data:{"policyPartialName":policyPartialName,"policyPartial":policyPartial},
            success: function(data){
                var returnedId = JSON.parse(data).response.id;
                editedpolicyPartialId = returnedId;
                policyPartialsArray.push({id: returnedId, policyPartialName: policyPartialName, policyPartial: policyPartial });
                updatePolicyPartial()
            },
            error: function(){}
        });

    }else{ // update

        $.ajax({
            url: '/publisher/api/entitlement/policy/partial/update',
            type: 'PUT',
            contentType: 'application/json',
            dataType: 'json',
            data:JSON.stringify({"id": editedpolicyPartialId, "policyPartialName":policyPartialName,"policyPartial":policyPartial}),
            success: function(data){
            },
            error: function(){

            }
        });


        $.each(policyPartialsArray, function( index, obj ) {
            if(obj!= null && obj.id == editedpolicyPartialId){
                policyPartialsArray[index].policyPartialName = policyPartialName;
                policyPartialsArray[index].policyPartial = policyPartial;

                updatePolicyPartial();
                return false;
            }
        });

    }

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


function updatePolicyPartial(){
    $('#policyPartialsTable tbody').html("");
    $(".policy-partial-dropdown").html("");
    var policyPartialIndexArray = [];



    $.each(policyPartialsArray, function( index, obj ) {
       if(obj != null){
           $('#policyPartialsTable tbody').append('<tr><td>' + obj.policyPartialName + '</td><td><a data-target="#entitlement-policy-editor" data-toggle="modal" data-policy-id="'+ obj.id +'" class="policy-edit-button"><i class="icon-edit"></i></a> &nbsp;<a  data-policy-name="'+ obj.policyPartialName +'"  data-policy-id="'+ obj.id +'" class="policy-delete-button"><i class="icon-trash"></i></a></td></tr>');
           $(".policy-partial-dropdown").append("<li> \
               <table> \
               <tr> \
                   <td><small>Permit</small></td> \
                   <td><small>Deny</small></td> \
                   <td rowspan='2' style='background-color: #ffffff'>" + obj.policyPartialName + "</td> \
               </tr> \
               <tr> \
               <td><input class='policy-allow-cb policy-allow-cb"+ obj.id +"' data-policy-id='" + obj.id + "' type='checkbox'></td> \
               <td><input class='policy-deny-cb policy-deny-cb"+ obj.id +"' data-policy-id='" + obj.id + "'  type='checkbox'></td> \
                </tr> \
               </table></li>");

           policyPartialIndexArray.push(obj.id);

       }


    });

    if(policyPartialsArray == null || policyPartialsArray[0] == null || policyPartialsArray.length == 0 ){
        $(".dropdown").hide();
    }else{
        $(".dropdown").show()
    }

    $('#uritemplate_policyPartialIds').val(JSON.stringify(policyPartialIndexArray));


}


$(document).on("click", "#btn-add-xacml-policy", function () {

    editedpolicyPartialId = 0;
    //$('#entitlement-policy-editor #policy-content').val("");
    editor.setValue("");
    $('#entitlement-policy-editor #policy-name').val("");
    $('#entitlement-policy-editor #notification-text').text("");

});


$(document).on("click", ".policy-edit-button", function () {

    var policyId = $(this).data( "policyId");
    //$('#entitlement-policy-editor #policy-content').val("");
    editor.setValue("");
    $('#entitlement-policy-editor #policy-name').val("");
    $('#entitlement-policy-editor #notification-text').text("");

    $.each(policyPartialsArray, function( index, obj ) {
       if(obj!= null && obj.id == policyId){
          // $('#entitlement-policy-editor #policy-content').val(obj.policyPartial);
           $('#entitlement-policy-editor #policy-name').val(obj.policyPartialName);
           editor.setValue(obj.policyPartial);

       }


    });


    editedpolicyPartialId = policyId;


});



$(document).on("click", ".policy-delete-button", function () {

    var policyName = $(this).data( "policyName");

    var conf = confirm("Are you sure you want to delete the policy "+ policyName +"?");
    if (conf == true) {
        var policyId = $(this).data( "policyId");


       deleteEntitlementPolicyPartial(policyId);

        $.each(policyPartialsArray, function( index, obj ) {
            if(obj!= null && obj.id == policyId){
                delete policyPartialsArray[index];
                updatePolicyPartial();
                return;
            }

        });

        updatePolicyPartial();
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

    $('#dropdown_entitlementPolicyPartialMappings'+ resourcesId +' li').each(function(i, li) {

            if($(this).find('.policy-allow-cb').prop('checked')){
                policyArray.push({"entitlementPolicyPartialId":$(this).find('.policy-allow-cb').data( "policyId"), "effect":"Permit"});
            }

            if($(this).find('.policy-deny-cb').prop('checked')){
                policyArray.push({"entitlementPolicyPartialId":$(this).find('.policy-deny-cb').data( "policyId"), "effect":"Deny"});
            }

    });


    $('#uritemplate_entitlementPolicyPartialMappings'+ resourcesId).val(JSON.stringify(policyArray));

}




$('#entitlement-policy-editor').on('shown', function() {
    editor.refresh()
});
