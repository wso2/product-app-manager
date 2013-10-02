/*
 Description: The class is used to secure the storage by checking if a user
 has access rights to a given
 Created Date: 2/10/2013
 Filename: storage.security.provider.js
 */

var securityModule = function () {

    var log = new Log('storage.security.provider');
    var utility = require('/modules/utility.js').rxt_utility();
    var bundler = require('/modules/bundler.js').bundle_logic();

    var STORAGE_BLOCK = 'storage';
    var LIFECYCLE_BLOCK = 'lifecycle';
    var CONFIG_FORMAT = 'json';
    var ROLE_ADMIN='admin';

    function SecurityProvider(context) {
        this.storageBlocks = {};
        this.context = context;
        log.info(this.context);
        this.bundleManager = new bundler.BundleManager({path: context.path});
    }

    /*
     The function is used to read all of the extension files and obtain the storage block
     if one is present
     */
    SecurityProvider.prototype.init = function () {

        var root = this.bundleManager.getRoot();
        var configFile;
        var that = this;

        root.each(function (bundle) {

            //We only look for json configuration files
            if (bundle.getExtension() == CONFIG_FORMAT) {

                //Obtain the configuration file
                configFile = require(that.context.path + '/' + bundle.getName());

                //Check if a storage block is specified
                if (configFile.hasOwnProperty(STORAGE_BLOCK)) {

                    var name = bundle.getName().replace('.' + bundle.getExtension(), '');

                    that.storageBlocks[name] = configFile.storage;
                }
            }
        });

        log.info(this.storageBlocks);
    };

    /*
     The function is used to check if a user is able to access a provided asset
     @asset: The asset which will be given access permissions
     @user: The username to which rights must be given
     @return: True if the user is allowed access,else false
     */
    SecurityProvider.prototype.isAllowed = function (asset, user, roles, state, field) {
        var isUserAllowed = false;
        var state = state.toLowerCase();

        var type = asset.type || '';


        //Check if a storage block exists for the provided asset, if it does
        //not then it is allowed access by default
        if (!this.storageBlocks.hasOwnProperty(type)) {
            log.info('storage block does not exist');
            return true;
        }

        //Check if the field is supported
        if (!this.storageBlocks[type].hasOwnProperty(field)) {
            log.info('field ' + field + ' has no storage rules');
            return true;
        }

        //Check if lifecycle constrictions have been provided
        if (!this.storageBlocks[type][field].hasOwnProperty(LIFECYCLE_BLOCK)) {
            log.info('field ' + field + ' does not have a ' + LIFECYCLE_BLOCK);
            return true;
        }

        //Obtain the lifecycle information
        var lifecycleData = this.storageBlocks[type][field][LIFECYCLE_BLOCK];

        //Determine if the current state is handled
        if (!lifecycleData.hasOwnProperty(state)) {
            log.info('field ' + field + ' does not have any rules for state: ' + state);
            return true;
        }

        var stateData = this.storageBlocks[type][field][LIFECYCLE_BLOCK][state];

        //Add the admin role if state roles do not have it
        if(stateData.indexOf(ROLE_ADMIN)==-1){
            stateData.push(ROLE_ADMIN);
        }



        //Fill in dynamic values
        stateData=dataInjectToRoles(stateData,asset);


        var commonRoles = utility.intersect(stateData, roles, function (a, b) {
            return (a == b);
        });



        //Determine if the role occurs in the stateData,if so then allow the user access
        if (commonRoles.length > 0) {

            return true;
        }
        return isUserAllowed;
    };

    SecurityProvider.prototype.execute=function(){

    };

    /*
     The function injects data to the provided roles using content in the
     asset
     */
    function dataInjectToRoles(roles, asset) {
        var roleList = [];
        var changedRole;

        //Go through each role
        utility.each(roles, function (role) {

            changedRole = dataInjectToRole(role, asset);
            roleList.push(changedRole);

        });

        return roleList;
    }

    /*
     The function injects data in the asset to the role string
     */
    function dataInjectToRole(role, asset) {
        role = inject(role, asset);
        role = inject(role, asset.attributes);
        return role;
    }

    /*
     The function checks if the properties of an object can
     be injected into a string
     @string: A string to be injected with data
     @object: The object with properties to be injected
     */
    function inject(string, object) {

        //Go through all properties of an object
        utility.each(object, function (value, key) {
            string = string.replace('{' + key + '}', value);
        });

        return string;
    }

    /*
    The funciton locates the field containing the provided UUID
    @uuid: The uuid of a stored resource
    @asset: An asset instance
    @return: The field name if it is found,else null
     */
    function findField(uuid,asset){

        var attributes=asset.attributes||{};

        for(var field in attributes){
            if(attributes[field]==uuid){
                return field;
            }
        }

        return null;
    }


    return{
        SecurityProvider: SecurityProvider,
        findField:findField
    }

};
