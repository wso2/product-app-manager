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
    var newViewData;
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
            heading = data.newViewData.name.value;
            break;
        case 'edit':
            data = require('/helpers/edit-asset.js').processData(data);
            listPartial='edit-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.name.value + " - Edit";
            break;
        case 'lifecycle':
            listPartial='lifecycle-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.name.value + " - Lifecycle";
            break;
        case 'versions':
            listPartial='versions-asset';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.name.value + " - Versions";
            break;
        case 'documentation':
            listPartial='documentation';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.name.value + " - Documentation";
            break;
        case 'copyapp':
            data = require('/helpers/copy-app.js').processData(data);
            listPartial='copy-app';
            var copyOfData = parse(stringify(data));
            data.newViewData =  require('/helpers/splitter.js').splitData(copyOfData);
            heading = data.newViewData.name.value + " - Copy";
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
                    context: {active:listPartial}
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
