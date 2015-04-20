var checkeRole = function (username, session) {
	var opts, um,role 
      	authorized = false,
      	carbon = require('carbon'),
      	event = require('event'),
      	usr = carbon.server.tenantUser(username);
	var log = new Log();
	var user = require('store').user;
    var config = require('/config/publisher.json');

    var apiUtil = Packages.org.wso2.carbon.appmgt.impl.utils.AppManagerUtil;
    var apiUtil = new apiUtil();
    var ADMIN_ROLE = Packages.org.wso2.carbon.context.PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm().getRealmConfiguration().getAdminUserName();

	if (!user.configs(usr.tenantId)) {
		event.emit('tenantLoad', usr.tenantId);
	}
	opts = user.configs(usr.tenantId);
	role = opts.userRoles;
  	var server=require('store').server;
  	um = server.userManager(usr.tenantId);
  	usr = um.getUser(usr.username);
  	usr.tenantDomain = carbon.server.tenantDomain({tenantId: usr.tenantId});

  	event.emit('login', usr.tenantId, usr, session);

    var roles = um.getRoleListOfUser(usr.username);

    for (var index in roles) {
        if(roles[index] == ADMIN_ROLE){
            return true;
        }
    }

    if (!(apiUtil.checkPermissionWrapper(username, config.permissions.webapp_create)
          || apiUtil.checkPermissionWrapper(username, config.permissions.webapp_publish)
            || apiUtil.checkPermissionWrapper(username, config.permissions.mobileapp_create)
                || apiUtil.checkPermissionWrapper(username, config.permissions.mobileapp_publish))) {
        return false;
    }
  	return true;
};
