/*
 Description: The class is used to create and manage modes from schemas
 Filename:model.creator.js
 Created Date:23/9/2013
 */

var modelManager = function () {

    var utility = require('/modules/utility.js').utility();

    var PROP_NAME = 'name';
    var PROP_SCHEMA = 'schema';

    function ModelManager(options) {
        this.driver = null;
        utility.config(options, this);
        this.managedModels = {};
    }

    /*
    The function loads the schemas
     */
    ModelManager.prototype.loadSchemas=function(){

    };


    /*
     The function creates a model from the provided schema and then registers it with the
     model manager.
     @schema: Describes the structure of the model
     */
    ModelManager.prototype.register = function (schema) {

        //Obtain the list of fields in the schema
        var fields = schema.fields || [];

        //Examine the fields in the schema
        var model = function () {

            this[PROP_NAME] = schema.name;
            this[PROP_SCHEMA] = schema;

            var that = this;
            //Create a property for each field
            utility.each(fields, function (field) {
                that[field.name] = 'empty';
            });
        };

        //Attach the functions

        //Add the model to the list of managed models
        this.managedModels[schema.name] = model;
    };


    /*
     The function creates and returns a new instance of the provided model
     @modelName: The name of the model
     @return: A model instance
     */
    ModelManager.prototype.get = function (modelName) {

        //Check if the model is managed
        if (this.managedModels.hasOwnProperty(modelName)) {
            return new this.managedModels[modelName]();
        }

        return null;
    };


    function attachDefaultOperations(model, modelManager) {

        //Find models matching the predicate
        model.prototype.find = function (predicate) {

            var query = this.driver.queryProvider.select(this.schema, predicate);
            var results = this.driver.query(query, this.schema, modelManager);
            return results;
        };

        //Obtain all of the models
        model.prototype.findAll = function () {
            var query = this.driver.queryProvider.selectAll(this.schema);
            var results = this.driver.query(query, this.schema, modelManager);
            return results;
        };

        //Creates a table in the database
        model.prototype.create=function(predicate){
            var query = this.driver.queryProvider.create(this.schema);
            var results=this.driver.query(query,this.schema);
            return results;
        };

        //Save the model details to the underlying database
        model.prototype.save=function(predicate){
            var query = this.driver.queryProvider.insert(this.schema,predicate);
            var results=this.driver.query(query,this.schema);
            return results;
        };
    }

    return{

        ModelManager: ModelManager

    };

};