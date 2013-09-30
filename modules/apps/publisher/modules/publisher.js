var PUBLISHER_CONFIG_PATH = '/_system/config/publisher/configs/publisher.json';

var TENANT_PUBLISHER = 'tenant.publisher';
var log=new Log('modules.publisher');
var utility=require('/modules/utility.js').rxt_utility();
var SUPER_TENANT=-1234;

var init = function (options) {
    var event = require('/modules/event.js');

    event.on('tenantCreate', function (tenantId) {
        var carbon = require('carbon'),
            config = require('/config/publisher-tenant.json'),
            server = require('/modules/server.js'),
            system = server.systemRegistry(tenantId),
            um = server.userManager(tenantId),
            CommonUtil = Packages.org.wso2.carbon.governance.registry.extensions.utils.CommonUtil,
            GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants;

        system.put(PUBLISHER_CONFIG_PATH, {
            content: JSON.stringify(config),
            mediaType: 'application/json'
        });

        CommonUtil.addRxtConfigs(system.registry.getChrootedRegistry("/_system/governance"), tenantId);
        um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);
        log.info('TENANT CREATED');
        addLifecycles(system);
    });

    event.on('tenantLoad', function (tenantId) {
        var user = require('/modules/user.js'),
            server = require('/modules/server.js'),
            carbon = require('carbon'),
            config = server.configs(tenantId);
        var reg = server.systemRegistry(tenantId);
        var CommonUtil = Packages.org.wso2.carbon.governance.registry.extensions.utils.CommonUtil;
        var GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants;
        var um = server.userManager(tenantId);
        var publisherConfig=require('/config/publisher-tenant.json')

        //check whether tenantCreate has been called
        if (!reg.exists(PUBLISHER_CONFIG_PATH)) {
            event.emit('tenantCreate', tenantId);
        }

        config[user.USER_OPTIONS] = configs(tenantId);

        //Check if the tenant is the super tenant
        if(tenantId==SUPER_TENANT){

            log.info('executing default asset deployment logic since super tenant has been loaded.');

            log.info('attempting to load rxt templates to the registry.');

            //Try to deploy the rxts
            CommonUtil.addRxtConfigs(reg.registry.getChrootedRegistry("/_system/governance"), reg.tenantId);
            um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);

            log.info('finished loading rxt templates to the registry.');

            //Attempt to load the default assets
            var deployer = require('/modules/asset.deployment.js').deployment_logic();

            log.info('starting auto deployment of default assets.');

            //Create a deployment manager instance
            var deploymentManager = new deployer.Deployer({
                config: publisherConfig.defaultAssets
            });

            log.info('initializing deployementManager');

            deploymentManager.init();

            deploymentManager.autoDeploy();

            log.info('finished auto deployment of default assets.');
        }


    });



    event.on('login', function (tenantId, user, session) {
        configureUser(tenantId, user);
    });
};

var configs = function (tenantId) {
    var server = require('/modules/server.js'),
        registry = server.systemRegistry(tenantId);
    return JSON.parse(registry.content(PUBLISHER_CONFIG_PATH));
};


var addLifecycles = function (registry) {
    var lc,
        files = new File('/config/lifecycles'),
        rootReg = registry.registry,
        configReg = rootReg.getChrootedRegistry('/_system/config'),
        CommonUtil = Packages.org.wso2.carbon.governance.lcm.util.CommonUtil;
    files.listFiles().forEach(function (file) {
        file.open('r');
        lc = file.readAll();
        file.close();

        //Create an xml from the contents
        var lcXml=new XML(lc);

        //Create a JSON object
        //TODO:This could be a problem -we are passing xml to JSON everytime!
        var lcJSON=utility.xml.convertE4XtoJSON(lcXml);

        //Check if the lifecycle is present
        var isPresent=CommonUtil.lifeCycleExists(lcJSON.name,configReg);

        log.info('Is life-cycle present: '+isPresent);

        //Only add the lifecycle if it is not present in the registry
        if(!isPresent){

            log.info('Adding life-cycle since it is not deployed.');

            CommonUtil.addLifecycle(lc, configReg, rootReg);
        }

    });
};

var publisher = function (o, session) {
    var publisher, tenantId,
        user = require('/modules/user.js'),
        server = require('/modules/server.js');

    tenantId = (o instanceof Request) ? server.tenant(o, session).tenantId : o;

    publisher = session.get(TENANT_PUBLISHER);
    if (publisher) {
        return publisher;
    }
    publisher = new Publisher(tenantId, session);
    session.put(TENANT_PUBLISHER, publisher);
    return publisher;
};

var Publisher = function (tenantId, session) {
    var server = require('/modules/server.js'),
        user = require('/modules/user.js'),
        managers = buildManagers(tenantId, user.userRegistry(session));
    this.tenantId = tenantId;
    this.modelManager = managers.modelManager;
    this.rxtManager = managers.rxtManager;
    this.routeManager = managers.routeManager;
    this.dataInjector=managers.dataInjector;
    this.DataInjectorModes=managers.DataInjectorModes;
};
/*

 Publisher.prototype.rxtManager = function() {
 return this.rxtManager;
 };*/

