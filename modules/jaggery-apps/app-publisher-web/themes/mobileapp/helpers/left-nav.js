var getTypeObj, breadcrumbItems;
var deploymentManagement=require('/modules/deployment/deployment.manager.js').deploymentManagementModule();
var deploymentManager=deploymentManagement.cached();

breadcrumbItems = deploymentManager.getAssetData()

var generateLeftNavJson = function(data, listPartial) {
	
	var currentTypeObj = getTypeObj(data.shortName);
	
    var leftNavItems = { leftNavLinks :
        [
           /*
            {
                           name : "Browse All",
                           additionalClasses : "active",
                           url : "/publisher/assets/" + data.shortName + "/"
                       },*/
           
           /* {
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
        leftNavItems = { leftNavLinks :
            [
               /*
                {
                                   name : "Browse All",
                                   additionalClasses : "active",
                                   url : "/publisher/assets/" + data.shortName + "/"
                               },*/
               
//                {
//                    name : "Overview",
//                    iconClass : "icon-list-alt",
//                    additionalClasses : (listPartial == "view-asset" ) ? "active" : null,
//                    url : "/publisher/asset/operations/view/" + data.shortName + "/" + data.artifact.id + ""
//                },
//                {
//                    name : "Edit",
//                    iconClass : "icon-edit",
//                     additionalClasses : (listPartial == "edit-asset" ) ? "active" : null,
//                    url : "/publisher/asset/operations/edit/" + data.shortName + "/" + data.artifact.id + ""
//                },
//                {
//                    name : "Life Cycle",
//                    iconClass : "icon-retweet",
//                     additionalClasses : (listPartial == "lifecycle-asset" ) ? "active" : null,
//                    url : "/publisher/asset/operations/lifecycle/" + data.shortName + "/" + data.artifact.id + ""
//                }
            ]
        };
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
