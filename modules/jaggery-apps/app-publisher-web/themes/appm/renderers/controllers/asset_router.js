/*
 Description: Renders the asset.jag view
 Filename:asset.js
 Created Date: 29/7/2013
 */
var server = require('store').server;
var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');

var render=function(theme,data,meta,require){

    var log = new Log();

    var user=server.current(session);
    var um=server.userManager(user.tenantId);
    var createActionAuthorized = permissions.isAuthorized(user.username, config.permissions.webapp_create, um);

    var viewStatsAuthorized = permissions.isAuthorized(user.username, config.permissions.view_statistics, um);

    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"
    var listPartial='view-asset';
    var heading = "";
    var newViewData;
    var notifications = session.get('notifications');
    var notificationCount = session.get('notificationCount');
    //Determine what view to show
    switch(data.op){

        case 'create':
            listPartial='add-asset';
            heading = "Create New Web Application";
            break;
        case 'view':
            data = require('/helpers/view-asset.js').merge(data);
            listPartial='view-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.displayName.value;
            break;
        case 'edit':
            var editEnabled = permissions.isEditPermitted(user.username, data.artifact.path, um);
            if(data.artifact.lifecycleState == "Published"){
                editEnabled = false;
            }
            if(user.hasRoles(["admin"])){
                editEnabled = true;
            }
            if(!editEnabled){
                response.sendError(404);
            }
            data = require('/helpers/edit-asset.js').processData(data);
            listPartial='edit-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.displayName.value;
            break;
        case 'lifecycle':
            listPartial='lifecycle-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.displayName.value + " - Lifecycle";
            break;
        case 'versions':
            listPartial='versions-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.displayName.value + " - Versions";
            break;
        case 'copyapp':
            data = require('/helpers/copy-app.js').processData(data);
            listPartial='copy-app';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.displayName.value;
            break;
        default:
            break;
    }


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
                    context: {
                        active:listPartial,
                        createPermission : createActionAuthorized,
                        viewStats : viewStatsAuthorized,
                        notifications : notifications,
                        notificationCount: notificationCount
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
                    partial: 'heading',
                    context: {title:heading, menuItems: require('/helpers/left-nav.js').generateLeftNavJson(data, listPartial)}
                }
            ]
        });



};
