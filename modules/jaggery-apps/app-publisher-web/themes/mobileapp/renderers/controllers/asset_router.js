/*
	Description: Renders the asset.jag view
	Filename:asset.js
	Created Date: 29/7/2013
*/
var server = require('store').server;
var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');

var render=function(theme,data,meta,require){
    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"

    var user=server.current(session);
    var um=server.userManager(user.tenantId);
    var createMobileAppAuthorized = permissions.isAuthorized(user.username, config.permissions.mobileapp_create, um);
    var updateMobileAppAuthorized = permissions.isAuthorized(user.username, config.permissions.mobileapp_update, um);

    if(!updateMobileAppAuthorized){
        response.sendError(400);
        return;
    }

	var listPartial='view-asset';
    var heading = "";
    var mobileNotifications = session.get('mobileNotifications');
    var mobileNotificationCount = session.get('mobileNotificationCount');
	//Determine what view to show
	switch(data.op){
	case 'create':
		listPartial='add-asset';
		if(data.data.meta.shortName=='mobileapp'){
			//log.info('Special rendering case for mobileapp-using add-mobilepp.hbs');
			listPartial='add-mobileapp';
		}
        heading = "Create New Mobile App";
		break;
	case 'view':
		listPartial='view-asset';
        data = require('/helpers/splitter.js').splitData(data);
        heading = data.newViewData.name.value;
		break;
    case 'edit':
        listPartial='edit-asset';
        if(data.data.meta.shortName=='mobileapp'){
			//log.info('Special rendering case for mobileapp-using edit-mobilepp.hbs');
			listPartial='edit-mobileapp';
		}
        data = require('/helpers/edit-asset.js').selectCategory(data);
        data = require('/helpers/edit-asset.js').screenshots(data);

        data = require('/helpers/splitter.js').splitData(data);
        if(data.artifact.lifecycleState == "Published"){
            response.sendError(400);
            return;
        }
        heading = data.newViewData.name.value;
        break;
    case 'lifecycle':
        listPartial='lifecycle-asset';
        heading = "Lifecycle";
        break;
    case 'versions':
        listPartial='versions-asset';
        heading = "Versions";
        break;
	default:
		break;
	}

    var breadCrumbData = require('/helpers/breadcrumb.js').generateBreadcrumbJson(data);
    breadCrumbData.activeRibbonElement = listPartial;
    breadCrumbData.createMobileAppPerm = createMobileAppAuthorized;

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
		        context:{
                    active:listPartial,
                    isNotReviwer:data.isNotReviwer,
                    createMobileAppPerm:createMobileAppAuthorized,
                    mobileNotifications : mobileNotifications,
                    mobileNotificationCount: mobileNotificationCount
                }
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
                partial:listPartial,
		        context: data
            }
        ],
        heading: [
            {
                partial:'heading',
                context: {title:heading,menuItems:require('/helpers/left-nav.js').generateLeftNavJson(data, listPartial)}
            }
        ]
    });
};
