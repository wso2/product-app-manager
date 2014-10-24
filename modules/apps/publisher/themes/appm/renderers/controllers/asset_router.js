/*
	Description: Renders the asset.jag view
	Filename:asset.js
	Created Date: 29/7/2013
*/
var render=function(theme,data,meta,require){

    var log = new Log();

    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"
	var listPartial='view-asset';
    var heading = "";
	//Determine what view to show
	switch(data.op){
        case 'create':
            listPartial='add-asset';
            heading = "Add New Web Application";
            break;
        case 'view':
            data = require('/helpers/view-asset.js').merge(data);
            data = require('/helpers/splitter.js').splitData(data);
            heading = data.newViewData.name.value;
            listPartial='view-asset';
            break;
        case 'edit':
            data = require('/helpers/edit-asset.js').processData(data);
            data = require('/helpers/splitter.js').splitData(data);
            heading = "Edit Web Application" + data.newViewData.name.value;

            listPartial='edit-asset';
            break;
        case 'lifecycle':
            listPartial='lifecycle-asset';
            heading = "Lifecycle";

            break;
        case 'versions':
            listPartial='versions-asset';
            heading = "Versions";

            break;
        case 'documentation':
            listPartial='documentation';
            heading = "Documentation";

            break;
        case 'copyapp':
                data = require('/helpers/copy-app.js').processData(data);
                data = require('/helpers/splitter.js').splitData(data);
                listPartial='copy-app';
                break;
        default:
            break;
	}
    var breadCrumbData = require('/helpers/breadcrumb.js').generateBreadcrumbJson(data);
    breadCrumbData.activeRibbonElement = listPartial;
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
                partial:listPartial,
		        context: data
            }
        ],
        heading: [
            {
                partial: 'heading',
                context: {title: heading, menuItems: require('/helpers/left-nav.js').generateLeftNavJson(data, listPartial)}
            }
        ]
    });
};
