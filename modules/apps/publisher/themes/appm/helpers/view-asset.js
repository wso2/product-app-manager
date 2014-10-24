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
}

var merge = function(data){
	
     var log =  new Log();
	     
     var apiMgtDAO = Packages.org.wso2.carbon.appmgt.impl.dao.ApiMgtDAO;
     var apiMgtDAOObj = new apiMgtDAO();
     var result  = apiMgtDAOObj.getApplicationKeyPair(data.artifact.attributes.overview_name,data.artifact.attributes.overview_provider);
     
     var sResult = result.toString();
      sResult = sResult.substr(1, sResult.length()-2);

	 
	 for(var i in data.data.fields){

		 if (data.data.fields[i].name == 'oauthapis_webappConsumerKey'){
			 data.data.fields[i].value =  sResult.split(",")[0];
		 }else if(data.data.fields[i].name == 'oauthapis_webappConsumerSecret')
			 data.data.fields[i].value = sResult.split(",")[1];

	 }
	 return data;
}
var splitData = function(data){
    var log = new Log();
    var dataPart = data.data.fields;
    var overview_name;
    var overview_meta = [];
    var overview_main = [];
    var overview_description;
    var uriTemplate = [];
    var images = [];
    var sso = [];
    var oauthapis =[];
    var isMoreInfo = false;

    for(var i=0;i<dataPart.length;i++) {
        if (dataPart[i].name == "overview_name") {
            overview_name = dataPart[i];
        }else if (dataPart[i].name == "overview_provider") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_context") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_version") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_webAppUrl") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_tier") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_trackingCode") {
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
        }else if (dataPart[i].name.search("sso_")!=-1) {
            sso.push(dataPart[i]);
        }else if (dataPart[i].name.search("oauthapis_")!=-1) {
            oauthapis.push(dataPart[i]);
        }
    }
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
    function isEmpty(obj) {
        for(var prop in obj) {
            if(obj.hasOwnProperty(prop))
                return false;
        }

        return true;
    }
    var alreadyGot = [];
    var uriTemplateSorted = [];
    for(i=0;i<uriTemplate.length;i++){
        var numberInAction = parseInt(uriTemplate[i].name.split("uritemplate_urlPattern")[1]);

        if(alreadyGot.indexOf(numberInAction) == -1 ){
            var patternEntry = {};
            alreadyGot.push(numberInAction);
            for(var j=0;j<uriTemplate.length;j++){

                if(uriTemplate[j].name.endsWith(numberInAction)){
                    var secondNamePart = uriTemplate[j].name.split(numberInAction)[0];
                    if(secondNamePart == "uritemplate_urlPattern"){
                        patternEntry.urlPattern = uriTemplate[j].value;
                    } else if(secondNamePart == "uritemplate_httpVerb"){
                        patternEntry.httpVerb = uriTemplate[j].value;
                    }else if(secondNamePart == "uritemplate_tier"){
                        patternEntry.tire = uriTemplate[j].value;
                    }else if(secondNamePart == "uritemplate_skipthrottle"){
                        patternEntry.skip = uriTemplate[j].value;
                    }else if(secondNamePart == "uritemplate_userRoles"){
                        patternEntry.roles = uriTemplate[j].value;
                    }else if(secondNamePart == "uritemplate_entitlementPolicyPartialMappings"){
                        patternEntry.policy = uriTemplate[j].value;
                    }
                }
            }
            if(!isEmpty(patternEntry)){
                uriTemplateSorted.push(patternEntry);
            }

        }
    }

    data.name=overview_name;
    data.description=overview_description;
    data.main=overview_main;
    data.meta=overview_meta;
    data.uriTemplate=uriTemplateSorted;
    data.images=images;
    data.sso=sso;
    data.oauthapis=oauthapis;
    data.isMoreInfo=isMoreInfo;

    return data;
};



