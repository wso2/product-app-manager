include("/extensions/webapp/modules/jagg/jagg.jag");
var permissions={};
var manager = jagg.module("manager").getAPIStoreObj();

(function() {
    var log=new Log();
    /**
     * The function checks whether a user can perform life-cycle actionss
     * @param  {[type]}  username     The name of the user for which the check must be performed
     * @param  {[type]}  resourcePath The registry resource path
     * @param  {[type]}  userManager
     * @return {Boolean}			  True if the user can perform life-cycle actions
     */
    var isLCActionsPermitted = function(username, resourcePath, userManager) {
        log.debug('###Checking permissions ###');
        var roles = userManager.getRoleListOfUser(username);
        var action = 'authorize';
        var role;
        for (var index in roles) {
            role=roles[index];
            var isAuthorized = userManager.isAuthorized(role, resourcePath, action);
            log.debug('Role: '+role+' resource: '+resourcePath+'action: '+action);
            if (isAuthorized) {
                return true;
            }
        }
        return false;
    };


    /**
     * The function checks whether a user can delete an asset
     * @param  {[type]}  username     The name of the user for which the check must be performed
     * @param  {[type]}  resourcePath The registry resource path
     * @param  {[type]}  userManager
     * @return {Boolean}			  True if the user can perform life-cycle actions
     */
    var isDeletePermitted = function(username, resourcePath, userManager) {
        log.debug('###Checking delete permissions ###');
        var roles = userManager.getRoleListOfUser(username);;
        var action = 'delete';
        var role;
        for (var index in roles) {
            role=roles[index];
            var isAuthorized = userManager.isAuthorized(role, resourcePath, action);
            log.debug('Role: '+role+' resource: '+resourcePath+'action: '+action);
            if (isAuthorized) {
                return true;
            }
        }
        return false;
    };


    /**
     * The function checks whether a user can Edit an asset
     * @param  {[type]}  username     The name of the user for which the check must be performed
     * @param  {[type]}  resourcePath The registry resource path
     * @param  {[type]}  userManager
     * @return {Boolean}			  True if the user can perform life-cycle actions
     */
    var isEditPermitted = function(username, resourcePath, userManager) {
        log.debug('###Checking delete permissions ###');
        var roles = userManager.getRoleListOfUser(username);;
        var action = 'add';
        var role;
        for (var index in roles) {
            role=roles[index];
            var isAuthorized = userManager.isAuthorized(role, resourcePath, action);
            log.debug('Role: '+role+' resource: '+resourcePath+'action: '+action);
            if (isAuthorized) {
                return true;
            }
        }
        return false;
    };

    /**
     * The function checks whether a user can delete an asset
     * @param  {[type]}  username     The name of the user for which the check must be performed
     * @param  {[type]}  resourcePath The registry resource path
     * @param  {[type]}  userManager
     * @return {Boolean}			  True if the user can perform life-cycle actions
     */
    var isAuthorized = function(username, permission, userManager) {
        var isEmailLoginEnabled = manager.isEnableEmailUsername();
        log.debug('### CHECKING PERMISSINON! ###');
        var uname = username;
        if(!isEmailLoginEnabled) {
            uname = username.split('@')[0];
        }
        var action = "ui.execute";
        var user = userManager.getUser(uname);
        log.debug("Authorization Check : "+user.isAuthorized(permission,action));
        return  user.isAuthorized(permission,action);
    };

    permissions.isEditPermitted = isEditPermitted;
    permissions.isAuthorized = isAuthorized;
    permissions.isLCActionsPermitted = isLCActionsPermitted;
    permissions.isDeletePermitted = isDeletePermitted;

}());