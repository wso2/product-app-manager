var tiers ={}; //contains Throttling tier details
var throttlingTierControlBlock; //html formatted block for throttling tiers list


$( document ).ready(function() {

    var uuid = $("#uuid").val(); //Application UUID

    $("#overview_context").attr('maxlength','200');

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

    //load throttling tiers
    $("#throttlingTier").empty().append(throttlingTierControlBlock);

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
        updateVisuals();
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

    //load global (application level) dynamic optional java policies
    loadAvailableJavaPolicies(uuid, true);

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
                    author: data[i].author,
                    description: data[i].description
                });
            }
        },
        error: function () {
        }
    });

    loadPolicyGroupData(uuid);

    $("#add_resource").click(function(){
        $(".http_verb").each(function(){
            var resource = {};
            var url_pattern = $("#url_pattern").val() == "" ? "*" : $("#url_pattern").val();
            resource.url_pattern =  url_pattern.indexOf('/') == 0 ? url_pattern : '/' + url_pattern;
            resource.http_verb = $(this).val();
            resource.user_roles = $("#user_roles").val();
            if ($(this).is(':checked')) {
                if (resource.url_pattern != "") {
                    //check if the resource is already available
                    for (var i = 0; i < RESOURCES_1.length; i++) {
                        if (resource.url_pattern == RESOURCES_1[i].url_pattern &&
                            resource.http_verb == RESOURCES_1[i].http_verb) {
                            alert("Resource is already added.")
                            return false;
                        }
                    }
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

        var conf = confirm("Are you sure you want to delete the selected resource?");
        if (conf == false) {
            return;
        }

        var i = $(this).attr("data-index");
        RESOURCES_1.splice(i, 1);

        // Invalidate relevant entitlement policy
        //invalidateEntitlementPolicy(i);

        $("#resource_tbody").trigger("draw");
    });

    $("#resource_tbody").on("draw", function () {
        $("#resource_tbody").html("");
        for (var i = 0; i < RESOURCES_1.length; i++) {
            $("#resource_tbody").prepend(
                "<tr> \
                  <td><span style='color:#999'>/{context}/{version}</span>" + RESOURCES_1[i].url_pattern + " <input type='hidden' value='" + RESOURCES_1[i].url_pattern + "' name='uritemplate_urlPattern" + i + "'/></td> \
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

        }
    });

    $(document).on("click", "#btn-policy-save", function () {
        addEntitlementPolicy();
       // $("#entitlement-policy-editor").modal('hide');
    })

    $("#resource_tbody").trigger("draw");

    //handle global policies checkbox logic
    $(document).on("click", '.controll_visibility', function () {
        if($(this).context.checked){
            $('#token-input-roles').show();
            $('.global_role>ul.token-input-list-facebook').css('border','1px solid #ccc');
        }else{
            $('#token-input-roles').hide();
            $('#roles').tokenInput("clear");
            $('.global_role>ul.token-input-list-facebook').css('border','none');
        }
    });

    if($('#roles').attr("data-roles")){
        $('.controll_visibility').prop('checked', true);
        $('#token-input-roles').show();
    }else{
        $('#token-input-roles').hide();
        $('.global_role>ul.token-input-list-facebook').css('border','none');
    }

    $(document).on("click", '.controll_overview_logoutUrl', function () {
        if($(this).context.checked){
            $('#overview_logoutUrl').show();
        } else{
            $('#overview_logoutUrl').val('');
            $('#overview_logoutUrl').hide();
        }
    })
    //set default on loading
    if($('#overview_logoutUrl').val() !=' '){
        $('.controll_overview_logoutUrl').prop('checked', true);
        $('#overview_logoutUrl').show()
    }else{
        $('#overview_logoutUrl').hide()
    }


    //set skip gateway checkbox value in edit mode
    var skipGateway = $('#overview_skipGateway').val();
    if (skipGateway == "true") {
        $('.skip_gateway_checkbox').prop('checked', true);
        $('#overview_acsUrl').show();
    }
    else {
        $('.skip_gateway_checkbox').prop('checked', false);
        $('#overview_acsUrl').hide();
    }


    //when skip gateway checkbox value in changed, adjust the hidden field value which used in save operation
    $(".skip_gateway_checkbox").click(function () {
        var output = [];
        if ($('.skip_gateway_checkbox').is(':checked')) {
            output.push("true");
            $('#overview_acsUrl').show();
        }
        else {
            output.push("false");
            $('#overview_acsUrl').hide();
        }
        $('#overview_skipGateway').val(output);
    });

    /**
     * This enable/disable role based authorization adhere with anonymous access property value
     */
    $('#anonymousAccessToUrlPattern').change(function () {
        var selectedVal = $('#anonymousAccessToUrlPattern').val();
        if (selectedVal == "true") {
            $('.authPolicies').hide(200);
        } else {
            $('.authPolicies').show(200);
        }
    });


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

/**
 * load policy group details and policy partial details
 * @param uuid : Application UUID
 */
function loadPolicyGroupData(uuid) {
    $.ajax({
        url: '/publisher/api/entitlement/get/webapp/id/from/entitlements/uuid/' + uuid,
        type: 'GET',
        async: false,
        contentType: 'application/json',
        success: function (id) {
            //get partials of web app
            $.ajax({
                url: '/publisher/api/entitlement/policy/partialList/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                async: false,
                success: function (data) {
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
                },
                error: function () {
                }
            });

            // get the entitlement policy groups
            $.ajax({
                url: '/publisher/api/entitlement/get/policy/Group/by/appId/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
                async: false,
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {

                        policyGroupsArray.push({
                            policyGroupId: data[i].policyGroupId,
                            policyGroupName: data[i].policyGroupName,
                            throttlingTier: data[i].throttlingTier,
                            anonymousAccessToUrlPattern: data[i].allowAnonymous,
                            userRoles: data[i].userRoles,
                            policyPartials: data[i].policyPartials,
                            policyGroupDesc: data[i].policyGroupDesc
                        })
                    }
                    updatePolicyGroupPartial(policyGroupsArray);
                },
                error: function () {
                }
            });

        },
        error: function () {
        }
    });
}


