/*
 Description: The script is used to edit an asset
 Filename: edit.asset.js
 Created Date: 17/8/2013
 */
$(function() {

	//The container used to display messages to the user
	var MSG_CONTAINER = '#msg-container-recent-activity';
	var ERROR_CSS = 'alert alert-error';
	var SUCCESS_CSS = 'alert alert-info';
	var CHARS_REM = 'chars-rem';
	var DESC_MAX_CHARS = 995;

	$('#overview_description').after('<span class="span8 ' + CHARS_REM + '"></span>');
		
	  // let's fill all the permissions
    $.each($('.perm-check'), function () {
        // var checkbox = $(checkbox);

        if($(this).attr('data-permissions').indexOf($(this).attr('data-perm')) > -1) {
            $(this).attr('checked', true);
        } else {
            $(this).attr('checked', false);
        }
    });
    
    var sso_provider = $('#sso_ssoProvider').val();
    if(sso_provider != " "){
    	$('#autoConfig').prop('checked', true);
    	$('#provider-table').show();
	$('#claims-table').show();
	$.ajax({
		url: '/publisher/api/sso/providers',
	        type: 'GET',
	        contentType: 'application/json',
	        success: function(response) {
			var providers_data = JSON.parse(response);
                  	if((providers_data.success === true) && (!$.isEmptyObject(providers_data.response))) {
 				loadSelectedProviders(providers_data.response);
                  	} else {
				$("#ssoTable").remove();
                  	}
		},
	        error: function(response) {
	        	showAlert('Error adding providers.', 'error');
	        }
	});
		
    } else{
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
    	
    }

    function loadProviders(providers_data){
	for(var i=0;i<providers_data.length;i++){
		var x = providers_data[i];
		$("#providers").append($("<option></option>").val(x).text(x));
		if(x == sso_provider){
			$("#providers").val(sso_provider);
		}
	}
	var value = $('#providers').val();
	loadClaims(value);
    }
    
    
    
    function loadSelectedProviders(providers_data){
	for(var i=0;i<providers_data.length;i++){
		var x = providers_data[i];
		$("#providers").append($("<option></option>").val(x).text(x));
		if(x == sso_provider){
			$("#providers").val(sso_provider);
		}
	}
	loadClaims(sso_provider);
	loadSelectedClaims(sso_provider);
     }
    
    function loadSelectedClaims(selectedProvider){
    	var y = selectedProvider.split("-");
		var appProvider = $("#overview_provider").val();
    	var appName = $("#overview_name").val();
    	var appVersion = $("#overview_version").val();

    	$.ajax({
	          url: '/publisher/api/sso/'+ y[0] + '/' + y[1] + '/' + appProvider + '/' + appName + '/' + appVersion ,
	          type: 'GET',
	          contentType: 'application/json',
	          success: function(response) {
	        	 
	        	  var provider_data = JSON.parse(response).response;
	        	  var selected_claims = provider_data.claims;
	        	  for(n=0;n<selected_claims.length;n++){
	        		  var claim = selected_claims[n];
	        		  if(claim == "http://wso2.org/claims/role"){
	        			  addToClaimsTable(claim,false);  
	        		  }else{
	        			  addToClaimsTable(claim,true);  
	        		  }
	        		  
	        		  
	        	  }
	  			
	          },
	          error: function(response) {
	              showAlert('Error adding providers.', 'error');
	          }
	    });
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
          		 console.log("y:"+y);
          		 $("#claims").append($("<option></option>").val(y).text(y));
          	 }
    			
            },
            error: function(response) {
                showAlert('Error adding claims.', 'error');
            }
        });
	}
    
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
    
    function populateVisibleRoles(){
		
		var visibilityComponent = $('#roles');
		var visibleRoles = visibilityComponent.data('roles');

	 	if(visibleRoles){
	 		visibleRoles = visibleRoles.split(",");

	 		for(var i = 0; i < visibleRoles.length; i++){
	 			var role = visibleRoles[i];
	 			visibilityComponent.tokenInput("add", {id: role, name: role});
	 		}
	 	}

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
	
    

	$('#editAssetButton').on('click', function() {

		var data = {};
		//var formData=new FormData();

		//Obtain the current url
		var url = window.location.pathname;

		//The type of asset
		var type = $('#meta-asset-type').val();

		//The id
		//Break the url into components
		var comps = url.split('/');

		//Given a url of the form /pub/api/asset/{asset-type}/{asset-id}
		//length=5
		//then: length-2 = {asset-type} length-1 = {asset-id}
		var id = comps[comps.length - 1];

		//Extract the fields
		var fields = $('#form-asset-edit :input');
		
		// Add entitlement policies.
		$('#entitlementPolicies').val(JSON.stringify(entitlementPolicies));

		if($('#autoConfig').is(':checked')){
			var selectedProvider = $('#providers').val();
			$('#sso_ssoProvider').val(selectedProvider);
	    	}else{
	    		var selectedProvider = " ";
			$('#sso_ssoProvider').val(selectedProvider);
	    	}
		

		//Create the data object which will be sent to the server
		/*
		fields.each(function () {

		if ((this.type != 'button')&&(this.type!='reset')&&(this.type!='hidden')) {
		data[this.id] = this.value;
		formData=fillForm(this,formData);
		}
		});*/

		// console.log(JSON.stringify(formData));

		var url = '/publisher/api/asset/' + type + '/' + id;

		var options = {

			beforeSubmit : function(arr, $form, options) {
			},
			success : function(response) {
				var result = JSON.parse(response);
				
				if (result.ok) {
					var asset = result.asset;
					createMessage(MSG_CONTAINER, SUCCESS_CSS, 'Asset updated successfully');

					updateFileFields(asset);
				        
			        (function setupPermissions() {
			    		var rolePermissions = [];

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
                                		url: '/publisher/asset/' + type + '/id/' + id + '/permissions',
                                		type: 'POST',
                                		processData: false,
                                		contentType: 'application/json',
                                		data: JSON.stringify(rolePermissions),
                                		success: function (response) {
                                   			 showModel(type, id);
                               			 },
                               			error: function (response) {
                                   			 showAlert('Error adding permissions.', 'error');
                                		}
                            		});
                        	}
	            			
        			})();

			    	if($('#autoConfig').is(':checked')){
						createServiceProvider();
					}

                    window.location = "/publisher/assets/webapp/";
				} else {
					var report = processErrorReport(result.report);
					createMessage(MSG_CONTAINER, ERROR_CSS, report);
				}

			},
			error : function(response) {
				createMessage(MSG_CONTAINER, ERROR_CSS, 'Asset was not updated successfully.');
			},

			// other available options:
			url : url, // override for form's 'action' attribute
			type : 'POST' // 'get' or 'post', override for form's 'method' attribute
			//dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
			//clearForm: true        // clear all form fields after successful submit
			//resetForm: true        // reset the form after successful submit

			// $.ajax options can be used here too, for example:
			//timeout:   3000
		};

		$('#form-asset-edit').ajaxSubmit(options);

	});

	$('#overview_description').keyup(function() {
		var self = $(this), length = self.val().length, left = DESC_MAX_CHARS - length, temp;

		if (length > DESC_MAX_CHARS) {
			temp = self.val();
			$(this).val(temp.substring(0, DESC_MAX_CHARS));
			//console.log("Max chars reached");
			return;
		}
		$('.' + CHARS_REM).text('Characters left: ' + left);
	});
	
	// Visibility roles.
	$('#roles').tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
		theme: 'facebook',
        preventDuplicates: true,
        hintText: "Type in a user role"
 	});

	 $('#autoConfig').click(function () {
		if($('#autoConfig').is(':checked')){
			$('#provider-table').show();
			$('#claims-table').show();
			var roleClaim = "http://wso2.org/claims/role";
			addToClaimsTable(roleClaim,false);
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
	        loadClaims(value);
	 });

	// Populate 'Visibility' component with existing roles.	
 	populateVisibleRoles();
	 
	/*
	 The function updates the file upload fields after recieving a response from
	 the server
	 @asset: An updated asset instance
	 */
	function updateFileFields(asset) {
		var fields = $('#form-asset-edit :input');
		var fieldId;
		var previewId;
		var fieldValue;
		var inputField;

		fields.each(function() {

			//We only to update the file fields
			if (this.type == 'file') {
				fieldId = this.id;
				previewId = getFileLabelId(fieldId);
				fieldValue = asset.attributes[fieldId];

				inputField = $('#' + fieldId);

				var e = inputField;
				e.wrap('<form>').parent('form').trigger('reset');
				e.unwrap();

				//$('#img-preview-'+ fieldId).attr('src', fieldValue);
				//Update the label
				$('#' + previewId).html(fieldValue);
			}
		});
	}

	function getFileLabelId(fieldId) {
		return 'preview-' + fieldId;
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
			//Only add the file if the user has selected a new file
			if (field.files[0]) {
				formData.push({name:field.name,value:field.files[0],type:fieldType});
			} else {
				//Locate the existing url from the preview label
				var existingUrl = $('#preview-' + field.id).html();
				formData({name:field.name, value:exisitingUrl, type:'text'});
			}
		} else {
			formData.push({name:field.name,value:field.value});
		}

		return formData;
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
		sso_config.app_allowAnonymous=app_allowAnonymous;

        $.ajax({
            url: '/publisher/api/sso/editConfig',
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
				msg += report[index][item];
			}
		}

		return msg;
	}

	/*
	 The function creates a message and displays it in the provided container element.
	 @containerElement: The html element within which the message will be displayed
	 @cssClass: The type of message to be displayed
	 @msg: The message to be displayed
	 */
	function createMessage(containerElement, cssClass, msg) {
		var date = new Date();
		var time = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
		var infoMessage = '<div class="' + cssClass + '">' + '<a data-dismiss="alert" class="close">x</a>' + time + ' ' + msg + '</div';

		//Place the message
		$(containerElement).html(infoMessage);
	}

	var showModel=function(type,id){
	     	
		console.info('Successfully updated Web app: ');
		
		//alert('Succsessfully subscribed to the '+subscription.apiName+' Web App.');
		$('#messageModal1').html($('#confirmation-data1').html());
		$('#messageModal1 h3.modal-title').html(('Updating Successful'));
		$('#messageModal1 div.modal-body').html('\n\n'+ ('Congratulations! You have successfully updated the Web App ')+ '</b>');
		$('#messageModal1 a.btn-other').html('OK');
		$('#messageModal1').modal();
		$("#messageModal1").on('hidden.bs.modal', function(){
			window.location = '/publisher/asset/' + type + '/'+id;
		});

	};

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
