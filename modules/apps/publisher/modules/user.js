var USER = 'server.user';

var USER_REGISTRY = 'server.user.registry';

var USER_OPTIONS = 'server.user.options';

var USER_SPACE = 'server.user.space';

var USER_ROLE_PREFIX = 'private_';
var log=new Log();
var init = function (options) {
    var role, roles, user,
        server = require('/modules/server.js'),
        um = server.userManager();
    roles = options.roles;
    for (role in roles) {
        if (roles.hasOwnProperty(role)) {
            if (um.roleExists(role)) {
                um.authorizeRole(role, roles[role]);
            } else {
                um.addRole(role, [], roles[role]);
            }
        }
    }
    user = um.getUser(options.user.username);
    if (!user.hasRoles(options.userRoles)) {
        user.addRoles(options.userRoles);
    }
    application.put(USER_OPTIONS, options);
};

var options = function () {
    return application.get(USER_OPTIONS);
};

var login = function (username, password) {
    var user, perm, perms, actions, i, length,
        authorized = false,
        opts = options(),
        carbon = require('carbon'),
        server = require('/modules/server.js'),
        serv = server.server(),
        um = server.userManager();
    if (!serv.authenticate(username, password)) {
        return false;
    }
    user = um.getUser(username);
    perms = opts.permissions.login;
    L1:
        for (perm in perms) {
            if (perms.hasOwnProperty(perm)) {
                actions = perms[perm];
                length = actions.length;
                for (i = 0; i < length; i++) {
                    if (user.isAuthorized(perm, actions[i])) {
                        authorized = true;
                        break L1;
                    }
                }
            }
        }
    if (!authorized) {
        return false;
    }
    session.put(USER, new carbon.user.User(um, username));
    session.put(USER_REGISTRY, new carbon.registry.Registry(serv, {
        username: username,
        tenantId: carbon.server.tenantId()
    }));
    session.put(USER_SPACE, new carbon.user.Space(username, opts.userSpace.space, opts.userSpace.options));
    if (opts.login) {
        opts.login(user, password, session);
    }
    return true;
};

var isAuthorized = function (permission, action) {
    var user = current(),
        server = require('/modules/server.js'),
        um = server.userManager();
    user = user ? user.username : options().user.username;
    return um.getUser(user).isAuthorized(permission, action);
};

var userSpace = function () {
    try {
        return session.get(USER_SPACE);
    } catch (e) {
        return null;
    }
};

var userRegistry = function () {
    try {
        return session.get(USER_REGISTRY);
    } catch (e) {
        log.debug('user registry not loaded: '+e);
        return null;
    }
};

var logout = function () {
    var opts = options(),
        user = current();
    if (opts.logout) {
        opts.logout(user, session);
    }
    session.remove(USER);
    session.remove(USER_SPACE);
    session.remove(USER_REGISTRY);
};

var userExists = function (username) {
    var server = require('/modules/server.js');
    return server.userManager().userExists(username);
};

var privateRole = function (username) {
    return USER_ROLE_PREFIX + username;
};

/*
 The function is used to fill the permissions object. The permissions are applied
 to the users space e.g. username= test , and the gadget collection in the /_system/governance
 then the permissions will be applicable to
 /_system/governance/gadget/test.
 @username: The username of the account to which the permissions will be attached
 @permissions: An object of permissions which will be assigned to the newly created user role
 */
