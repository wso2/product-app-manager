/*
 Description:The class is used to secure  pages
 Created Date: 5/10/2013
 Filename: url.security.provider.js
 */

var securityModule = function () {

    var LOGGED_IN_USER = 'LOGGED_IN_USER';
    var log=new Log('url.security.provider');
    /*
     The function checks if a user is present in the session
     */
    function isPermitted() {

        //Obtain the session and check if there is a user
        var loggedInUser = session.get(LOGGED_IN_USER);

        if (loggedInUser) {
            return true;
        }

        return false;
    }

    /*
     The function is invoked when the the security check fails
     */
    function onSecurityCheckFail() {
        log.info('security check failed redirecting...');
        response.sendRedirect('/publisher/login');
    }

    /*
     The function is invoked when the security check is passed
     */
    function onSecurityCheckPass() {
        //Do nothing for now :)
    }

    return{

        isPermitted: isPermitted,
        onSecurityCheckFail: onSecurityCheckFail,
        onSecurityCheckPass: onSecurityCheckPass

    }
};