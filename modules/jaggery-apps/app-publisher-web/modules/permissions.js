var permissions={};
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
        var roles = userManager.getRoleListOfUser(username);;
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


    permissions.isLCActionsPermitted = isLCActionsPermitted;
    permissions.isDeletePermitted = isDeletePermitted;

}());