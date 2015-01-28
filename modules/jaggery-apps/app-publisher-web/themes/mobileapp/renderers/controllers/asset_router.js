/*
	Description: Renders the asset.jag view
	Filename:asset.js
	Created Date: 29/7/2013
*/
var render=function(theme,data,meta,require){
    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"
	var listPartial='view-asset';
    var heading = "";
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
		        context:breadCrumbData
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
