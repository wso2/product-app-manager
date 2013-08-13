var meta={
    use:'import',
    type:'form.importer',
    required:['model','template','inputData']
};

/*
 Description: Reads data from an array obtained from a POST request  and stores it in the model
 Filename:form.importer.js
 Created Dated: 11/8/2013
 */
var module=function(){

    var log=new Log();

    function fillFields(model,data){

        //Go through each key
        for(var key in data){

            //break up the key
            var field=key.replace('_','.');
            log.info('Saving field: '+field);

            model.setField(field,data[key]);
        }
    }


    return{
          execute:function(context){

                 log.info('Entered :'+meta.type);
                 var data=context.inputData;
                 var model=context.model;
                 var template=context.template;

                 log.info('Attempting import data from'+stringify(data));

                 fillFields(model,data);

                 log.info('Finished importing data from form');
                 log.info('Exited : '+meta.type);
          }
    }
};