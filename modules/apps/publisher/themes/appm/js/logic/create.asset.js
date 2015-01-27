$(function() {

	/*
	Creates a new asset
	 */

	//var id=$('#meta-asset-id').html();
	var type = $('#meta-asset-type').val();

	var TAG_API_URL = '/publisher/api/tag/';
	var tagType = $('#meta-asset-type').val() + 's';

	var tagUrl = TAG_API_URL + tagType;
	var THEME = 'facebook';
	var TAG_CONTAINER = '#tag-test';
	var CHARS_REM = 'chars-rem';
	var DESC_MAX_CHARS = 995;


	$('#overview_description').after('<span class="span8 ' + CHARS_REM + '"></span>');

	//Obtain all of the tags for the given asset type
	$.ajax({
		url : tagUrl,
		type : 'GET',
		success : function(response) {
			var tags = JSON.parse(response);
			$(TAG_CONTAINER).tokenInput(tags, {
				theme : THEME,
				allowFreeTagging : true
			});

		},
		error : function() {
			console.log('unable to fetch tag cloud for ' + type);
		}
	});



    $('#overview_context').on('blur', function() {
        var $this = $(this), flag = $('.icon-check-appname'), btnCreate = $('#btn-create-asset');
        var context = $this.val();

        if (!flag.length) {
            $this.after('<i class="icon-check-appname"></i>');
            flag = $('.icon-check-appname');
        }
        if(context !== '') {

            if(context.charAt(0) == '/'){
                context = context.substr(1);
            }


            //check if the asset name available as user types in
            $.ajax({
                url : '/publisher/api/validations/assets/' + type + '/overview_context/' + context,
                type : 'GET',
                success : function(response) {

                    //Check if the context already exists
                    if (response == 'true') {

                        flag.removeClass().addClass('icon-ban-circle icon-check-appname').show();
                        btnCreate.attr('disabled', 'disabled');
                        showAlert("Duplicate context value.", 'error');
                    } else {

                        flag.removeClass().addClass('icon-ok icon-check-appname').show();
                        btnCreate.removeAttr('disabled');
                        $(".alert-error");
                    }

                },
                error : function(response) {
                    flag.removeClass().addClass('icon-ok icon-check-appname').hide();
                    showAlert('Unable to auto check Asset name availability', 'error');
                }
            });
        }else{
            showAlert('Context Cannot be null.');
        }

    });




	$('#btn-create-asset').on('click', function(e) {
		e.preventDefault();
		var context =  $('#overview_context').val();

		if (context.charAt(0) !=  "/"){
			context = "/" + context;
			$('#overview_context').val(context);
		}

        var name =  $('#overview_name').val()
		var version =  $('#overview_version').val();

        // random number between 0 to 1 e.g-0.5838903994299471
        var randomNum = Math.random();
        var code =randomNum.toString();
        code = code.replace("0.","");
        var tracking_code_id = "AM_"+code;

		$('#tracking_code').val(tracking_code_id);
		
		 if($('#autoConfig').is(':checked')){
			var selectedProvider = $('#providers').val();
			$('#sso_ssoProvider').val(selectedProvider);
		 }
		
		 // Add entitlement policies.
		 $('#entitlementPolicies').val(JSON.stringify(entitlementPolicies));

		// AJAX request options.
 		var options = { 
      
			success: function(response) {

				var result = JSON.parse(response);

				//Check if the asset was added
				if (result.ok) {
					
					showAlert('Asset added successfully.', 'success');
				    
				    	(function setupPermissions() {

                    				var rolePermissions = [];
                        			
			    			// 'GET' permission to be applied to the selected roles.
			    			var readPermission = new Array();
			    			readPermission.push("GET");

			    			// Get roles from the UI
			    			var rolesInUI = $('#roles').tokenInput("get");	

			    			for(var i = 0; i < rolesInUI.length; i++){
			    				rolePermissions.push({
				    				role: rolesInUI[i].id,
				    				permissions: readPermission
							});
			    			}

			    			if (rolePermissions.length > 0) {
							$.ajax({
				    				url: '/publisher/asset/' + type + '/id/' + result.id + '/permissions',
				    				type: 'POST',
				    				processData: false,
				    				contentType: 'application/json',
				    				data: JSON.stringify(rolePermissions),
				    				success: function(response) {
									window.location = '/publisher/assets/' + type + '/';
				    				},
				    				error: function(response) {
									showAlert('Error adding permissions.', 'error');
				    				}
								});
			    			} else {
								window.location = '/publisher/assets/' + type + '/';
			    			}
                    			})();
                    
					/**adding tags**/

					var data = {};
					var tags = [];
					var selectedTags;
				        selectedTags = $('#tag-test').tokenInput('get');

					for (var index in selectedTags) {
						tags.push(selectedTags[index].name);
					}

                    			data['tags'] = tags
                    			if (selectedTags.length > 0) {
                        			$.ajax({
                            				url: TAG_API_URL +  $('#meta-asset-type').val() + '/' + result.id,
                            				type: 'PUT',
                            				data: JSON.stringify(data),
                            				contentType: 'application/json; charset=utf-8',
                            				dataType: 'json',
                            				success: function (response) {},
                            					error: function () {
                            					showAlert('Unable to add the selected tag.', 'error');
                            				}
                        			});
                    			}		
					
					if($('#autoConfig').is(':checked')){
						createServiceProvider();
					}

				} else {
                    if(result.isexists){
                        showAlert(result.msg, 'error');

                    }else {
                        var msg = processErrorReport(result.report);
                        showAlert(msg, 'error');
                    }
				}

			},  // post-submit callback 
 		
			error : function(response) {
				showAlert('Failed to add asset.', 'error');
			},
		 
        		url: '/publisher/asset/' + type, 
        		type : 'POST'
        	
		}; 
    
    	$('#form-asset-create').ajaxSubmit(options); 
		
	});
	

	// Visibility roles
	$('#roles').tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
    	theme: 'facebook',
    	preventDuplicates: true,
    	hintText: "Type in a user role"
	});

	$('#overview_description').keyup(function() {
		var self = $(this), length = self.val().length, left = DESC_MAX_CHARS - length, temp;

		if (length > DESC_MAX_CHARS) {
			temp = self.val();
			$(this).val(temp.substring(0, DESC_MAX_CHARS));
			console.log("Max chars reached");
			return;
		}
		$('.' + CHARS_REM).text('Characters left: ' + left);
	});
	
	$('#autoConfig').click(function () {
		if($('#autoConfig').is(':checked')){
			$('#provider-table').show();
			$('#claims-table').show();
			//var roleClaim = "http://wso2.org/claims/role";
			//addToClaimsTable(roleClaim,false);
		}else{
			var rows = $('table.sso tr');
			var provider =  rows.filter('.provider-table');
			provider.hide();
									
			var claims = rows.filter('.claims-table');
			claims.hide();
			removeClaimTable();

		}
	});
	
	$("#providers").change(function () {
		var value = $('#providers').val();
        	loadClaims(value)
    	});
	
	$.ajax({
          url: '/publisher/api/sso/providers',
          type: 'GET',
          contentType: 'application/json',
          success: function(response) {
        	 
        	  var providers_data = JSON.parse(response);
              	  if((providers_data.success === true) && (!$.isEmptyObject(providers_data.response))) {
			loadProviders(providers_data.response);
              	  } else {
			$("#ssoTable").remove();
              	  }
  			
          },
          error: function(response) {
              showAlert('Error adding providers.', 'error');
          }
    	});
	
	
	function loadProviders(providers_data){
		 for(var i=0;i<providers_data.length;i++){
			  var x = providers_data[i];
			  console.log(providers_data.length + "i:"+i);
			  console.log(x);
			  $("#providers").append($("<option></option>").val(x).text(x));
		  }
		 
		 var value = $('#providers').val();
		 loadClaims(value);

        var roleClaim = "http://wso2.org/claims/role";
        addToClaimsTable(roleClaim,false);
	}
	
	function loadClaims (provider){
		 var sso_values = provider.split("-");
		 $.ajax({
             		url: '/publisher/api/sso/claims?idp='+sso_values[0] +"&version="+sso_values[1],
             		type: 'GET',
             		contentType: 'application/json',
             		success: function(response) {
           	  		var claims = JSON.parse(response).response;
           	 		for(var i=0;i<claims.length;i++){
           		 		var y = claims[i];
           		 		$("#claims").append($("<option></option>").val(y).text(y));
           	 		}
     			
             		},
             		error: function(response) {
                 		showAlert('Error adding claims.', 'error');
             		}
         	});
	}

	
	$('#addClaims').click(function () {
		var claim  = $("#claims").val();
		addToClaimsTable(claim,true);
	});
	
	function addToClaimsTable(claim,clickable){
		var propertyCount = $('#claimPropertyCounter');

	    	var i = propertyCount.val(); 
	    	var currentCount = parseInt(i);

	    	currentCount = currentCount + 1;
	    	propertyCount.val(currentCount);

	    	$('#claimTableId').hide();
	    	if(clickable){
	    		$('#claimTableTbody').append($('<tr id="claimRow' + i +'" class="claimRow">'+
		    		'<td style="padding-left: 40px ! important; color: rgb(119, 119, 119); font-style: italic;">'+
		    		claim + '<input type="hidden" name="claimPropertyName' + i + '" id="claimPropertyName' + i + '"  value="' + claim + '"/> '+
		    		'</td>'+
		    		'<td>'+
		    		'<a href="#"  onclick="removeClaim(' + i + ');return false;"><i class="icon-remove-sign"></i>  Delete</a>' +
		    		'</td>'+
		    		'</tr>'));
		}else{
			$('#claimTableTbody').append($('<tr id="claimRow' + i +'" class="claimRow">'+
		    		'<td style="padding-left: 40px ! important; color: rgb(119, 119, 119); font-style: italic;">'+
		    		claim + '<input type="hidden" name="claimPropertyName' + i + '" id="claimPropertyName' + i + '"  value="' + claim + '"/> '+
		    		'</td>'+
		    		'<td>'+
		    		'<a href="#" style="pointer-events: none; cursor: default;color:#C4C4C4"  onclick="removeClaim(' + i + ');return false;"><i class="icon-remove-sign"></i>  Delete</a>' +
		    		'</td>'+
		    		'</tr>'));
		}
	    
	    	$('#claimTableTbody').parent().show();
	}
	
	

	function createServiceProvider(){
        	var sso_config = {};
        	var provider_name  = $('#providers').val();
        	var logout_url = $('#overview_logoutUrl').val();
        	var idp_provider = $('#sso_idpProviderUrl').val();
		var app_name = $('#overview_name').val();
		var app_version = $('#overview_version').val();
		var app_transport = $('#overview_transports').val();
		var app_context = $('#overview_context').val();
		var app_provider = $('#overview_provider').val();
		var app_allowAnonymous=$('#overview_allowAnonymous').val();

		var claims = [];
		var index=0;
		var propertyCount = document.getElementById("claimPropertyCounter").value;
		while(index < propertyCount){
				var claim = $("#claimPropertyName"+index).val();
				if(claim != null){
					claims[claims.length] = claim;
				}
				index++;
		}

        	sso_config.provider = provider_name;
        	sso_config.logout_url = logout_url;
        	sso_config.claims = claims;
		sso_config.idp_provider = idp_provider;
		sso_config.app_name = app_name;
		sso_config.app_verison = app_version;
		sso_config.app_transport = app_transport;
		sso_config.app_context = app_context;
		sso_config.app_provider = app_provider;
		sso_config.app_allowAnonymous=app_allowAnonymous;

        	$.ajax({
            		url: '/publisher/api/sso/addConfig',
            		type: 'POST',
            		contentType: 'application/json',
            		data:JSON.stringify(sso_config),
            		success: function(response) {
                		console.log("Added SSO config successfully");
            		},
            		error: function(response) {
                		showAlert('Error adding service provider.', 'error');
            		}
        	});
	}

	
	
	/*
	 The function is used to build a report message indicating the errors in the form
	 @report: The report to be processed
	 @return: An html string containing the validation issues
	 */
	function processErrorReport(report) {
		var msg = '';
		for (var index in report) {

			for (var item in report[index]) {
				msg += report[index][item] + "<br>";
			}
		}

		return msg;
	}

	/*
	 The function is used to add a given field to a FormData element
	 @field: The field to be added to the formData
	 @formData: The FormDara object used to store the field
	 @return: A FormData object with the added field
	 */
	function fillForm(field, formData) {

		var fieldType = field.type;

		if (fieldType == 'file') {
			console.log('added ' + field.id + ' file.');
			formData[field.id] = field.files[0];
		} else {
			formData[field.id] = field.value;
		}

		return formData;
	}

	/*
	 The function is used to obtain tags selected by the user
	 @returns: An array containing the tags selected by the user
	 */
	function obtainTags() {

		var tagArray = [];

		try {
			var tags = $(TAG_CONTAINER).tokenInput('get');

			for (var index in tags) {
				tagArray.push(tags[index].name);
			}

			return tagArray;
		} catch(e) {
			return tagArray;
		}

	}


	$('.selectpicker').selectpicker();
});


function removeClaim(i) {
	var propRow = document.getElementById("claimRow" + i);
    	if (propRow != undefined && propRow != null) {
        	var parentTBody = propRow.parentNode;
        	if (parentTBody != undefined && parentTBody != null) {
            		parentTBody.removeChild(propRow);
            		if (!isContainRaw(parentTBody)) {
                		var propertyTable = document.getElementById("claimTableId");
                		propertyTable.style.display = "none";

            		}
        	}
    	}
}

function isContainRaw(tbody) {
    	if (tbody.childNodes == null || tbody.childNodes.length == 0) {
        	return false;
    	} else {
        	for (var i = 0; i < tbody.childNodes.length; i++) {
            		var child = tbody.childNodes[i];
            		if (child != undefined && child != null) {
                		if (child.nodeName == "tr" || child.nodeName == "TR") {
                    			return true;
                		}
            		}
        	}
    	}
    return false;
}


function removeClaimTable() {
	$('.claimRow').remove();
	$('#claimTableId').hide();
	$('#claimPropertyCounter').val(0);
}

