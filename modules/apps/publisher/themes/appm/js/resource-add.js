var tiers ={}; //contains Throttling tier details

var throttlingTierControlBlock; //html formatted block for throttling tiers list

var RESOURCES = [
    {"url_pattern":"/*", "http_verb":"GET" ,  "throttling_tier":"", "user_roles":"" },
    {"url_pattern":"/*", "http_verb":"POST"  , "throttling_tier":"", "user_roles":"" },
    {"url_pattern":"/*", "http_verb":"PUT" ,  "throttling_tier":"", "user_roles":"" },
    {"url_pattern":"/*", "http_verb":"DELETE" , "throttling_tier":"", "user_roles":"" },
    {"url_pattern":"/*", "http_verb":"OPTIONS" , "throttling_tier":"", "user_roles":"" },
];


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

    //fixed chrome issue with file paths
    $('input[type=file]').on('change', function(e) {
        var filename = $(e.currentTarget).val().replace(/^.*\\/, "");
        $(this).parent().parent().find('.txt-filepath').val(filename);
    });

    // get the Shared entitlement partials
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
                })
            }
            updatePolicyPartial();

        },
        error: function () {
        }
    });

    $("#add_resource").click(function(){

        $(".http_verb").each(function(){
            var resource = {};
            resource.url_pattern = $("#url_pattern").val();
            resource.http_verb = $(this).val();
            resource.user_roles = $("#user_roles").val();
            if($(this).is(':checked')){
            	if(resource.url_pattern != ""){
            		RESOURCES.push(resource);
            	}

            }
        })

        resetResource();
        $("#resource_tbody").trigger("draw");
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



    $("#clear_resource").click(function(){
        resetResource();
    });

    $("#resource_tbody").delegate(".delete_resource","click", function(){


        var conf = confirm("Are you sure you want to delete the selected resource?");
        if (conf == true) {
            var i = $(this).attr("data-index");
            RESOURCES.splice(i, 1);

            // Invalidate relevant entitlement policy
            invalidateEntitlementPolicy(i);

            $("#resource_tbody").trigger("draw");
        }




    });

    $("#resource_tbody").on("draw", function(){
        $("#resource_tbody").html("");
        for(var i=0; i< RESOURCES.length; i++){

          $("#resource_tbody").prepend(
                "<tr> \
                  <td><span style='color:#999'>/{context}/{version}/</span>"+ RESOURCES[i].url_pattern +" <input type='hidden' value='"+RESOURCES[i].url_pattern+"' name='uritemplate_urlPattern"+i+"'/></td> \
                  <td><strong>"+ RESOURCES[i].http_verb +"</strong><input type='hidden' value='"+RESOURCES[i].http_verb+"' name='uritemplate_httpVerb"+i+"'/></td> \
                  <td style='padding:0px'><select name='uritemplate_tier" + i + "' class='selectpicker'  onChange='updateDropdownThrottlingTier(" + i + ");' id='getThrottlingTier" + i + "' style='width:100%;border:none;'>"+throttlingTierControlBlock+"</select></td> \
                  <td style='padding:0px'><select name='uritemplate_skipthrottle" + i + "' class='selectpicker' onChange='updateDropDownSkipThrottle(" + i + ");' id='getSkipthrottle" + i + "' style='width:100%;border:none;'><option value='False'>False</option><option value='True'>True</option></select></td> \
                    <td style='padding:0px'><select name='uritemplate_allowAnonymous" + i + "' onChange='updateDropDownAllowAnonymous(" + i + ");' class='selectpicker' id='getAllowAnonymous" + i + "' style='width:100%;border:none;'><option value='False' id='False'>False</option><option value='True' id='True'>True</option></select></td> \
                   \
                  <td> \
                    \
               <div class='dropdown'> \
                    <a href='#' data-toggle='dropdown' class='dropdown-toggle'>Add <b class='caret'></b></a>\
                    <ul  id='dropdown_entitlementPolicyPartialMappings"+i+"' class='dropdown-menu policy-partial-dropdown' onChange='updateAccessPolicyOptions(" + i + ");' data-resource-id='"+ i +"' style='margin: 0px;'>\
                    \
                    </ul>\
                </div>\
                  \
                    \
                    <input type='hidden' class='uritemplate_entitlementPolicyPartialMappings_text' id='uritemplate_entitlementPolicyPartialMappings"+i+"'  name='uritemplate_entitlementPolicyPartialMappings"+i+"'value='[]'/> \
                  </td> \
                  \
                  <td class='userRoles' style='padding:0px'><input type='text' name='uritemplate_userRoles"+i+"' onChange='updateUserRoles(" + i + ");' id='getUserRoles"+i+"' style='width:95%;border:none;'></input></td> \
                  <td> \
                  	<a data-index='"+i+"' class='delete_resource'><i class='icon-remove-sign'></i>  Delete</a>&nbsp; \
                  </td> \
                </tr> \
				"
            );

            updatePolicyPartial();

         // roles autocomplete

            $('#getUserRoles'+i).tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
              	theme: 'facebook',
              	preventDuplicates: true,
              	onAdd: function(role) {

              	},
              	onDelete: function(role) {

              	}
          	});

            //set Throttling Tier when adding a new resource
            if (RESOURCES[i].throttling_tier !== undefined && RESOURCES[i].throttling_tier !== '') {
                $('#getThrottlingTier' + i).val(RESOURCES[i].throttling_tier);
            }

            //set Skip Throttling when adding a new resource
            if (RESOURCES[i].skipthrottle !== undefined && RESOURCES[i].skipthrottle !== '') {
                $('#getSkipthrottle' + i).val(RESOURCES[i].skipthrottle);
            }

            //set User Roles when adding a new resource
            if (RESOURCES[i].user_roles !== undefined && RESOURCES[i].user_roles !== '') {
                var res = RESOURCES[i].user_roles.split(",");
                for (var j = 0; j < res.length; ++j) {
                    $('#getUserRoles' + i).tokenInput("add", {id: res[j], name: res[j]});
                }
            }

            //set Access Policy Options when adding a new resource
            if (RESOURCES[i].accessPolicyOptions !== undefined && RESOURCES[i].accessPolicyOptions !== '') {
                $('#uritemplate_entitlementPolicyPartialMappings' + i).val(RESOURCES[i].accessPolicyOptions);
                updatePolicyPartial();
            }

            //set Anonymous Allow option value
            if(RESOURCES[i].allowAnonymous !== undefined && RESOURCES[i].allowAnonymous !== '') {
                 $('#getAllowAnonymous' + i).val(RESOURCES[i].skipthrottle);
            }
        }
    });

    $(document).on("click", ".add_entitlement_policy", function () {
      var resourceIndex = $(this).data('index');
      preparePolicyEditor(resourceIndex);
    })

    $(document).on("click", ".delete_entitlement_policy", function () {
      var resourceIndex = $(this).data('index');
      deleteEntitlementPolicy(resourceIndex);
    })

    $("#resource_tbody").trigger("draw");


    $(document).on("click", ".add-policy-partial", function () {
       alert("Hi");
    })




});

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
    RESOURCES[index].throttling_tier = throttlingTierElement.options[throttlingTierElement.selectedIndex].value;
}

/*
 Fires when user change Skip Throttle
 @param index : row id
 */
function updateDropDownSkipThrottle(index) {
    var skipthrottleElement = document.getElementById("getSkipthrottle" + index);
    RESOURCES[index].skipthrottle = skipthrottleElement.options[skipthrottleElement.selectedIndex].value;
}

/*
 Fires when user change user roles
 @param index : row id
 */
function updateUserRoles(index) {
    var userRolesElement = document.getElementById("getUserRoles" + index);
    RESOURCES[index].user_roles = userRolesElement.value;
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
    RESOURCES[index].accessPolicyOptions = entitlementPolicyPartialMappingsElement.value;
}

/*
 Fires when user change Access Allow Anonymous option
 @param index : row id
 */
function updateDropDownAllowAnonymous(index) {
    var allowAnonymousElement = document.getElementById("getAllowAnonymous" + index);
    RESOURCES[index].allowAnonymous = allowAnonymousElement.options[allowAnonymousElement.selectedIndex].value;
}
