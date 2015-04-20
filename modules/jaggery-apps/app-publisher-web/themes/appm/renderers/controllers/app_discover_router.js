/*
 Description: Renders the assets.jag view
 Filename:assets.js
 Created Date: 29/7/2013
 */

var server = require('store').server;
var permissions=require('/modules/permissions.js').permissions;
var config = require('/config/publisher.json');
var lcModule = require('/modules/comment.js');
var user=server.current(session);
var um=server.userManager(user.tenantId);
var publisher = require('/modules/publisher.js').publisher(request, session);
var rxtManager = publisher.rxtManager;

var render = function (theme, data, meta, require) {
    var log = new Log();


    var notifications = session.get('notifications');
    var notificationCount = session.get('notificationCount');

    var listPartial = 'discover';

    //Determine what view to show
    switch (data.op) {

        case 'app_discover':
            listPartial = 'app_discover';
            break;
        default:
            break;
    }


    var breadCrumbData = require('/helpers/breadcrumb.js').generateBreadcrumbJson(data);
    var createActionAuthorized = permissions.isAuthorized(user.username, config.permissions.webapp_create, um);
    var viewStatsAuthorized = permissions.isAuthorized(user.username, config.permissions.view_statistics, um);

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
                partial: listPartial,
                context: data
            }
        ]
    });
};
