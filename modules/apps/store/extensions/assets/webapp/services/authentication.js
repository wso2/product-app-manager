/*
 Description: The service is used to authenticate users using the carbon administration module
 Filename: authentication.js
 Created Date: 10/1/2014
 */
var serviceModule = (function () {
    var log = new Log();

    /*
     A generic authenticator interface
     */
    function Authenticator() {
        this.instance = null;
        this.context = null;
    }

    Authenticator.prototype.init = function (context, session) {
        this.instance = context.module('user');
        this.context = context;
    };

    Authenticator.prototype.login = function (options) {
        log.info(options);
        var result = this.instance.login(options.username, options.password, options.tenant);

        //Check if an error has occurred
        if (result.error) {
            throw result.message;
        }
        else {
            var userData = {};
            userData['username'] = options.username;
            userData['isSuperTenant'] = result.isSuperTenant;
            userData['cookie'] = result.cookie;
            this.context.setUser(userData);

            log.info('********User Data *************');
            log.info(this.context.getUser());
        }

    };

    Authenticator.prototype.logout = function (options) {
        this.context.setUser(null);
    };

    Authenticator.prototype.getLoggedInUser=function(options){
        return this.context.getUser();
    };

    return {
        Authenticator: Authenticator
    };
})();