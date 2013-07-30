/*
	Description: Initialization script
	Filename:app.js
	Author: Sameera M.
	Created Date: 29/7/2013
*/

var caramel=require('caramel');

var l=new Log();

l.info('loaded');

//Configure Caramel
caramel.configs({
	context:'/publisher',
	cache:true,
	negotiation:true,
	themer:function(){
		//TODO: Hardcoded theme
		return 'theme0';
	}

});




