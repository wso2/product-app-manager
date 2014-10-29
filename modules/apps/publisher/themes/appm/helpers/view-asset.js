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
};


