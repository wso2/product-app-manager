(function(){
    
	var log = new Log();

    var sso_sessions = application.get('sso_sessions');
    
    // 'sso_sessions' property is availabe only after the user signs in.
    // So we should proceed only if 'sso_sessions' is available.
    if(sso_sessions){
    	log.debug("Deleting SSO session " + sso_sessions[session.getId()] + " for the HTTP session " + session.getId());
    	delete sso_sessions[session.getId()];
    }

}());