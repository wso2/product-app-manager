var carbon=require('carbon');
var server = require('store').server;
var storeUser=require('store').user;


/*
	Description: Encapsulates the web application management services to discover
	the available applications

	NOTE: [Dev] Objects created from this module is kept in the session. Please re-login if the
	code is changed on this file while editing Jaggery App on-live.

	Filename:discover.js
	Created Date: 17/03/2015
*/

var discover_client=function(){
    var log=new Log();
    var cookies;
    var hostPart;

	function DiscoverClient(){
	    hostPart = carbon.server.address('https');
	}

	/*
      Calls the discover service and get the list of applications


    */
    DiscoverClient.prototype.discoverWebapps=function(serviceUrl, userName, password, pageNumber,
                appStatus, appNameStartsWith, loggedInUsername){
        var data = {"credentials" :
                    {"userName" : userName, "appServerUrl" : serviceUrl, "password" : password ,
                    "loggedInUsername" : loggedInUsername},
                   "searchCriteria" :
                    {"applicationName" : appNameStartsWith, "status" : appStatus, "pageNumber" : pageNumber }
                   } ;
        var url = hostPart+'/api/v1/apps/mobile/discovery/app/list/a';
        try {

            var xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.setRequestHeader("Content-Type", "application/json" );

            if(cookies != null) {
                xhr.setRequestHeader("Cookie", cookies );
            }
            xhr.send(stringify(data));

            if(xhr.status === 200) {
                var response = parse(xhr.responseText);
                var result = this.convertToUiList(response);
                var respCookies = xhr.getResponseHeader("Set-Cookie");
                if(respCookies != null) {
                    cookies = respCookies;
                }
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
         result.moreResultsPossible = remoteResult.moreResultsPossible;
         result.totalNumberOfPagesKnown = remoteResult.totalNumberOfPagesKnown;

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
         var hasMoreResultsString = remoteResult.moreResultsPossible == true ? ' and more': '';
         var hasMorePagesString = remoteResult.totalNumberOfPagesKnown == false ? ' + ': '';
         result.status.description = 'Successfully Queried the server; Showing in ['+
            remoteResult.pageCount+hasMorePagesString+'] pages';
         return result;
    }

	DiscoverClient.prototype.getApplicationData2=function(serviceUrl, userName, password,
        		appType, serverType, applicationId) {

        var data = {"applicationId" : applicationId} ;

        var url = hostPart+'/api/v1/apps/mobile/discovery/app/info/a';
        try {

            var xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.setRequestHeader("Content-Type", "application/json" );
            if(cookies != null) {
                xhr.setRequestHeader("Cookie", cookies );
            }
            xhr.send(stringify(data));
            if(xhr.status === 200) {
                var result = {"status": { "code": "0", "description":""}};
                var data = parse(xhr.responseText);
                result.data = data;
                var respCookies = xhr.getResponseHeader("Set-Cookie");
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


