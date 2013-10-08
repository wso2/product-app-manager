/*
 Description: The class is used to filter assets based on the permissions specified in the extension json files
 Filename: permission.asset.filter.js
 Created Date: 7/10/2013
 */

var filterModule = function () {

    var log=new Log('permission.asset.filter');
    var utility=require('/modules/utility.js').rxt_utility();

    /*
     The function filters the provided assets list based on the permissions defined in the extension file
     */
    function execute(context) {
        var data = context['data'];
        var userRoles = context['roles'];
        var item;
        var items=[];

        //Go through each data item
        for (var index in data) {

            item = data[index];

            //Obtain the permissions for the current lifecycle state
            permissableRoles=obtainPermissableRoles(context,item.lifecycleState);

            //Fill in dynamic values
            permissableRoles=fillDynamicPermissableRoles(permissableRoles);

            //Check if the user has any of the roles specified for the state
            var commonRoles=utility.intersect(userRoles,permissableRoles,function(a,b){
                return (a==b);
            });

            if(commonRoleslength>0){
               items.push(item);
            }
        }

        context['data']=items;

        return true;

    }

    /*
    The function is used to obtain permissible roles based on the provided state
    @context: A context containing the permissions configuration block
    @state: The state of the current asset
    @return: An array of permissible roles
     */
    function obtainPermissibleRoles(context,state){

    }

    /*
    The function is used to fill in dynamic values of permissions
     */
    function fillDynamicPermissibleRoles(context,permissions){
        var list=[];
        for(var index in permissions){
            list.push(permissions.replace('{overview_provider}',context.username));
        }

        return list;
    }

    return{
        execute: execute
    }
}