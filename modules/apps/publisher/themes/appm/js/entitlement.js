var entitlementPolicies = new Array();

// UI events
$(document).on("click", "#btn-policy-save", function () {

    var policyContent = $('#entitlement-policy-editor #policy-content').val();
    validatePolicyPartial(policyContent, continueAddingEntitlementPolicyPartialAfterValidation, 
                                                            displayValidationRequestException);
     
})

$(document).on("click", "#btn-policy-partial-validate", function () {

    var policyContent = $('#entitlement-policy-editor #policy-content').val();
    validatePolicyPartial(policyContent, function(){}, function(){});
     
})

$(document).on("click", "#btn-policy-save-and-close", function () {
    
    $('#entitlement-policy-editor #save-and-close').val("YES");
    var policyContent = $('#entitlement-policy-editor #policy-content').val();
    validatePolicyContent(policyContent, continueAddingEntitlementPolicyAfterValidation, 
                                                            displayValidationRequestException);
     
})

function continueAddingEntitlementPolicyPartialAfterValidation(response){

    var response = JSON.parse(response);

    if(response.success){
        response = response.response; // Abusing the name response :-P

        if(response.isValid){
            savePolicyPartial();

            if(shouldCloseAfterSave()){
                $("#entitlement-policy-editor").modal('hide');    
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

    var policyContent = $('#entitlement-policy-editor #policy-content').val();
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
        data:"cookie=test",
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

    var policyPartial = $('#entitlement-policy-editor #policy-content').val();

    $.ajax({
        url: '/publisher/api/entitlement/policy',
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data:{"policyPartial":policyPartial},
        success: function(){},
        error: function(){}
    });
}

function setPolicyContent(policyContent){
    $('#entitlement-policy-editor #policy-content').val(policyContent);
}