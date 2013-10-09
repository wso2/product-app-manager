/*
Description: The validation manager is able to run some set of logic on a provided context
Filename: validation.manager.js
Created Date: 8/10/2013
 */

var validationManagement=function(){

    var log=new Log('validation.manager');
    var bundler=require('/modules/bundler.js').bundle_logic();

    var VALIDATOR_PATH='/modules/ext/scripts/validations';

    var VALIDATION_TYPES={
        MANDATORY_CHECK:'MANDATORY_CHECK',
        READ_ONLY_CHECK:'READ_ONLY'
    };

    function ValidationManager(){
       this.validatorScripts=[];
    }

    ValidationManager.prototype.init=function(){

        var bundleManager=new bundler.BundleManager({path:VALIDATOR_PATH});

        var root=bundleManager.getRoot();

        root.each(function(bundle){

            //Load the script
            var file=require(VALIDATOR_PATH+bundle.getName()).validatorModule();

            this.validatorScripts.push(file);

        });
    };

    /*
    The function runs all loaded validators
     */
    ValidationManager.prototype.validate=function(context){
        var validatorScript;
        var kill=false;

        context['report']={};
        context['report']['failedFields']=[];

        for(var index in this.validatorScripts){

            validatorScript=this.validatorScripts[index];

            //Check if the current validator script can be run on the current context
            if(validatorScript.isApplicable(context)){

                kill=validatorScript.execute(context);

                //Checks if the other validations should be skipped
                if(kill){
                    return context;
                }

            }

        }

        return context;
    };


}
