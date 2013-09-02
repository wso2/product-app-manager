var USER = 'server.user';

var USER_REGISTRY = 'server.user.registry';

var USER_OPTIONS = 'server.user.options';

var USER_SPACE = 'server.user.space';

var USER_ROLE_PREFIX = 'private_';

/**
 * Initializes the user environment for the specified tenant. If it is already initialized, then will be skipped.
 */
var init = function (options) {
    var event = require('/modules/event.js');
    event.on('tenantCreate', function (tenantId) {
        var role, roles,
            server = require('/modules/server.js'),
            um = server.userManager(tenantId),
            options = server.options();
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
        /*user = um.getUser(options.user.username);
         if (!user.hasRoles(options.userRoles)) {
         user.addRoles(options.userRoles);
         }*/
        //application.put(key, options);
    });

    event.on('tenantLoad', function (tenantId) {

    });

    event.on('tenantUnload', function (tenantId) {

    });

    event.on('login', function (tenantId, user, session) {
        session.put(USER, user);
        session.put(USER_REGISTRY, new carbon.registry.Registry(server.server(), {
            username: user.username,
            tenantId: tenantId
        }));
        session.put(USER_SPACE, userSpace(user.username));
    });

    event.on('logout', function (tenantId, user, session) {
        session.remove(USER);
        session.remove(USER_SPACE);
        session.remove(USER_REGISTRY);
    });
};

/**
 * Returns user options of the tenant.
 * @return {Object}
 */
var options = function (tenantId) {
    var server = require('/modules/server.js');
    return server.configs(tenantId)[USER_OPTIONS];
};

/**
 * Logs in a user to the store. Username might contains the domain part in case of MT mode.
 * @param username ruchira or ruchira@ruchira.com
 * @param password
 * @return {boolean}
 */
var login = function (username, password) {
    var user, perm, perms, actions, i, length, um, opts, config,
        log = new Log(),
        authorized = false,
        carbon = require('carbon'),
        event = require('/modules/event.js'),
        server = require('/modules/server.js'),
        serv = server.server(),
        usr = carbon.server.tenantUser(username);
    if (!serv.authenticate(username, password)) {
        return false;
    }
    //load the tenant if it hasn't been loaded yet.
    if (!server.configs(usr.tenantId)) {
        event.emit('tenantCreate', usr.tenantId);
    }
    if (!server.configs(usr.tenantId)[USER_OPTIONS]) {
        event.emit('tenantLoad', usr.tenantId);
    }

    opts = options(usr.tenantId);
    //log.info(usr.tenantId);
    um = server.userManager(usr.tenantId);
    user = um.getUser(usr.username);
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
    event.emit('login', usr.tenantId, usr, session);
    //TODO: ??
    if (opts.login) {
        opts.login(user, password, session);
    }
    return true;
};

/**
 * Checks whether the logged in user has permission to the specified action.
 * @param user
 * @param permission
 * @param action
 * @return {*}
 */
var isAuthorized = function (user, permission, action) {
    var server = require('/modules/server.js'),
        um = server.userManager(user.tenantId);
    return um.getUser(user.username).isAuthorized(permission, action);
};

/**
 * Returns the user's registry space. This should be called once with the username,
 * then can be called without the username.
 * @param username ruchira
 * @return {*}
 */
var userSpace = function (username) {
    try {
        return session.get(USER_SPACE) || options().userSpace.store + '/' + username;
    } catch (e) {
        return null;
    }
};

/**
 * Get the registry instance belongs to logged in user.
 * @return {*}
 */
var userRegistry = function () {
    try {
        return session.get(USER_REGISTRY);
    } catch (e) {
        return null;
    }
};

/**
 * Logs out the currently logged in user.
 */
var logout = function () {
    var user = current(),
        opts = options(user.tenantId);
    if (opts.logout) {
        opts.logout(user, session);
    }
    event.emit('logout', user.tenantId, user, session);
};

/**
 * Checks whether the specified username already exists.
 * @param username ruchira@ruchira.com(multi-tenanted) or ruchira
 * @return {*}
 */
var userExists = function (username) {
    var server = require('/modules/server.js'),
        carbon = require('carbon'),
        usr = carbon.server.tenantUser(username);
    return server.userManager(usr.tenantId).userExists(usr.username);
};

var privateRole = function (username) {
    return USER_ROLE_PREFIX + username;
};

var register = function (username, password) {
    var user, role, id, perms, r, p,
        server = require('/modules/server.js'),
        carbon = require('carbon'),
        event = require('/modules/event.js'),
        usr = carbon.server.tenantUser(username),
        um = server.userManager(usr.tenantId),
        opts = options(usr.tenantId);
    um.addUser(usr.username, password, opts.userRoles);
    user = um.getUser(usr.username);
    role = privateRole(usr.username);
    id = userSpace(usr.username);
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
    event.emit('userRegister', usr.tenantId, user);
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
        carbon = require('carbon'),
        usr = carbon.server.tenantUser(username),
        opts = options(usr.tenantId),
        server = require('/modules/server.js'),
        event = require('/modules/event.js'),
        um = server.userManager(usr.tenantId);

    user = um.getUser(usr.username);
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
    event.emit('login', usr.tenantId, user);
    if (opts.login) {
        opts.login(user, "", session);
    }

    var permission = {};
    permission[userSpace(username)] = [
        carbon.registry.actions.GET,
        carbon.registry.actions.PUT,
        carbon.registry.actions.DELETE
    ];
    um.authorizeRole(privateRole(username), permission);

    return true;
};
