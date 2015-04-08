var carbon=require('carbon');


/*
	Description: Encapsulates the web application management services to discover
	the available applications
	Filename:discover.js
	Created Date: 17/03/2015
*/

var discover_client=function(){
    var log=new Log();

	function DiscoverClient(registry){
		this.registry=registry;
	}

	/*
      Calls the discover service and get the list of applications


    */
    DiscoverClient.prototype.discoverWebapps=function(serviceUrl, userName, password, pageNumber){

        var data = {"credentials" :
                    {"userName" : userName, "appServerUrl" : serviceUrl, "password" : password ,
                    "loggedInUsername" : "admin"},
                   "searchCriteria" :
                    {"applicationName" : "", "status" : "New", "pageNumber" : 0 }
                   } ;
        var url = 'https://localhost:9443/api/v1/apps/mobile/discovery/app/list/a';
        try {

        var xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.setRequestHeader("Content-Type", "application/json" );
            xhr.send(stringify(data));

            if(xhr.status === 200) {
                var response = parse(xhr.responseText);
                var result = this.convertToUiList(response);
                return result;
            } else {
                var result = {"status": { "code": "0", "description":""}, "metadataList": []};
                result.status.code = 403;
                result.status.description = "Application server can not be reached with given credentials";
                return result;
            }
        } catch (e) {
          var result = {"status": { "code": "0", "description":""}, "metadataList": []};
          result.status.code = 403;
          result.status.description = "Application server can not be reached with given credentials";
          return result;
        }
    }

    /*
    Translate the remote service reply to the object notation used in the page

    Sample reply:
          {
              "pageCount": 2,
              "currentPage": 0,
              "authCookie": null,
              "applicationList": [
                {
                  "applicationId": "async_jaxws_war",
                  "serverType": "WSO2-AS",
                  "applicationType": "jaxWebapp",
                  "applicationName": JAX-WS/JAX-RS Webapp async_jaxws_war,
                  "displayName": "JAX-WS/JAX-RS Webapp",
                  "version": "1.0",
                  "proxyContext": "/async_jaxws",
                  "remoteContext": "/async_jaxws",
                  "remoteVersion": "1.0.0",
                  "remoteHost": "127.0.0.1",
                  "portMap": [{"http" : "8080", "https" : "8443"}]
                },...
                ]
          }
    */
    DiscoverClient.prototype.convertToUiList = function(remoteResult) {
         var result = {"status": { "code": "0", "description":""}, "metadataList": []};
         result.status.code = 200;
         result.numberOfPages = remoteResult.pageCount;
         result.authCookie = remoteResult.authCookie;

         var arrayLength = remoteResult.applicationList.length;
         for (var i = 0; i < arrayLength; i++) {
             var elem = remoteResult.applicationList[i];
             var obj = {};
             obj.name = elem.displayName;
             obj.version = elem.version;
             obj.appType = elem.applicationType;
             obj.context = elem.remoteContext;
             obj.proxyContext = elem.proxyContext;
             obj.status = elem.status;
             obj.webappId = elem.applicationId;
             obj.id = elem.applicationId;
             obj.applicationUrl = elem.applicationUrl;
             obj.applicationPreviewUrl = elem.applicationPreviewUrl;

             result.metadataList.push(obj);
         }

         result.status.description = 'Successfully Queried the server; found ['
            +remoteResult.totalNumberOfResults
            +'] Applications; Showing in ['+remoteResult.pageCount+'] pages';
         return result;
    }

	DiscoverClient.prototype.getApplicationData2=function(serviceUrl, userName, password,
        		appType, serverType, applicationId) {

        var data = {"credentials" :
                    {"userName" : userName, "appServerUrl" : serviceUrl, "password" : password ,
                    "loggedInUsername" : "admin"},
                   "searchCriteria" :
                    {"applicationName" : applicationId, "status" : "New", "pageNumber" : 0 }
                   } ;

        var url = 'https://localhost:9443/api/v1/apps/mobile/discovery/app/info/a';
        try {

            var xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.setRequestHeader("Content-Type", "application/json" );
            xhr.send(stringify(data));
            if(xhr.status === 200) {
                var result = {"status": { "code": "0", "description":""}};
                var data = parse(xhr.responseText);
                result.data = data;
                return result;
            } else {
                var result = {"status": { "code": "0", "description":""}, "metadataList": []};
                result.status.code = 403;
                result.status.description = "Application server can not be reached with given credentials";
                return result;
            }
        } catch (e) {
          var result = {"status": { "code": "0", "description":""}, "metadataList": []};
          result.status.code = 403;
          result.status.description = "Application server can not be reached with given credentials";
          return result;
        }
    }

	return {
		DiscoverClient:DiscoverClient
	};
};


