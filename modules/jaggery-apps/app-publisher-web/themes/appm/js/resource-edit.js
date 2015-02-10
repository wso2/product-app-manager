var tiers ={}; //contains Throttling tier details
var throttlingTierControlBlock; //html formatted block for throttling tiers list

$( document ).ready(function() {

    //get Tier details from tier.xml
    $.ajax({
        url: '/publisher/api/entitlement/get/Tiers',
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        success: function (data) {
            tiers = data;
            throttlingTierControlBlock = drawThrottlingTiersDynamically();
        },
        error: function () {
        }
    });

    $(".trans_checkbox").click(function(){
        var output = [];
        $( ".trans_checkbox" ).each(function( index ) {
            var value = $(this).data('value');
            if( $(this).is(':checked')){
                output.push(value);
            }
        });

        $('#overview_transports').val(output);


    });


    var transportVal = $('#overview_transports').val().split(',');
    $( ".trans_checkbox" ).each(function( index ) {
        var value = $(this).data('value');
        if($.inArray(value, transportVal) >= 0){
            $(this).prop('checked', true);
        }

    });

    $(".anonymous_checkbox").click(function(){
        var output = [];
        $( ".anonymous_checkbox" ).each(function( index ) {
            if( $(this).is(':checked')){
                output.push("TRUE");
            }
            else
            {
                output.push("FALSE");
            }
        });
        $('#overview_allowAnonymous').val(output);
     });


    var anonymousVal = $('#overview_allowAnonymous').val();
    $( ".anonymous_checkbox" ).each(function( index ) {
        if(anonymousVal == "TRUE"){
            $(this).prop('checked', true);
        }
        else
        {
            $(this).prop('checked', false);
        }
    });


   //fixed chrome issue with file paths
    $('input[type=file]').on('change', function(e) {
        var filename = $(e.currentTarget).val().replace(/^.*\\/, "");
        $(this).parent().parent().find('.txt-filepath').val(filename);
    });

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
                    author: data[i].author
                });
            }

        },
        error: function () {
        }
    });

    var uuid = $("#uuid").val();

    $.ajax({
        url: '/publisher/api/entitlement/get/webapp/id/from/entitlements/uuid/' + uuid,
        type: 'GET',
        async:false,
        contentType: 'application/json',
        success: function(id){
            //get partials of web app
            $.ajax({
                url: '/publisher/api/entitlement/policy/partialList/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                async:false,
                success: function(data){

                    for (var i = 0; i < data.length; i++) {
                        var obj = {
                            id: data[i].partialId,
                            policyPartialName: data[i].partialName,
                            policyPartial: data[i].partialContent,
                            isShared: data[i].isShared,
                            author: data[i].author
                        };
                        // avoid duplicating shared partials
                        if (!obj.isShared) {
                            policyPartialsArray.push(obj);
                        }
                    }
                    updatePolicyPartial();
                 },
                error: function(){}
            });

            // get the entitlement policy groups
            $.ajax({
                url: '/publisher/api/entitlement/get/policy/Group/by/appId/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                async:false,
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {

                        policyGroupsArray.push({
                            policyGroupId: data[i].policyGroupId,
                            policyGroupName: data[i].policyGroupName,
                            throttlingTier: data[i].throttlingTier,
                            anonymousAccessToUrlPattern: data[i].userRoles,
                            userRoles: data[i].allowAnonymous,
                            policyPartials:data[i].policyPartials
                        })
                    }
                    updatePolicyGroupPartial(policyGroupsArray);
                },
                error: function () {
                }
            });

alert(JSON.stringify(policyGroupsArray[0].policyPartials));
        },
        error: function(){}
    });




    $("#add_resource").click(function(){
        $(".http_verb").each(function(){
            var resource = {};
            resource.url_pattern = $("#url_pattern").val();
            resource.http_verb = $(this).val();
            resource.user_roles = $("#user_roles").val();
            if($(this).is(':checked')){
                if(resource.url_pattern != ""){
                    RESOURCES_1.push(resource);
                }
            }
        })

        resetResource();
        $("#resource_tbody").trigger("draw");
    });

    $("#clear_resource").click(function(){
        resetResource();
    });

    $("#resource_tbody").delegate(".delete_resource","click", function(){
        var i = $(this).attr("data-index");
        RESOURCES_1.splice(i, 1);

        // Invalidate relevant entitlement policy
        invalidateEntitlementPolicy(i);

        $("#resource_tbody").trigger("draw");
        console.log(RESOURCES_1);
    });

    $("#resource_tbody").on("draw", function () {
        $("#resource_tbody").html("");
        for (var i = 0; i < RESOURCES_1.length; i++) {
            $("#resource_tbody").prepend(
                "<tr> \
                  <td><span style='color:#999'>/{context}/{version}/</span>" + RESOURCES_1[i].url_pattern + " <input type='hidden' value='" + RESOURCES_1[i].url_pattern + "' name='uritemplate_urlPattern" + i + "'/></td> \
                  <td><strong>" + RESOURCES_1[i].http_verb + "</strong><input type='hidden' value='" + RESOURCES_1[i].http_verb + "' name='uritemplate_httpVerb" + i + "'/></td> \
                     <td style='padding:0px'><select name='uritemplate_policyGroupId" + i + "' id='uritemplate_policyGroupId" + i + "' onChange='updateDropdownPolicyGroup(" + i + ");'   class='policy_groups form-control'>" + policyGroupBlock + "</select></td>\
                   <td> \
                  	<a data-index='" + i + "' class='delete_resource'><i class='icon-remove-sign'></i>  Delete</a>&nbsp; \
                  </td> \
                </tr> \
				"
            );

            //set policy group id value
            if (RESOURCES_1[i].policyGroupId !== undefined && RESOURCES_1[i].policyGroupId !== '') {
                $('#uritemplate_policyGroupId' + i).val(RESOURCES_1[i].policyGroupId);
            }
            updatePolicyPartial();

        }
    });

    $(document).on("click", ".add_entitlement_policy", function () {
        var resourceIndex = $(this).data('index');
        preparePolicyEditorInEditMode(resourceIndex);
    })

    $(document).on("click", ".delete_entitlement_policy", function () {
        var resourceIndex = $(this).data('index');
        deleteEntitlementPolicy(resourceIndex);
    })

    $(document).on("click", "#btn-policy-save", function () {
        addEntitlementPolicy();
       // $("#entitlement-policy-editor").modal('hide');
    })

    $("#resource_tbody").trigger("draw");
});

// NOTE : This function is used as a workaround for a bug in registry model import and export.
// When the value of an attribute is "" import and export modifies as " ".
function getValidatedEntitlementPolicyId(resourceIndex){

    var policyId = RESOURCES_1[resourceIndex].entitlement_policy_id;

    if(policyId){
        return policyId.trim();
    }else{
        return "";
    }
}


function resetResource() {
    $("#url_pattern").val("");
    $(".http_verb").each(function(){
           this.checked = false;
    })
}

/**
 * Fires when user changes the policy group values
 * @param index :row id
 */
function updateDropdownPolicyGroup(index) {
    var policyGroupIdElement = document.getElementById("uritemplate_policyGroupId" + index);
    RESOURCES_1[index].policyGroupId = policyGroupIdElement.options[policyGroupIdElement.selectedIndex].value;
}

/**
 * set policy group id value
 */
function setPolicyGroupValue() {
    for (var i = 0; i < RESOURCES_1.length; i++) {
        if (RESOURCES_1[i].policyGroupId !== undefined && RESOURCES_1[i].policyGroupId !== '') {
            $('#uritemplate_policyGroupId' + i).val(RESOURCES_1[i].policyGroupId);
        }
    }
}