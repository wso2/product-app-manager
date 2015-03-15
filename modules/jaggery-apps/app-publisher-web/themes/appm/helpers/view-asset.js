var resources=function(page,meta){

	return {
        js: ['view.asset.js', 'logic/documentation.js', 'bootstrap-select.min.js','options.text.js','jagg.js','validate.js'],
        css:['bootstrap-select.min.css','doc.css']
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
	     
     var appMDAO = Packages.org.wso2.carbon.appmgt.impl.dao.AppMDAO;
     var appMDAOObj = new appMDAO();
     var result  = appMDAOObj.getApplicationKeyPair(data.artifact.attributes.overview_name,data.artifact.attributes.overview_provider);
     
     var sResult = result.toString();
      sResult = sResult.substr(1, sResult.length()-2);

	var resultPolicy = JSON.parse(appMDAOObj.getPolicyGroupXACMLPoliciesByApplication(data.artifact.id));

	 for(var i in data.data.fields) {

		 if (data.data.fields[i].name == 'oauthapis_webappConsumerKey') {
			 data.data.fields[i].value = sResult.split(",")[0];
		 } else if (data.data.fields[i].name == 'oauthapis_webappConsumerSecret')
			 data.data.fields[i].value = sResult.split(",")[1];


		 for (var c in data.data.fields) {

			 if (data.data.fields[i].name == "uritemplate_policygroupid" + c) {
				 if (data.data.fields[i].value != "" && !isNaN(data.data.fields[i].value)) {
                     //replace group policy id by name
                     for(var j=0; j < resultPolicy.length; j++){
                        if(resultPolicy[j].POLICY_GRP_ID == data.data.fields[i].value){
                            data.data.fields[i].value = {
                                'id': resultPolicy[j].POLICY_GRP_ID,
                                'name': resultPolicy[j].POLICY_GRP_NAME,
                                'tire': resultPolicy[j].THROTTLING_TIER,
                                'anonymous':resultPolicy[j].URL_ALLOW_ANONYMOUS,
                                'roles':resultPolicy[j].USER_ROLES,
                                'partials':resultPolicy[j].POLICY_PARTIAL_NAME
                            };
                        }
                     }

				 }
			 }


		 }
	 }


	 return data;
};