var buildPermissionsList=function(username,permissions,server){
    log.info('Entered buildPermissionsList');

    //Obtain the accessible collections
    var accessible=options().userSpace.accessible;
    log.info(stringify(accessible));

    var id;
    var accessibleContext;
    var accessibleCollections;
    var context;
    var actions;
    var collection;
    var sysRegistry=server.systemRegistry();;

    //Go through all of the accessible directives
    for(var index in accessible){

        accessibleContext=accessible[index];

        accessibleCollections=accessibleContext.collections;

        context=accessibleContext.context;     //e.g. /_system/governance/
        actions=accessibleContext.actions;     //read,write

        //Go through all of the collections
        for(var colIndex in accessibleCollections){

            collection=accessibleCollections[colIndex];

            //Create the id used for the permissions
            id=context+'/'+collection+'/'+username;


            //Check if a collection exists
            var col=sysRegistry.get(id);

            //Only add permissions if the path  does not exist
            if(col==undefined){
                log.info('collection: '+id+' does not exist.');
                //Assign the actions to the id
                permissions[id]=actions;

                //Create a dummy collection, as once permissions are
                //the user will be unable to create assets in the
                //parent collection.
                //Thus we create a user collection.
                sysRegistry=server.systemRegistry();

                //Create a new collection if a new one does not exist
                sysRegistry.put(id,{
                    collection:true
                });
            }
            else{
                log.info('collection: '+id+'is present.');
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

var configureUser=function(username){

    //Ignore adding permissions for the admin
    if(username=='admin'){
        return;
    }

    var server=require('/modules/server.js');
    var um=server.userManager();
    var opts=options();
    var user=um.getUser(username);
    var perms={};
    var role=privateRole(username);
    var defaultRoles=opts.userRoles;

    log.info('Starting configuringUser.');

    //Create the permissions in the options configuration file
    perms=buildPermissionsList(username,perms,server);

    //Only add the role if permissions are present

    if(!checkIfEmpty(perms)){

        //log.info('length: '+perms.length);

        //Register the role
        //We assume that the private_role is already present
        //TODO: This needs to be replaced.
        um.authorizeRole(role,perms);

        //log.info('after add role');

        //user.addRoles(role);
    }

};

var checkIfEmpty=function(object){
    for(var index in object){
        if(object.hasOwnProperty(index)){
            return false;
        }
    }

    return true;
}

var register = function (username, password) {
    var user, role, id, perms, r, p,
        server = require('/modules/server.js'),
        um = server.userManager(),
        opts = options();
    um.addUser(username, password, opts.userRoles);
    user = um.getUser(username);
    role = privateRole(username);
    id = opts.userSpace.options.path + '/' + opts.userSpace.space;
    perms = {};
    perms[id] = [
        'http://www.wso2.org/projects/registry/actions/get',
        'http://www.wso2.org/projects/registry/actions/add',
        'http://www.wso2.org/projects/registry/actions/delete',
        'authorize'
    ];

    p = opts.permissions.login;
    for (r in p) {
        if (p.hasOwnProperty(r)) {
            perms[r] = p[r];
        }
    }
    um.addRole(role, [], perms);
    user.addRoles([role]);
    if (opts.register) {
        opts.register(user, password, session);
    }
    login(username, password);
};

/**
 * Returns the currently logged in user
 */
var current = function () {
    try {
        return session.get(USER);
    } catch (e) {
        return null;
    }
};

var loginWithSAML = function (username) {
    var user, perm, perms, actions, i, length,
        authorized = false,
        opts = options(),
        carbon = require('carbon'),
        server = require('/modules/server.js'),
        serv = server.server(),
        um = server.userManager();

    user = um.getUser(username);
    perms = opts.permissions.login;
    L1:
        for (perm in perms) {
            if (perms.hasOwnProperty(perm)) {
                actions = perms[perm];
                length = actions.length;
                for (i = 0; i < length; i++) {
                    if (user.isAuthorized(perm, actions[i])) {
                        authorized = true;
                        break L1;
                    }
                }
            }
        }
    if (!authorized) {
        return false;
    }
    session.put(USER, new carbon.user.User(um, username));
    session.put(USER_REGISTRY, new carbon.registry.Registry(serv, {
        username: username,
        tenantId: carbon.server.tenantId()
    }));
    session.put(USER_SPACE, new carbon.user.Space(username, opts.userSpace.space, opts.userSpace.options));
    if (opts.login) {
        opts.login(user, "", session);
    }

    var permission = {};
    permission[opts.userSpace.options.path + '/' + username ] = [
        carbon.registry.actions.GET,
        carbon.registry.actions.PUT,
        carbon.registry.actions.DELETE
    ];
    um.authorizeRole(privateRole(username), permission);

    return true;
};