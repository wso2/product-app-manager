/*
	Description: Initialization script
	Filename:app.js
	Created Date: 29/7/2013
*/

var caramel=require('caramel'),
	rxt_management=require('/modules/rxt.manager.js').rxt_management(),
	route_management=require('/modules/router-g.js').router(),
	config=require('/config/publisher.json'),	
	carbon=require('carbon'),
	url='https://localhost:9443/admin',
	username='admin',
	password='admin',
	mediaType='application/vnd.wso2.registry-ext-type+xml'; //TODO: Change the url

var server=new carbon.server.Server(url);
var registry=new carbon.registry.Registry(server,{
		systen:true,
		username:username,
		tenantId:carbon.server.superTenant.tenantId
});
var rxtManager=new rxt_management.RxtManager(registry);
var routeManager=new route_management.Router();


routeManager.setRenderer(config.router.RENDERER);

//All of the rxt xml files are read and converted to a JSON object called
//a RxtTemplate(Refer rxt.domain.js)
rxtManager.loadAssets();

application.put(config.app.RXT_MANAGER,rxtManager);
application.put(config.app.ROUTE_MANAGER,routeManager);

//Configure Caramel
caramel.configs({
	context:'/publisher',
	cache:true,
	negotiation:true,
	themer:function(){
		//TODO: Hardcoded theme
		return 'publisher';
	}

});




