var resources = function(page, meta) {
	var log = new Log('edit-asset');
	log.debug('resource called');
	return {
		js : ['edit.asset.js', '/logic/asset.tag.edit.js', 'bootstrap-select.min.js','options.text.js','resource-edit.js', 'entitlement.js'],
		css : ['bootstrap-select.min.css']
	};

};

/**
* Processes data before rendering.
*/
var processData = function(data){
	var processedData = selectCategory(data);
	processedData = extractVisibleRoles(processedData);
	return processedData;
}

var selectCategory = function(data) {

	var selected, 
		arr=[],
		currentCategory = data.artifact.attributes['overview_category'],
		categories = selectCategories(data.data.fields);

	for (var i in categories) {
		
		selected = (currentCategory == categories[i])?true:false;
		arr.push({
			cat:categories[i],
			sel:selected
			});
	}
	data.categorySelect = arr;
	return data;
}

/**
* Extracts the roles who can use/see this web app in the store.
*/
var extractVisibleRoles = function(data){

	// Filters out system roles from in the list.
	var isExcludedRole = function(role){

		var excludedRoles = ["admin", "Internal/private_admin"];
	
		for(var i = 0; i < excludedRoles.length; i++){
			if(role == excludedRoles[i]){
				log.debug("Excluding role : " + role + " from visible roles list.")
				return true;
			}
		}

		return false;

	}

	// Checks whether the given permissions array has GET permission. We only deal with GET permission.
	var hasGetPermission = function(permissions){

		for(var i = 0; i < permissions.length; i++){
			if(permissions[i] == "GET"){
				return true;
			}
		}
		return false;

	}

	var permissions = data.data.permissions;
	var visibleRoles = [];

	if(permissions){
		
		for(var i = 0; i < permissions.length; i++){
		
			var role = permissions[i].role;

			if(isExcludedRole(role)){
				continue;
			}

			if(hasGetPermission(permissions[i].permissions)){
				visibleRoles.push(role);
			}

		}	
	}
	
	data.data["visibleRoles"] = visibleRoles;

	return data;
}

var selectCategories = function(fields) {
	for (var i in fields) {	
		if(fields[i].name == "overview_category"){
			return fields[i].valueList;
		}
	}
}




