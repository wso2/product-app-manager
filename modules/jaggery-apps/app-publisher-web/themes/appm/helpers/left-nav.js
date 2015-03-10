var getTypeObj, breadcrumbItems;
var deploymentManagement=require('/modules/deployment/deployment.manager.js').deploymentManagementModule();
var deploymentManager=deploymentManagement.cached();

var server = require('store').server;
var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');

var log = new Log();

breadcrumbItems = deploymentManager.getAssetData()

var generateLeftNavJson = function(data, listPartial) {

    var user=server.current(session);
    var um=server.userManager(user.tenantId);
    var createActionAuthorized = permissions.isAuthorized(user.username, config.permissions.webapp_create, um);
	
	var currentTypeObj = getTypeObj(data.shortName);
	
    var leftNavItems = { leftNavLinks :
        [
           /*
            {
                           name : "Browse All",
                           additionalClasses : "active",
                           url : "/publisher/assets/" + data.shortName + "/"
                       },*/
           
         /*   {
                name : "Add " + data.shortName + "",
                iconClass : "icon-plus-sign-alt",
                additionalClasses : (listPartial == "add-asset" ) ? "active" : null,
                url : "/publisher/asset/" + data.shortName + ""
            },
            {
                name: "Statistics",
                iconClass: "icon-dashboard",
                additionalClasses : (listPartial == "statistics" ) ? "active" : null,
                url: "/publisher/assets/statistics/" + data.shortName + "/"
            }*/
        ]
    };
    if(data.artifact){

        if(createActionAuthorized) {
            leftNavItems = { leftNavLinks: [
                {
                    name: "Overview",
                    iconClass: "icon-list-alt",
                    additionalClasses: (listPartial == "view-asset" ) ? "active" : null,
                    url: "/publisher/asset/operations/view/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name: "Edit",
                    iconClass: "icon-edit",
                    additionalClasses: (listPartial == "edit-asset" ) ? "active" : null,
                    url: "/publisher/asset/operations/edit/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name: "Documentation",
                    iconClass: "icon-file-alt",
                    additionalClasses: (listPartial == "documentation" ) ? "active" : null,
                    url: "/publisher/asset/operations/documentation/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name: "Copy",
                    iconClass: "icon-file",
                    additionalClasses: (listPartial == "copy-app" ) ? "active" : null,
                    url: "/publisher/asset/operations/copyapp/" + data.shortName + "/" + data.artifact.id + ""
                }
            ]
            };
        }else{
            leftNavItems = { leftNavLinks: [
                {
                    name: "Overview",
                    iconClass: "icon-list-alt",
                    additionalClasses: (listPartial == "view-asset" ) ? "active" : null,
                    url: "/publisher/asset/operations/view/" + data.shortName + "/" + data.artifact.id + ""
                }
            ]
            };
        }
    }
    return leftNavItems;
};

getTypeObj = function(type){
	for(item in breadcrumbItems){
		var obj = breadcrumbItems[item]
		if(obj.assetType == type){
			return obj;
		}
	}
}
