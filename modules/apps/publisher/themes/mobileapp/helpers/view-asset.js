var resources=function(page,meta){

    return{
        js:['view.asset.js']
    };

};

var format = function(data){
	var unixtime, newDate,
		newDate = new Date(),
		artifacts = data.artifacts;
	for(var i in artifacts){
		unixtime = artifacts[i].attributes['overview_createdtime'];
		newDate.setTime(unixtime);
		data.artifacts[i].attributes['overview_createdtime'] = newDate.toUTCString();
	}
	return data;
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


    data.name=overview_name;
    data.description=overview_description;
    data.main=overview_main;
    data.meta=overview_meta;
    data.images=images;
    data.isMoreInfo=isMoreInfo;

    return data;
};
