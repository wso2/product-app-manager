var queryTranslator=function(){

    /*
    The function uses the schema and the modeManager to convert the results into
    model objects
     */
    function translate(schema,modelManager,results){
        var result;
        var field;
        var models=[];
        var model;
        for(var index in results){

            result=results[index];
            model=modelManager.get(schema.name);

            for(var prop in result){

                for(var key in schema.fields){

                    field=schema.fields[key];


                    if(field.name.toUpperCase()==prop) {

                         model[field.name]=result[prop];
                    }
                }
            }

            models.push(model);

        }

        return models;
    }

   return {
       translate:translate
   }
};
