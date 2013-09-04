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
var leftNavItems = { leftNavLinks :
    [
        {
            name : "Browse All",
            additionalClasses : "prominent-link",
            url : "/publisher/assets/gadget/"
        },
        {
            name : "Overview",
            iconClass : "icon-list-alt",
            url : "#"
        },
        {
            name : "Edit",
            iconClass : "icon-edit",
            url : "#"
        },
        {
            name : "Life Cycle",
            iconClass : "icon-retweet",
            url : "#"
        },
        {
            name : "Versions",
            iconClass : "icon-qrcode",
            url : "#"
        }
    ]
};
var render=function(theme,data,meta,require){

	var listPartial='view-asset';
	//Determine what view to show
	switch(data.op){
	case 'create':
		listPartial='add-asset';
		break;
	case 'view':
		listPartial='view-asset';
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
