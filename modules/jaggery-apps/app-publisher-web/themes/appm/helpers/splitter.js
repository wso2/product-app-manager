var resources=function(page,meta){

    return{
        js:['splitter.js']
    };

};
function endsWith(suffix,str) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
function isEmpty(obj) {
    for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }

    return true;
}
var splitData = function(copyOfData){
    var dataPart = copyOfData.data.fields;
    var overview_name;
    var overview_displayName;
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
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_provider") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_context") {
            overview_meta.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_version") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_displayName") {
            overview_displayName = dataPart[i];
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_webAppUrl") {
            overview_main.push(dataPart[i]);
        }else if (dataPart[i].name == "overview_tier") {
           // overview_main.push(dataPart[i]);
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

    var alreadyGot = [];
    var uriTemplateSorted = [];
    for(i=0;i<uriTemplate.length;i++){
        var numberInAction = parseInt(uriTemplate[i].name.match(/\d+$/)[0]);
        if(alreadyGot.indexOf(numberInAction) == -1 ){
            var patternEntry = {};
            alreadyGot.push(numberInAction);
            for(var j=0;j<uriTemplate.length;j++){

                if(numberInAction == parseInt(uriTemplate[j].name.match(/\d+$/)[0])){
                    var secondNamePart = uriTemplate[j].name.split(numberInAction)[0];
                    if(secondNamePart == "uritemplate_urlPattern"){
                        patternEntry.urlPattern = uriTemplate[j].value;
                        patternEntry.policyUid = (uriTemplate[j].value).replace('/','-').replace('*','-');
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
                    }else if(secondNamePart == "uritemplate_allowanonymous"){
                        patternEntry.anonymous= uriTemplate[j].value;
                    }else if(secondNamePart == "uritemplate_policygroupid") {
                        patternEntry.policyGroupId = uriTemplate[j].value.id;
                        patternEntry.policyGroupName = uriTemplate[j].value.name;
                        patternEntry.policyGroupTire = uriTemplate[j].value.tire;
                        patternEntry.policyGroupAnonymous = uriTemplate[j].value.anonymous;
                        patternEntry.policyGroupRoles = uriTemplate[j].value.roles;
                        patternEntry.policyGroupPartials = uriTemplate[j].value.partials;
                    }

                }
            }
            if(!isEmpty(patternEntry)){
                uriTemplateSorted.push(patternEntry);
            }

        }
    }
    var newViewData = {};

    newViewData.name=overview_name;
    newViewData.displayName=overview_displayName;
    newViewData.description=overview_description;
    newViewData.main=overview_main;
    newViewData.meta=overview_meta;
    newViewData.uriTemplate=uriTemplateSorted;
    newViewData.images=images;
    newViewData.sso=sso;
    newViewData.oauthapis=oauthapis;
    newViewData.isMoreInfo=isMoreInfo;


    return newViewData;
};



