var server = require('store').server;
var user=server.current(session);
var config = require('/config/publisher.json');

var render=function(theme,data,meta,require){

    var canEdit =  user.isAuthorized(config.permissions.webapp_update, "ui.execute") ||
                        user.isAuthorized(config.permissions.webapp_create, "ui.execute");
    data.canedit = canEdit;

    var log = new Log();
  
   	var listPartial='inline-editor';



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
     		        context:require('/helpers/breadcrumb.js').generateBreadcrumbJson(data)
                 }
        ],
        listassets: [
            {
                partial:listPartial,
		        context: data
            }
        ]
    });
};