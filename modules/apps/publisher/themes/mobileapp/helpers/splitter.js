var resources=function(page,meta){

    return{
        js:['splitter.js']
    };

};

var splitData = function(data){
    var log = new Log();
    var dataPart = data.data.fields;
    var overview_name;
    var overview_meta = [];
    var overview_main = [];
    var overview_description;
    var uriTemplate = [];
    var images = [];
    var isMoreInfo = false;

    for(var i=0;i<dataPart.length;i++) {
        if (dataPart[i].name == "overview_name") {
            overview_name = dataPart[i];
        }else if (dataPart[i].name == "overview_provider") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_context") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_status") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_version") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_recentchanges") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_packagename") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_category") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_appid") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_bundleversion") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_url") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_url") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_description") {
            overview_description = dataPart[i];
            if(overview_description.length > 400){
                isMoreInfo = true;
            }
        }else if (dataPart[i].name.search("uritemplate_")!=-1) {
            uriTemplate.push(dataPart[i]);
        }else if (dataPart[i].name.search("images_")!=-1) {
            images.push(dataPart[i]);
        }
    }
    var newViewData = {};

    newViewData.name=overview_name;
    newViewData.description=overview_description;
    newViewData.main=overview_main;
    newViewData.meta=overview_meta;
    newViewData.images=images;
    newViewData.isMoreInfo=isMoreInfo;

    data.newViewData = newViewData;

    return data;
};


