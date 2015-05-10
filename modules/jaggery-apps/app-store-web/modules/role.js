var checkeRole = function (username, session) {
	var opts, um,role 
      	authorized = false,
      	carbon = require('carbon'),
      	event = require('event'),
      	usr = carbon.server.tenantUser(username);
	var log = new Log();

    var config = require('/config/store.json');

    var apiUtil = Packages.org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
    var apiUtil = new apiUtil();

	var user = require('store').user;
	if (!user.configs(usr.tenantId)) {
		event.emit('tenantLoad', usr.tenantId);
	}

	opts = user.configs(usr.tenantId);
	role = opts.userRoles;
  
	var server=require('store').server;
  	um = server.userManager(usr.tenantId);
  	usr = um.getUser(usr.username);
  	usr.tenantDomain = carbon.server.tenantDomain({tenantId: usr.tenantId});

  	//if (!usr.hasRoles([role[0]])) {
      	//	return false;
  	//}

    if (!(apiUtil.checkPermissionWrapper(username, config.permissions.webapp_subscribe)
          || apiUtil.checkPermissionWrapper(username, config.permissions.mobileapp_subscribe))) {
           return false;
  	}

    event.emit('login', usr.tenantId, usr, session);
  	return true;
};
