/*
 Description: Renders the assets.jag view
 Filename:assets.js
 Created Date: 29/7/2013
 */

var server = require('store').server;
var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');
var user=server.current(session);
var um=server.userManager(user.tenantId);

var render = function (theme, data, meta, require) {



    var lifecycleColors = {"Create": "btn-green", "Recycle": "btn-blue", "Re-Publish": "btn-blue", "Publish": "btn-blue", "Unpublish": "btn-orange", "Deprecate": "btn-danger", "Retire": "btn-danger", "Approve": "btn-blue", "Reject": "btn-orange"};

    if(data.artifacts){

        var deleteButtonAvailability = false;


        for(var i = 0; i < data.artifacts.length; i++){
            var lifecycleAvailableActionsButtons = new Array();
            if(permissions.isLCActionsPermitted(user.username,data.artifacts[i].path,um)) {
                for (var j = 0; j < data.artifacts[i].lifecycleAvailableActions.length; j++) {
                    var name = data.artifacts[i].lifecycleAvailableActions[j];

                    for(var k = 0; k < data.roles.length; k++){

                        if (name == "Approve") {
                            lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                        }
                        if (name == "Reject") {
                            lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                        }
                        if (name == "Publish") {
                            lifecycleAvailableActionsButtons.push({name: name, style: lifecycleColors[name]});
                        }
                        if(name == "Recycle"){
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

                        break;

                    }


                }
            }

            data.artifacts[i].lifecycleAvailableActions = lifecycleAvailableActionsButtons;

            //Adding the delete button
            if (permissions.isDeletePermitted(user.username,data.artifacts[i].path,um)) {
                deleteButtonAvailability = true;
            }

            data.artifacts[i].deleteButtonAvailability = deleteButtonAvailability;
        }



    }

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
        case 'app_sub_user':
            listPartial = 'app_sub_user';
            break;
        case 'apps':
            listPartial = 'apps';
            break;
        case 'response-time':
            listPartial = 'response-time';
            break;
        case 'usage-page':
            listPartial = 'usage-page';
            break;
        case 'cache-stat':
            listPartial = 'cache-stat';
            break;

        default:
            break;
    }


    var breadCrumbData = require('/helpers/breadcrumb.js').generateBreadcrumbJson(data);
    var createActionAuthorized = permissions.isAuthorized(user.username, config.permissions.webapp_create, um);
    breadCrumbData.activeRibbonElement = listPartial;
    breadCrumbData.createPermission = createActionAuthorized;
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
                context: breadCrumbData
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
