/*
The following class is used to validate all mandatory fields
 */
var validatorModule=function(){

    /*
    The function is used to check if the context can be handled by the
    validator
    @return: True if the validator is handled,else false
     */
    function isApplicable(context){
        var model=context.model;
        var template=context.template;

        //Check if the template and model are given
        if((template)&&(model)){
           return true;
        }

        return false;
    }

    /*
    The function is used to check for mandatory fields and check if they have been filled
     */
    function execute(context){

        var template=context.template;
        var model=context.model;

        //Go through each field and check if it is mandatory
        for(var index in template){

        }


        return true;
    }

    return{
        isApplicable:isApplicable,
        execute:execute
    }
}
