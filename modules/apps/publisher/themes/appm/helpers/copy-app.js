var resources = function(page, meta) {
    return {
        js: ['copy-app.js', '/logic/asset.tag.edit.js', 'bootstrap-select.min.js', 'options.text.js', 'codemirror/codemirror.js', 'codemirror/show-hint.js', 'codemirror/xml-hint.js', 'codemirror/xml.js', 'entitlement.js', 'resource-edit.js']
    };
};

/**
 * Processes data before rendering.
 */
var processData = function(data) {
    var processedData = selectCategory(data);
    processedData = extractVisibleRoles(processedData);
    return processedData;
}

var selectCategory = function(data) {

    var selected,
        arr = [],
        currentCategory = data.artifact.attributes['overview_category'],
        categories = selectCategories(data.data.fields);

    for (var i in categories) {

        selected = (currentCategory == categories[i]) ? true : false;
        arr.push({
            cat: categories[i],
            sel: selected
        });
    }
    data.categorySelect = arr;
    return data;
}

/**
 * Extracts the roles who can use/see this web app in the store.
 */
var extractVisibleRoles = function(data) {

    // Function :: Filters out system roles from in the list.
    var isExcludedRole = function(role) {

        // Function :: Checks whether the given role has the prefix 'Internal/private_'.
        var isInternalRole = function(role) {
            var internalRolePrefix = "Internal/private_";
            return role.indexOf(internalRolePrefix) == 0;
        }

        // Exclude if the role has the prefix 'Internal/private_'.
        if (isInternalRole(role)) {
            if (log.isDebugEnabled()) {
                log.debug("Excluding role '" + role + "' from visible roles list. Reason : Role is an internal role");
            }
            return true;
        }

        // Or else check against the exclude list.
        var config = require("/config/publisher.json");
        var excludedRolesList = config.excludedRolesList;

        for (var i = 0; i < excludedRolesList.length; i++) {
            if (role == excludedRolesList[i]) {
                if (log.isDebugEnabled()) {
                    log.debug("Excluding role '" + role + "' from visible roles list. Reason : Role is in the exclude list ==> " + JSON.stringify(excludedRolesList))
                }
                    return true;
            }
        }

        return false;

    }

    // Function :: Checks whether the given permissions array has GET permission. We only deal with GET permission.
    var hasGetPermission = function(permissions) {

        for (var i = 0; i < permissions.length; i++) {
            if (permissions[i] == "GET") {
                return true;
            }
        }
        return false;

    }

    var permissions = data.data.permissions;
    var visibleRoles = [];

    if (permissions) {

        for (var i = 0; i < permissions.length; i++) {

            var role = permissions[i].role;

            if (isExcludedRole(role)) {
                continue;
            }

            if (hasGetPermission(permissions[i].permissions)) {
                visibleRoles.push(role);
            }

        }
    }

    data.data["visibleRoles"] = visibleRoles;

    return data;
}

var selectCategories = function(fields) {
    for (var i in fields) {
        if (fields[i].name == "overview_category") {
            return fields[i].valueList;
        }
    }
}
