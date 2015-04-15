$(function(){

    function doPostWebappCreation (postData) {

          jQuery.ajax({
              url: '/publisher/api/asset/webapp',
              type: "POST", data: postData, async:false, dataType : 'json',
              success: function (response) {
                $('#discover-create-asset-status').modal('show');
                var statusText = $('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-error');
                statusText.addClass('alert-info');
                statusText.text('The Proxy Application ['+postData.overview_displayName+'] is successfully created with proxy context ['+postData.overview_name+']');

                createServiceProvider(postData);
              },
              error: function (response) {
                var statusText = $('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-info');
                statusText.addClass('alert-error');
                statusText.text('Failed to create the application ['+postData.overview_name+']. Please consult server administrator for more information.');
                $('#discover-create-asset-status').modal('show');
              }
          }, 'json');
    }

    function getWebappCreationPostData() {
        var result = {
               "uritemplate_httpVerb4": "OPTIONS",
               "autoConfig": "on",
               "uritemplate_httpVerb0": "GET",
               "uritemplate_httpVerb1": "POST",
               "uritemplate_httpVerb2": "PUT",
               "uritemplate_httpVerb3": "DELETE",
               "overview_tier": "Unlimited",
               "optradio": "on",
               "overview_transports": "http",
               "sso_saml2SsoIssuer": "",
               "sso_idpProviderUrl": "https://localhost:9444/samlsso/",
               "uritemplate_urlPattern4": "/*",
               "uritemplate_urlPattern3": "/*",
               "uritemplate_urlPattern2": "/*",
               "providers": "wso2is-5.0.0",
               "webapp": "webapp",
               "uritemplate_policyGroupId3": "10",
               "overview_provider": "FIXME",
               "uritemplate_policyGroupId4": "10",
               "uritemplate_urlPattern1": "/*",
               "uritemplate_urlPattern0": "/*",
               "uritemplate_policyGroupId0": "10",
               "uritemplate_policyGroupId1": "10",
               "uritemplate_policyGroupId2": "10",
               "claims": "http://wso2.org/claims/otherphone",  //FIXME
               "oauthapis_apiConsumerSecret1": "",
               "oauthapis_apiConsumerSecret2": "",
               "sso_ssoProvider": "wso2is-5.0.0",
               "oauthapis_apiConsumerSecret3": "",
               "uritemplate_javaPolicyIds": "[]",
               "overview_allowAnonymous": "false",
               "overview_displayName": "FIXME",
               "images_thumbnail": "",
               "overview_webAppUrl": "FIXME",
               "sso_singleSignOn": "Enabled",
               "overview_logoutUrl": "",
               "roles": "",
               "images_banner": "",
               "claimPropertyCounter": "1",
               "uritemplate_policyPartialIdstemp": "[]",
               "oauthapis_apiTokenEndpoint3": "",
               "oauthapis_apiName2": "",
               "oauthapis_apiName3": "",
               "oauthapis_apiName1": "",
               "claimPropertyName0": "http://wso2.org/claims/role",
               "version": "",
               "overview_description": "T1",
               "uritemplate_policyGroupIds": "[10]",
               "overview_context": "/FIXME",
               "entitlementPolicies": "[]",
               "oauthapis_apiTokenEndpoint1": "",
               "oauthapis_apiTokenEndpoint2": "",
               "tags": "",
               "overview_trackingCode": "FIXME",
               "overview_name": "FIXME",
               "overview_skipGateway": "false",
               "oauthapis_apiConsumerKey3": "",
               "oauthapis_apiConsumerKey2": "",
               "oauthapis_apiConsumerKey1": "",
               "overview_version": "FIXME",
               "context": ""
                     };
        return result;
    };

    function importDiscoveredData(postData, webappInfo) {
        postData.overview_version = '1.0';
        postData.overview_context = webappInfo.proxyContext;
        postData.overview_name = webappInfo.applicationId;
        postData.overview_displayName = webappInfo.displayName;
        postData.overview_description = webappInfo.displayName + ' Imported by Discovery';
        postData.overview_webAppUrl = webappInfo.applicationUrl;
        postData.overview_provider = webappInfo.providerName;
    };

    function createServiceProvider(data){
        var sso_config = {};

        var claims = [];
        var index=0;
        var propertyCount = data.claimPropertyCounter;
        while(index < propertyCount){
                var claim = data['claimPropertyName'+index];
                if(claim != null){
                    claims[claims.length] = claim;
                }
                index++;
        }

        sso_config.provider = data.providers;
        sso_config.logout_url = data.overview_logoutUrl;
        sso_config.claims = claims;
        sso_config.idp_provider = data.sso_idpProviderUrl;
        sso_config.app_name = data.overview_name;
        sso_config.app_verison = data.overview_version;
        sso_config.app_transport = data.overview_transports;
        sso_config.app_context = data.overview_context;
        sso_config.app_provider = data.overview_provider;
        sso_config.app_allowAnonymous=data.overview_allowAnonymous;
//        alert(JSON.stringify(data));
            $.ajax({
                    url: '/publisher/api/sso/addConfig',
                    type: 'POST',
                    contentType: 'application/json',
                    data:JSON.stringify(sso_config),
                    success: function(response) {
                        $('#discover-create-asset-status #statusText').
                            text('Created the Application and SSO:'+sso_config.app_name);
                    },
                    error: function(response) {
                        $('#discover-create-asset-status #statusText').
                            text('Failed adding SSO to the Application:'+sso_config.app_name);
                    }
            });
    };

    $(".btn-create-discovered" ).click(function(e) {
        var $this = $(this);
        var app = $this.data("app");
        var action = $this.data("action");
        var id = $this.data("id")

        //proxy context entered
        var proxyContext = $('#proxy-context-'+id).val();

        var postData = {"proxy_context_path" : proxyContext};
        if(action=="Reject") {
            showCommentModel("Reason for Rejection",action,app);
        }else{


        //$('#discover-create-asset-status').modal('show');
        jQuery.ajax({
            url: '/publisher/api/discover/asset/loadCreatableAsset/webapp/'+id,
            type: "POST", data: postData, async:false, dataType : 'json',
            success: function (response) {
                $('#discover-create-asset-status #statusText').text('Application is being imported...');
                //Convert the response to a JSON object
                var statInfo = response.data;
                if(statInfo.ok == 'true') {
                    var postData = getWebappCreationPostData();
                    importDiscoveredData(postData, statInfo.data);
//                    console.log('Response received  ' +JSON.stringify(postData));
                   doPostWebappCreation(postData);
                }else{
                    console.log('Error in the serve: '+statInfo.message);
                }
            },
            error: function (response) {
                var statusText = $('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-info');
                statusText.addClass('alert-error');
                statusText.text('Failed to create the application.');
                $('#discover-create-asset-status #info-table').hide();
    //            $('#discover-create-asset-status #errorInfo').html(response.responseText);
            }
        }, 'json');


        }
        e.stopPropagation();
    });
} );