var buildManagers = function (tenantId, registry) {
    var ext_parser = require('/modules/ext/core/extension.parser.js').extension_parser();
    var ext_domain = require('/modules/ext/core/extension.domain.js').extension_domain();
    var ext_core = require('/modules/ext/core/extension.core.js').extension_core();
    var ext_mng = require('/modules/ext/core/extension.management.js').extension_management();
    var rxt_management = require('/modules/rxt.manager.js').rxt_management();
    var route_management = require('/modules/router-g.js').router();
    var dataInjectorModule=require('/modules/data/data.injector.js').dataInjectorModule();


    var dataInjector=new dataInjectorModule.DataInjector();
    var injectorModes=dataInjectorModule.Modes;

    //var server=new carbon.server.Server(url);
    /*var registry=new carbon.registry.Registry(server,{
     systen:true,
     username:username,
     tenantId:carbon.server.superTenant.tenantId
     });*/
    var rxtManager = new rxt_management.RxtManager(registry);
    var routeManager = new route_management.Router();

    var server = require('/modules/server.js');
    var conf = configs(tenantId);
    var config = server.configs(tenantId);

    routeManager.setRenderer(conf.router.RENDERER);

    //All of the rxt xml files are read and converted to a JSON object called
    //a RxtTemplate(Refer rxt.domain.js)
    rxtManager.loadAssets();

    var parser = new ext_parser.Parser();

    //Go through each rxt template
    rxtManager.rxtTemplates.forEach(function (rxtTemplate) {
        parser.registerRxt(rxtTemplate);
    });

    parser.load(conf.paths.RXT_EXTENSION_PATH);

    var adapterManager = new ext_core.AdapterManager({parser: parser});
    adapterManager.init();

    var fpManager = new ext_core.FieldManager({parser: parser});
    fpManager.init();

    var ruleParser = new ext_parser.RuleParser({parser: parser});
    ruleParser.init();

    var actionManager = new ext_core.ActionManager({templates: parser.templates});
    actionManager.init();

    var modelManager = new ext_mng.ModelManager({parser: parser, adapterManager: adapterManager, actionManager: actionManager, rxtManager: rxtManager});

    return {
        modelManager: modelManager,
        rxtManager: rxtManager,
        routeManager: routeManager,
        dataInjector:dataInjector,
        DataInjectorModes:injectorModes
    };
};

/*
 The function is used to fill the permissions object. The permissions are applied
 to the users space e.g. username= test , and the gadget collection in the /_system/governance
 then the permissions will be applicable to
 /_system/governance/gadget/test.
 @username: The username of the account to which the permissions will be attached
 @permissions: An object of permissions which will be assigned to the newly created user role
 */
var buildPermissionsList = function (tenantId, username, permissions, server) {
    var log = new Log();
    log.info('Entered buildPermissionsList');
    var server = require('/modules/server.js');
    //Obtain the accessible collections
    var accessible = server.options(tenantId).userSpace.accessible;
    log.info(stringify(accessible));

    var id;
    var accessibleContext;
    var accessibleCollections;
    var context;
    var actions;
    var collection;
    var sysRegistry = server.systemRegistry(tenantId);

    //Go through all of the accessible directives
    for (var index in accessible) {

        accessibleContext = accessible[index];

        accessibleCollections = accessibleContext.collections;

        context = accessibleContext.context;     //e.g. /_system/governance/
        actions = accessibleContext.actions;     //read,write

        //Go through all of the collections
        for (var colIndex in accessibleCollections) {

            collection = accessibleCollections[colIndex];

            //Create the id used for the permissions
            id = context + '/' + collection + '/' + username;


            //Check if a collection exists
            var col = sysRegistry.get(id);

            //Only add permissions if the path  does not exist
            if (col == undefined) {
                log.info('collection: ' + id + ' does not exist.');
                //Assign the actions to the id
                permissions[id] = actions;

                //Create a dummy collection, as once permissions are
                //the user will be unable to create assets in the
                //parent collection.
                //Thus we create a user collection.
                sysRegistry = server.systemRegistry(tenantId);

                //Create a new collection if a new one does not exist
                sysRegistry.put(id, {
                    collection: true
                });
            }
            else {
                log.info('collection: ' + id + 'is present.');
            }
        }

    }

    return permissions;
};

/*
 The function is used to configure a user that is about to login
 It performs the following;
 1. Add permissions for the accessible collections
 2. Assign a set of default roles (private_username and publisher)
 3. Check if a collection exists,if not create a new one.
 */

var configureUser = function (tenantId, user) {

    //Ignore adding permissions for the admin
    if (user.username == 'admin') {
        return;
    }

    var server = require('/modules/server.js');
    var umod = require('/modules/user.js');
    var um = server.userManager(tenantId);
    var config = configs(tenantId);
    var user = um.getUser(user.username);
    var perms = {};
    var role = umod.privateRole(user.username);
    var defaultRoles = config.userRoles;
    var log = new Log();

    log.info('Starting configuringUser.');

    //Create the permissions in the options configuration file
    perms = buildPermissionsList(tenantId, user.username, perms, server);

    //Only add the role if permissions are present

    if (!checkIfEmpty(perms)) {

        //log.info('length: '+perms.length);

        //Register the role
        //We assume that the private_role is already present
        //TODO: This needs to be replaced.
        um.authorizeRole(role, perms);

        //log.info('after add role');

        //user.addRoles(role);
    }

};

var checkIfEmpty = function (object) {
    for (var index in object) {
        if (object.hasOwnProperty(index)) {
            return false;
        }
    }

    return true;
};
