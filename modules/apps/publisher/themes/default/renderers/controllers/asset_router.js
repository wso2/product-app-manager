/*
	Description: Renders the asset.jag view
	Filename:asset.js
	Created Date: 29/7/2013
*/
var breadcrumbData = { breadcrumb :
    [
        {
            assetType : "Gadget",
            url : "/publisher/assets/gadget/",
            assetIcon : "icon-dashboard", //font-awesome icon class
            assets : [
                {assetName : "Bar Chart", "versions" : ["1.0.0", "2.0.0", "2.0.1"]},
                {assetName : "Line Chart", "versions" : ["1.0.2", "1.0.4", "3.1.1"]},
                {assetName : "Horizontal Bar Chart", "versions" : ["1.1.2", "1.4.1", "2.1.1"]}
            ]
        },
        {
            assetType : "EBook",
            url : "/publisher/assets/ebook/",
            assets : [
                {assetName : "The art of Unix Programming", "versions" : ["Edition-1", "Edition-2", "Edition-3"]},
                {assetName : "The Complete Works of H.P.Lovecraft", "versions" : ["Edition-1", "Edition-2", "Edition-3"]}
            ]
        }
    ]
};

var render=function(theme,data,meta,require){
    //var _url = "/publisher/asset/"  + data.meta.shortName + "/" + data.info.id + "/edit"
    var leftNavItems = { leftNavLinks :
        [
            {
                name : "Browse All",
                additionalClasses : "prominent-link",
                url : "/publisher/assets/" + data.shortName + "/"
            },
            {
                name : "Add " + data.shortName + "",
                iconClass : "icon-plus-sign-alt",
                url : "/publisher/asset/" + data.shortName + ""
            },
            {
                name : "Statistics",
                iconClass : "icon-dashboard",
                url : "#"
            }
        ]
    };
    if(data.artifact){
        leftNavItems = { leftNavLinks :
            [
                {
                    name : "Browse All",
                    additionalClasses : "prominent-link",
                    url : "/publisher/assets/" + data.shortName + "/"
                },
                {
                    name : "Overview",
                    iconClass : "icon-list-alt",
                    url : "/publisher/asset/operations/view/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name : "Edit",
                    iconClass : "icon-edit",
                    url : "/publisher/asset/operations/edit/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name : "Life Cycle",
                    iconClass : "icon-retweet",
                    url : "/publisher/asset/operations/lifecycle/" + data.shortName + "/" + data.artifact.id + ""
                },
                {
                    name : "Versions",
                    iconClass : "icon-qrcode",
                    url : "/publisher/asset/operations/versions/" + data.shortName + "/" + data.artifact.id + ""
                }
            ]
        };
    }


	var listPartial='view-asset';
	//Determine what view to show
	switch(data.op){
	case 'create':
		listPartial='add-asset';
		break;
	case 'view':
		listPartial='view-asset';
		break;
    case 'edit':
        listPartial='edit-asset';
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
		        context:breadcrumbData
            }
        ],
        leftnav: [
            {
                partial: 'left-nav',
                context: leftNavItems
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
