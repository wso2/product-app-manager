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
        contentType: 'application/json',
        success: function(id){

            //get partials of web app
            $.ajax({
                url: '/publisher/api/entitlement/policy/partialList/' + id,
                type: 'GET',
                contentType: 'application/json',
                dataType: 'json',
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


                    for(var i=0; i< RESOURCES_1.length; i++){
                        var resourcePolicies = JSON.parse(getValidatedEntitlementPolicyId(i));

                        for(var j=0; j< resourcePolicies.length; j++){

                            if(resourcePolicies[j].effect == "Permit"){
                              $('#dropdown_entitlementPolicyPartialMappings' + i + " .policy-allow-cb" + resourcePolicies[j].entitlementPolicyPartialId).prop('checked', true);
                            }else if (resourcePolicies[j].effect == "Deny"){
                               $('#dropdown_entitlementPolicyPartialMappings' + i + " .policy-deny-cb" + resourcePolicies[j].entitlementPolicyPartialId).prop('checked', true);
                            }
                        }

                    }


                },
                error: function(){}
            });

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

    $("#resource_tbody").on("draw", function(){
        $("#resource_tbody").html("");
        for(var i=0; i< RESOURCES_1.length; i++){
            $("#resource_tbody").prepend(
                    "<tr> \
                      <td><span style='color:#999'>/{context}/{version}/</span>"+ RESOURCES_1[i].url_pattern +" <input type='hidden' value='"+RESOURCES_1[i].url_pattern+"' name='uritemplate_urlPattern"+i+"'/></td> \
                  <td><strong>"+ RESOURCES_1[i].http_verb +"</strong><input type='hidden' value='"+RESOURCES_1[i].http_verb+"' name='uritemplate_httpVerb"+i+"'/></td> \
                    <td style='padding:0px'><select name='uritemplate_tier" + i + "' onChange='updateDropdownThrottlingTier(" + i + ");' class='selectpicker' id='getThrottlingTier" + i + "' style='width:100%;border:none;'> "+ throttlingTierControlBlock +" </select></td> \
                  <td style='padding:0px'><select name='uritemplate_skipthrottle" + i + "' onChange='updateDropDownSkipThrottle(" + i + ");' class='selectpicker' id='getSkipthrottle" + i + "' style='width:100%;border:none;'><option value='False' id='False'>False</option><option value='True' id='True'>True</option></select></td> \
                     <td style='padding:0px'><select name='uritemplate_allowAnonymous" + i + "' onChange='updateDropDownAllowAnonymous(" + i + ");' class='selectpicker' id='getAllowAnonymous" + i + "' style='width:100%;border:none;'><option value='False' id='False'>False</option><option value='True' id='True'>True</option></select></td> \
                   \
                  <td> \
                     \
                <div class='dropdown'> \
                    <a href='#' data-toggle='dropdown' class='dropdown-toggle'>Add<b class='caret'></b></a>\
                    <div><ul  id='dropdown_entitlementPolicyPartialMappings"+i+"' class='dropdown-menu policy-partial-dropdown' onChange='updateAccessPolicyOptions(" + i + ");' data-resource-id='"+ i +"' style='margin: 0px;'>\
                    \
                    </ul></div>\
                </div>\
               \
                 <input type='hidden' class='uritemplate_entitlementPolicyPartialMappings_text' id='uritemplate_entitlementPolicyPartialMappings"+i+"' name='uritemplate_entitlementPolicyPartialMappings"+i+"' value='"+ getValidatedEntitlementPolicyId(i) + "'/> \
\
                  </td> \
                   \
                   <td class='userRoles' style='padding:0px'><input  type='text' name='uritemplate_userRoles"+i+"' onChange='updateUserRoles("+i+");'  id='getUserRoles"+i+"' style='width:95%;border:none;'></input></td> \
                  \
                  <td> \
                  	<a data-index='"+i+"' class='delete_resource'><i class='icon-remove-sign'></i>  Delete</a>&nbsp; \
                  </td> \
                </tr> \
				"
            );
            // roles autocomplete   
            $('#getUserRoles'+i).tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
                theme: 'facebook',
                //prePopulate: $.parseJSON(json),
                tokenDelimiter: ',',
                preventDuplicates: true
            });
            if(RESOURCES_1[i].user_roles !== undefined && RESOURCES_1[i].user_roles.indexOf(',')>-1){
                var res = RESOURCES_1[i].user_roles.split(",");
                for(var j=0; j< res.length; ++j){
                    $('#getUserRoles'+i).tokenInput("add", {id:res[j] , name:res[j]});
                    console.log(res[j]);
                }
            }
            else{
                $('#getUserRoles'+i).tokenInput("add", {id:RESOURCES_1[i].user_roles , name: RESOURCES_1[i].user_roles});
            }
            if (RESOURCES_1[i].throttling_tier !== undefined) {
                document.getElementById(RESOURCES_1[i].throttling_tier).selected="true";            }
            if(RESOURCES_1[i].skipthrottle !== undefined) {
                document.getElementById(RESOURCES_1[i].skipthrottle).selected="true";
            }

            //set Access Policy Options when adding a new resource
            if (RESOURCES_1[i].accessPolicyOptions !== undefined && RESOURCES_1[i].accessPolicyOptions !== '') {
                $('#uritemplate_entitlementPolicyPartialMappings' + i).val(RESOURCES_1[i].accessPolicyOptions);
                updatePolicyPartial();
            }


            //set Anonymous Allow option value
            if(RESOURCES_1[i].allowAnonymous !== undefined && RESOURCES_1[i].allowAnonymous !== '') {
                $('#getAllowAnonymous' + i).val(RESOURCES_1[i].allowAnonymous);
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

/*
 Fires when user change Throttling Tier
 @param index : row id
 */
function updateDropdownThrottlingTier(index) {
    var throttlingTierElement = document.getElementById("getThrottlingTier" + index);
    RESOURCES_1[index].throttling_tier = throttlingTierElement.options[throttlingTierElement.selectedIndex].value;
}

/*
 Fires when user change Skip Throttle
 @param index : row id
 */
function updateDropDownSkipThrottle(index) {
    var skipthrottleElement = document.getElementById("getSkipthrottle" + index);
    RESOURCES_1[index].skipthrottle = skipthrottleElement.options[skipthrottleElement.selectedIndex].value;
}

/*
 Fires when user change user roles
 @param index : row id
 */
function updateUserRoles(index) {
    var userRolesElement = document.getElementById("getUserRoles" + index);
    RESOURCES_1[index].user_roles = userRolesElement.value;
}

/*
 Create the html formatted block for throttling tier list
 */
function drawThrottlingTiersDynamically() {
    var strContent = "";
    tiers.reverse();
    for (var i = 0; i < tiers.length; i++) {
        strContent += "<option title='" + tiers[i].tierDescription + "' value='" + tiers[i].tierName + "' id='" + tiers[i].tierName + "'>" + tiers[i].tierDisplayName + "</option>";
    }
    return strContent;
}

/*
 Fires when user change Access Policy Options
 @param index : row id
 */
function updateAccessPolicyOptions(index) {
    var entitlementPolicyPartialMappingsElement = document.getElementById("uritemplate_entitlementPolicyPartialMappings" + index);
    RESOURCES_1[index].accessPolicyOptions = entitlementPolicyPartialMappingsElement.value;
}

/*
 Fires when user change Access Allow Anonymous option
 @param index : row id
 */
function updateDropDownAllowAnonymous(index) {
    var allowAnonymousElement = document.getElementById("getAllowAnonymous" + index);
    RESOURCES_1[index].allowAnonymous = allowAnonymousElement.options[allowAnonymousElement.selectedIndex].value;
}
