var entitlementPolicies = new Array();

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
        url: '/publisher/api/entitlement/policy/'+policyId
        ,
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

function setPolicyContent(policyContent){
    $('#entitlement-policy-editor #policy-content').val(policyContent);
}