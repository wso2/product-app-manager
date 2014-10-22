/*
	Description: Renders the asset.jag view
	Filename:asset.js
	Created Date: 29/7/2013
*/
var render=function(theme,data,meta,require){
    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"
	var listPartial='view-asset';
	//Determine what view to show
	switch(data.op){
	case 'create':
		listPartial='add-asset';
		if(data.data.meta.shortName=='mobileapp'){
			//log.info('Special rendering case for mobileapp-using add-mobilepp.hbs');
			listPartial='add-mobileapp';
		}
		
		break;
	case 'view':
		listPartial='view-asset';
		break;
    case 'edit':
        listPartial='edit-asset';
        if(data.data.meta.shortName=='mobileapp'){
			//log.info('Special rendering case for mobileapp-using edit-mobilepp.hbs');
			listPartial='edit-mobileapp';
		}
        data = require('/helpers/edit-asset.js').selectCategory(data);
        data = require('/helpers/edit-asset.js').screenshots(data);
        break;
    case 'lifecycle':
        listPartial='lifecycle-asset';
        break;
    case 'versions':
        listPartial='versions-asset';
        break;
	default:
		break;
	}
    data = require('/helpers/view-asset.js').splitData(data);

    log.info(data.op);
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
		        context:{active:listPartial}
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
                context: {title:data.name.value,menuItems:require('/helpers/left-nav.js').generateLeftNavJson(data, listPartial)}
            }
        ]
    });
};
