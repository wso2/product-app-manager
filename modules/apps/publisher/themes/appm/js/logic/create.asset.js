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

	
	$('#btn-create-asset').on('click', function(e) {
		e.preventDefault();
		var context =  $('#overview_context').val();

		if (context.charAt(0) !=  "/"){
			context = "/" + context;
			$('#overview_context').val(context);
		}
		
		var version =  $('#overview_version').val();
		var tracking_code = context + version;
		 
		 var hash = 5381;
		 for (i = 0; i < tracking_code.length; i++) {
		        char = tracking_code.charCodeAt(i);
		        hash = ((hash << 5) + hash) + char; /* hash * 33 + c */
		 }
		var tracking_code_id = "AM_"+hash;
		$('#tracking_code').val(tracking_code_id);
		
		 if($('#autoConfig').is(':checked')){
				var selectedProvider = $('#providers').val();
				$('#sso_ssoProvider').val(selectedProvider);
		 }
		

 	var options = { 
      
			success:       function(response) {

				var result = JSON.parse(response);

				//Check if the asset was added
				if (result.ok) {
					showAlert('Asset added successfully.', 'success');
				    	(function setupPermissions() {
                        			var rolePermissions = [];
                        			$('.role-permission').each(function(i, tr) {
                            				var role = $(tr).attr('data-role');

                            				var permissions = [];

                            				$(tr).children('td').children(':checked').each(function(j, checkbox) {
                                				permissions.push($(checkbox).attr('data-perm'));
                            				});

                            				rolePermissions.push({
                                				role: role,
                                				permissions: permissions
                            				});
                        			});


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
					var msg = processErrorReport(result.report);
					showAlert(msg, 'error');
				}

			},  // post-submit callback 
 		
			error : function(response) {
				showAlert('Failed to add asset.', 'error');
			},
		 
        		// other available options: 
        		url:       '/publisher/asset/' + type,         // override for form's 'action' attribute 
        		type : 'POST'      // 'get' or 'post', override for form's 'method' attribute 
        		//dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
        		//clearForm: true        // clear all form fields after successful submit 
        		//resetForm: true        // reset the form after successful submit 
 
        		// $.ajax options can be used here too, for example: 
        		//timeout:   3000 
	}; 
    
    

	$('#form-asset-create').ajaxSubmit(options); 
		
	});

	// roles autocomplete
    	$('#roles').tokenInput('/publisher/api/lifecycle/information/meta/' + $('#meta-asset-type').val() + '/roles', {
        	theme: 'facebook',
        	preventDuplicates: true,
        	onAdd: function(role) {
            		var permission = $('<tr class="role-permission" data-role="' + role.id + '"><td>' + role.name + '</td><td><input data-perm="GET" type="checkbox" value=""></td><td><input data-perm="PUT" type="checkbox" value=""></td><td><input data-perm="DELETE" type="checkbox" value=""></td><td><input data-perm="AUTHORIZE" type="checkbox" value=""></td></tr>')
            		$('#permissionsTable > tbody').append(permission);
        	},
        	onDelete: function(role) {
            		console.log()
            		$('#permissionsTable tr[data-role="' + role.id + '"]').remove();
        	}
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
	    var propertyCount = document.getElementById("claimPropertyCounter");

	    var i = propertyCount.value;
	    var currentCount = parseInt(i);

	    currentCount = currentCount + 1;
	    propertyCount.value = currentCount;

	    document.getElementById('claimTableId').style.display = '';
	    var claimTableTBody = document.getElementById('claimTableTbody');

	    var claimRow = document.createElement('tr');
	    claimRow.setAttribute('id', 'claimRow' + i);

	    var claim = document.getElementById('claims').value;
	    var claimPropertyTD = document.createElement('td');
	    claimPropertyTD.setAttribute('style', 'padding-left: 40px ! important; color: rgb(119, 119, 119); font-style: italic;');
	    claimPropertyTD.innerHTML = "" + claim + "<input type='hidden' name='claimPropertyName" + i + "' id='claimPropertyName" + i + "'  value='" + claim + "'/> ";

	    var claimRemoveTD = document.createElement('td');
	    
	    claimRemoveTD.innerHTML = '<a href="#"  onclick="removeClaim(' + i + ');return false;"><i class="icon-remove-sign"></i>  Delete</a>';
	    claimRow.appendChild(claimPropertyTD);
	    claimRow.appendChild(claimRemoveTD);

	    claimTableTBody.appendChild(claimRow);
	});
	
	

	function createServiceProvider(){
        	var sso_config = {};
        	var provider_name  = $('#providers').val();
        	var logout_url = $('#overview_logoutUrl').val();
        	var idp_provider = $('#sso_idpProviderUrl').val();
		var app_name = $('#overview_name').val();
		var app_version = $('#overview_version').val();
		var app_transport = $('#overview_transports').val();
		var app_context = $('#overview_context').val();

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
	var  i=0;
    	var propRow = document.getElementById("claimRow" + i);

    	while (propRow != undefined && propRow != null) {
        	var parentTBody = propRow.parentNode;
        	if (parentTBody != undefined && parentTBody != null) {
            		parentTBody.removeChild(propRow);
            		if (!isContainRaw(parentTBody)) {
                		var propertyTable = document.getElementById("claimTableId");
                		propertyTable.style.display = "none";

            		}
        	}
        
        i++;
        propRow = document.getElementById("claimRow" + i);
    	}
}

