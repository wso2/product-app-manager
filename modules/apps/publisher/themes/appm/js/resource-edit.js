
$( document ).ready(function() {


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
        url: '/publisher/api/entitlement/webapp/' + uuid,
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

        $("#resource_tbody").trigger("draw");
        console.log(RESOURCES_1);
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
                  <td style='padding:0px'><select name='uritemplate_tier"+i+"' class='selectpicker' id='getThrottlingTier' style='width:100%;border:none;'><option title='Allows unlimited requests' value='Unlimited' id='Unlimited'>Unlimited</option><option title='Allows 5 request(s) per minute.' value='Silver' id='Silver'>Silver</option><option title='Allows 20 request(s) per minute.' value='Gold' id='Gold'>Gold</option><option title='Allows 1 request(s) per minute.' value='Bronze' id='Bronze'>Bronze</option></select></td> \
                  <td style='padding:0px'><select name='uritemplate_skipthrottle"+i+"' class='selectpicker' id='' style='width:100%;border:none;'><option value='False' id='False'>False</option><option value='True' id='True'>True</option></select></td> \
                   \
                  <td> \
                     \
                <div class='dropdown'> \
                    <a href='#' data-toggle='dropdown' class='dropdown-toggle'>Add<b class='caret'></b></a>\
                    <div><ul  id='dropdown_entitlementPolicyPartialMappings"+i+"' class='dropdown-menu policy-partial-dropdown' data-resource-id='"+ i +"' style='margin: 0px;'>\
                    \
                    </ul></div>\
                </div>\
               \
                 <input type='hidden' class='uritemplate_entitlementPolicyPartialMappings_text' id='uritemplate_entitlementPolicyPartialMappings"+i+"' name='uritemplate_entitlementPolicyPartialMappings"+i+"' value='"+ getValidatedEntitlementPolicyId(i) + "'/> \
\
                  </td> \
                   \
                   <td class='userRoles' style='padding:0px'><input  type='text' name='uritemplate_userRoles"+i+"' id='getUserRoles"+i+"' style='width:95%;border:none;'></input></td> \
                  \
                  <td> \
                  	<a data-index='"+i+"' class='delete_resource'><i class='icon-trash icon-white'></i></a>&nbsp; \
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
            if(RESOURCES_1[i].user_roles.indexOf(',')>-1){
                var res = RESOURCES_1[i].user_roles.split(",");
                for(var j=0; j< res.length; ++j){
                    $('#getUserRoles'+i).tokenInput("add", {id:res[j] , name:res[j]});
                    console.log(res[j]);
                }
            }
            else{
                $('#getUserRoles'+i).tokenInput("add", {id:RESOURCES_1[i].user_roles , name: RESOURCES_1[i].user_roles});
            }

            document.getElementById(RESOURCES_1[i].throttling_tier).selected="true";
            document.getElementById(RESOURCES_1[i].skipthrottle).selected="true";
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
