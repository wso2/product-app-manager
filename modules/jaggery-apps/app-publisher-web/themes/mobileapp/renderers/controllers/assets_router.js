/*
 Description: Renders the assets.jag view
 Filename:assets.js
 Created Date: 29/7/2013
 */

var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');
var server = require('store').server;
var user=server.current(session);
var um=server.userManager(user.tenantId);

var render = function (theme, data, meta, require) {

	data.isNotReviwer = true;
    data.isReviwer = false;
    data.isAdmin = false;

    var publishActionAuthorized = permissions.isAuthorized(user.username, config.permissions.mobileapp_publish, um);
    var createMobileAppAuthorized = permissions.isAuthorized(user.username, config.permissions.mobileapp_create, um);

    if(publishActionAuthorized){
        data.isNotReviwer = false;
        data.isReviwer = true;
    }

   for(var k = 0; k < data.roles.length; k++){
       if(data.roles[k] == "admin"){
           data.isAdmin = true;
       }

	}

	var lifecycleColors = {"Demote": "btn-blue", "Submit for Review": "btn-blue", "Publish": "btn-blue", "Unpublish": "btn-orange", "Deprecate": "btn-danger", "Retire": "btn-danger", "Approve": "btn-blue", "Reject": "btn-danger"};
	if(data.artifacts){
        var log = new Log();
		for(var i = 0; i < data.artifacts.length; i++){
		var lifecycleAvailableActionsButtons = new Array();

            var pubActions = config.publisherActions;

		for(var j = 0; j < data.artifacts[i].lifecycleAvailableActions.length; j++){
			var name = data.artifacts[i].lifecycleAvailableActions[j];

			for(var k = 0; k < data.roles.length; k++){
                var skipFlag = false;

                if(pubActions.indexOf(String(name)) > -1){
                    log.info("## Skip called!!! for name : "+name);
                    if(!publishActionAuthorized) {
                        skipFlag = true;
                    }
                }

                if(!skipFlag) {
                    if (name == "Publish") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Reject") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Submit for Review") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Recycle") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Deprecate") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Re-Publish") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Unpublish") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Depreicate") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Retire") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    if (name == "Approve") {
                        lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                    }
                    break;
                }
			}


		}

		data.artifacts[i].lifecycleAvailableActions = lifecycleAvailableActionsButtons;
		}



	}

	
	//print(data);

    var listPartial = 'list-assets';
//Determine what view to show
    switch (data.op) {
        case 'list':
            listPartial = 'list-assets';
             data = require('/helpers/view-asset.js').format(data);
            break;
        case 'statistics':
            listPartial = 'statistics';
            break;
        default:
            break;
    }
    //var addAssetUrl = "/publisher/asset/" + data.meta.shortName +"";
    theme('single-col-fluid', {
        title: data.title,
        header: [
            {
                partial: 'header',
                context: data
            }
        ],
        ribbon: [
            {
                partial: 'ribbon',
                context: {active:listPartial,isNotReviwer:data.isNotReviwer,createMobileAppPerm:createMobileAppAuthorized}
            }
        ],
        leftnav: [
            {
                partial: 'left-nav',
                context: require('/helpers/left-nav.js').generateLeftNavJson(data, listPartial)
            }
        ],
        listassets: [
            {
                partial: listPartial,
                context: data
            }
        ]
    });
};
