/*
Descripiton:The filter manager is used to filter assets before been presented to the user
Filename: filter.manager.js
Created Date: 7/10/2013
 */

var filterManagement=function(){

    var log=new Log('filter.manager');
    var bundler=require('/modules/bundler.js').bundle_logic();
    var config=require('/modules/config/publisher-tenant.json');
    var FILTER_PATH='/modules/filters';
    var EXT_PATH=config.paths.RXT_EXTENSION_PATH;
    var APP_FM='filter.manager';
    var LOGGED_IN_USER='LOGGED_IN_USER';

    function FilterManager(){
       this.filters=[];
       this.um=null;
       this.bundleManager=new bundler.BundleManager({path:FILTER_PATH});
    }

    /*
    The function checks the filters directory for any filters and loads them to memory
     */
    FilterManager.prototype.init=function(){

        var root=this.bundleManager.getRoot();

        //Go through each bundle
        root.each(function(bundle){

            var file=require(FILTER_PATH+'/'+bundle.getName()).filterModule();

            //Load up the filter
            this.filters.push(file);
        });

    };

    FilterManager.prototype.readConfig=function(){
        var configBundleManager=new bundler.BundleManager({path:EXT_PATH});

        var root=configBundleManager.getRoot();

        root.each(function(bundle){

            if(bundle.getExtension()=='json') {


            }
        })
    }

    /*
    The function applies a set of filters on the provided data.The data can either be a
    an object or an array of objects
    @data: The data to be filtered
    @return: A filtered array of data
     */
    FilterManager.prototype.filter=function(data){

        var filter;
        var context={};
        var isContinued=true;
        var username=session.get(LOGGED_IN);
        var user=this.um.getUser(username);

        context['data']=getData(data);
        context['roles']=user.getRoles();
        context['username']=username;

        //Go through all of the filters
        for(var index in this.filters){

             filter=this.filters[index];

             isContinued=filter.execute(context);

             if(!isContinued){
                 log.debug('stopping execution of filters.');
                 return;
             }

        }

        return context['data']||[];
    };


    FilterManager.prototype.setContext=function(um){
        this.um=um;
    }

    /*
    The function is used to convert a single object into an array
    @data: An array or an object
    @return: An array of data
     */
    function getData(data){

        var list=[];

        if(data instanceof Object){
            list.push(data);
        }else{
            list=data;
        }

        return list;
    }

    function cached(){
        //var instance=application.get
    }

    return{
        FilterManager:FilterManager
    }
}
