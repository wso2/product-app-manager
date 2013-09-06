/*
 Description: Renders the assets.jag view
 Filename:assets.js
 Created Date: 29/7/2013
 */
var breadcrumbData = { breadcrumb :
    [
        {
            assetType : "Gadget",
            url : "/publisher/assets/gadget/",
            assetIcon : "icon-dashboard" //font-awesome icon class
        },
        {
            assetType : "EBook",
            url : "/publisher/assets/ebook/",
            assetIcon : "icon-book" //font-awesome icon class
        },
        {
            assetType : "Site",
            url : "/publisher/assets/site/",
            assetIcon : "icon-compass" //font-awesome icon class
        }
    ]
};

var render = function (theme, data, meta, require) {
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
                context: breadcrumbData
            }
        ],
        leftnav: [
            {
                partial: 'left-nav',
                context: require('/helpers/left-nav.js').getLeftNavLinks(data)
            }
        ],
        listassets: [
            {
                partial: 'list-assets',
                context: data
            }
        ]
    });
};
