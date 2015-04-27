$(function(){
    var placeHolders= {
        "server-url": "WSO2AS URL (e.g. https://localhost:9445/services/)",
        "server-username": "User Name who has permission to list Applications",
        "server-password" : "Password for the AS",
        "app-name" : "Keep empty to list all Contexts"
    }

    $(document).ready(function(){
        $.each(placeHolders, function(key,val) {
           $('#'+key).attr("placeholder", val);
        });
    });

    /**
    * Sets up the policy group ID s on the relevant resources and the main policy group
    */
    function setupPolicyGroups(postData, policyGroupId) {
        postData.uritemplate_policyGroupIds = '['+policyGroupId+']';
        postData.uritemplate_policyGroupId0 = policyGroupId;
        postData.uritemplate_policyGroupId1 = policyGroupId;
        postData.uritemplate_policyGroupId2 = policyGroupId;
        postData.uritemplate_policyGroupId3 = policyGroupId;
        postData.uritemplate_policyGroupId4 = policyGroupId;
    }

    /**
    * Save policy group
    * @param policyGroupName :Policy Group Name
    * @param throttlingTier :Throttling Tier
    * @param anonymousAccessToUrlPattern : if anonymous access allowed for the related url pattern/verb
    * @param userRoles : User Roles
    * @param appliedXacmlRules : Applied XACML rules.
    * @param policyGroupDesc : Policy Group DEscription
    */
    function insertPolicyGroup( policyGroupName, throttlingTier, anonymousAccessToUrlPattern,
            userRoles, appliedXacmlRules ,policyGroupDesc, onSuccess, postData) {
       jQuery.ajax({
           async: true,
           url: '/publisher/api/entitlement/policy/partial/policyGroup/save',
           type: 'POST',
           data: {
               "policyGroupName": policyGroupName,
               "throttlingTier": throttlingTier,
               "userRoles": userRoles,
               "anonymousAccessToUrlPattern": anonymousAccessToUrlPattern,
               "objPartialMappings": JSON.stringify(appliedXacmlRules),
               "policyGroupDesc" :policyGroupDesc
           },
           success: function (data) {
               var editedPolicyGroupResp = JSON.parse(data);
               if(editedPolicyGroupResp.success) {
                setupPolicyGroups(postData, editedPolicyGroupResp.response.id);
                onSuccess(postData);
               }

           },
           error: function () {
               console.log('Could not create the policy group');
           }
       });
    }


    var doPostWebappCreation = function (postData) {
          jQuery.ajax({
              url: '/publisher/api/asset/webapp',
              type: "POST", data: postData, async:true, dataType : 'json',
              success: function (response) {
                jQuery('#discover-create-asset-status').modal('show');
                var statusText = jQuery('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-error');
                statusText.addClass('alert-info');
                statusText.text('The Proxy Application ['+postData.overview_displayName+'] is successfully created with proxy context ['+postData.overview_name+']');

                createServiceProvider(postData);
              },
              error: function (response) {
                var statusText = jQuery('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-info');
                statusText.addClass('alert-error');
                jQuery('#gridSystemModalLabel').text('Application Creation Failed');
                statusText.text('Failed to create the application ['+postData.overview_name+']. Please consult server administrator for more information.');
                jQuery('#discover-create-asset-status').modal('show');
              }
          }, 'json');
    }

    function doCreateWebapp(postData) {
       insertPolicyGroup('Default', 'Unlimited', false, '', [], '', doPostWebappCreation, postData);
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
               "uritemplate_policyGroupId3": "1",
               "overview_provider": "FIXME",
               "uritemplate_policyGroupId4": "1",
               "uritemplate_urlPattern1": "/*",
               "uritemplate_urlPattern0": "/*",
               "uritemplate_policyGroupId0": "1",
               "uritemplate_policyGroupId1": "1",
               "uritemplate_policyGroupId2": "1",
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
               "uritemplate_policyGroupIds": "[]",
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

    function getTrackingCode() {
        // random number between 0 to 1 e.g-0.5838903994299471
        var randomNum = Math.random();
        var code =randomNum.toString();
        code = code.replace("0.","");
        var tracking_code_id = "AM_"+code;

        return tracking_code_id;
    }

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
        jQuery.ajax({
                url: '/publisher/api/sso/addConfig',
                type: 'POST',
                contentType: 'application/json',
                data:JSON.stringify(sso_config),
                success: function(response) {
                    jQuery('#discover-create-asset-status #statusText').
                        text('The Proxy Application ['+data.overview_displayName+'] is successfully created with proxy context ['+data.overview_context+']');
                    jQuery('#gridSystemModalLabel').text('Application Creation Successful');
                },
                error: function(response) {
                    jQuery('#discover-create-asset-status #statusText').
                        text('Failed adding SSO to the Application:'+sso_config.app_name);
                    jQuery('#gridSystemModalLabel').text('Application Creation Failed');
                }
        });
    };

    jQuery(".btn-create-discovered" ).click(function(e) {
        var $this = $(this);
        var app = $this.data("app");
        var action = $this.data("action");
        var id = $this.data("id");

        //proxy context entered
        var proxyContext = jQuery('#proxy-context-'+id).val();

        var postData = {"proxy_context_path" : proxyContext};
        if(action=="Reject") {
            showCommentModel("Reason for Rejection",action,app);
        }else{
            //Disable the button as we do not have a proper feedback from server yet. This is workaround to have better user experience
            $this.attr('disabled','disabled');
            jQuery('#application-status-'+id).text('PROCESSING');

            jQuery.ajax({
                url: '/publisher/api/discover/asset/loadCreatableAsset/webapp/'+id,
                type: "POST", data: postData, async:false, dataType : 'json',
                success: function (response) {
                    jQuery('#discover-create-asset-status #statusText').text('Application is being imported...');
                    //Convert the response to a JSON object
                    var statInfo = response.data;
                    if(statInfo.ok == 'true') {
                        var postData = getWebappCreationPostData();

                        postData.overview_trackingCode = getTrackingCode();

                        importDiscoveredData(postData, statInfo.data);
                        doCreateWebapp(postData);
                    }else{
                        console.log('Error in the serve: '+statInfo.message);
                    }
                },
                error: function (response) {
                    var statusText = jQuery('#discover-create-asset-status #statusText');
                    statusText.removeClass('alert-info');
                    statusText.addClass('alert-error');
                    statusText.text('Failed to create the application.');
                    jQuery('#gridSystemModalLabel').text('Application Creation Failed');
                }
            }, 'json');
        }
        e.stopPropagation();
    });
} );