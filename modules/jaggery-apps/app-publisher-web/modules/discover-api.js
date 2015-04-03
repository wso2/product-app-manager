var api = {};
(function(api){
    var HTTP_ERROR_NOT_IMPLEMENTED = 501;
    var HTTP_ERROR = 500;
    var MSG_ERROR_NOT_IMPLEMENTED = 'The provided action is not supported by this endpoint';

    var log= new Log();

    var discover_client = require('/modules/discover.js').discover_client();

    var msg = function(code, message, data) {
        var obj = {};
        obj.code = code;
        obj.message = message;
        obj.data = data;
        return obj;
    };

    var successMsg = function(obj) {
        obj.success = true;
        return obj;
    };
    var errorMsg = function(obj) {
        obj.success = false;
        return obj;
    };

    var createAsset = function (context, data, shortName, id, rxtManager, modelManager) {

        var artifactManager = rxtManager.getArtifactManager(shortName);

        //check for mobile
        if(shortName === "mobileapp"){
          addNewMobileApp(context, rxtManager);
          return;
        }


        //Check if the type is valid

          var model=modelManager.getModel(shortName);

          //assigning default thumbnail and banner if not provided.
          if(context.post['images_thumbnail'] == '') {
              context.post['images_thumbnail'] = '/publisher/config/defaults/img/thumbnail.jpg';
          }
          if(context.post['images_banner'] == '') {
              context.post['images_banner'] = '/publisher/config/defaults/img/banner.jpg';
          }



          model.import('form.importer',data);

          //Perform validations on the asset
          var report=model.validate();

          //If the report indicates the model has failed validations send an error
          if((report)&&(report.failed)){
              print({ok:false,message:'Validation failure',report:report});
              return;
          }

          //var assetModel = getModel(context.post);

          model.save();

          //var createdAsset = artifactManager.add(assetModel);

          //Get the model id
          var idField = model.get('*.id');

          if (!idField) {
              log.debug('An asset of type: ' + shortName + ' could not be created.Probably a fault with publisher logic!');
          }

    };

    /**
     * Allows request data to be sent in either the request body or
     * as url encoded query parameters
     * @param  {Object} req  Request object
     * @return {Object}      An object which contains data from request body and url encoded parameters
     */
    var obtainData = function(req) {
        var data = {};
        if (req.getContentType() === 'application/json') {
            try {
                data = req.getContent();
            } catch (e) {
                log.debug('Unable to obtain content from request', e);
            }
        }
        var params = req.getAllParameters('UTF-8');
        //Mix the content with request parameters
        for (var key in params) {
            data[key] = params[key];
        }
        return data;
    };

    api.createProxy  = function(context, req, res, session, options) {
        var publisher = require('/modules/publisher.js').publisher(request, session);
        var rxtManager = publisher.rxtManager;
        var modelManager = publisher.modelManager;

        var result = {};
        try {
            var shortName = options.appType;
            var applicationId = options.appId;
            var artifactManager = rxtManager.getArtifactManager(shortName);
            var proxyContext= context.post['proxy_context_path'];
            var loggedInUser = jagg.getUser().username;

            log.debug('Asset API discover asset add called '+applicationId);
            //check for mobile
            if(shortName === "mobileapp"){
                log.error('Mobile application discovery is not yet supported.');
                result = {ok: 'false', message: 'Mobile application discovery is not yet supported.'};
                return;
            }

            var sessionData = session.get('sessionData');
            log.debug('sessionData '+sessionData);

            if(sessionData == null) {
                log.error('Session expired');
                result = {ok: 'false', message: 'Session expired'};
                return;
            }

            var discover = new discover_client.DiscoverClient(null);
            var applicationMetaData = discover.getApplicationData(sessionData.serverUrl,
                                    sessionData.serverUserName, sessionData.serverPassword,
                                    shortName, "wso2as", applicationId);
            applicationMetaData.id = applicationId ;
            applicationMetaData.proxyContext = proxyContext ;
            applicationMetaData.overview_provider = loggedInUser;
            applicationMetaData.overview_name= applicationId;
            if(applicationMetaData.overview_version == '/default') {
                applicationMetaData.overview_version = '1.0';
            }

            createAsset(context, applicationMetaData, shortName, applicationId, rxtManager, modelManager);

            result = {ok: 'true', message: 'Asset added', applicationId : applicationMetaData.overview_name,
                        appName : applicationMetaData.overview_displayName,
                        proxyContext: proxyContext};

            return successMsg(msg(200, 'The asset is created', result));
        } catch (e) {
            log.error('An asset of type: ' + shortName + ' could not be created.The following exception was thrown: ' + e);
            return errorMsg(msg(HTTP_ERROR,
                'An asset of type: ' + shortName + ' could not be created.Please check the server logs. Reason: '+e));
        }
    };

    api.resolve = function(ctx, req,res,session,uriParams){
        var action = uriParams.action;
        var result = errorMsg(msg(HTTP_ERROR_NOT_IMPLEMENTED, MSG_ERROR_NOT_IMPLEMENTED));
        var data = obtainData(req);
        ctx.post = data;
        switch(action){
            case 'createAsset' :
                result = api.createProxy(ctx, req, res, session, uriParams);
                break;
            default:
                break;
        }

        return result;
    };
}(api));