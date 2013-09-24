/*
 Description: The class is used to create and manage modes from schemas
 Filename:model.creator.js
 Created Date:23/9/2013
 */

var modelManager = function () {

    var log=new Log('model.manager');
    var utility = require('/modules/utility.js').utility();
    var bundler=require('/modules/bundler.js').bundle_logic();


    var PROP_NAME = 'name';
    var PROP_SCHEMA = 'schema';

    var schemaPath='/schemas/';

    function ModelManager(options) {
        this.driver = null;
        utility.config(options, this);
        this.managedModels = {};

        this.loadSchemas();
    }

    /*
    The function loads the schemas
     */
    ModelManager.prototype.loadSchemas=function(){
        var bundleManager=new bundler.BundleManager({
            path:schemaPath
        });

        //Obtain the root bundle
        var root=bundleManager.getRoot();

        var that=this;

        root.each(function(bundle){
            var schema=require(schemaPath+bundle.getName()).schema();
            log.info('registering schema : ['+schema.name+']');
            that.register(schema);
            log.info('finished registering schema: ['+schema.name+']');
        });
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
        attachDefaultOperations(model,this);

        var temp=new model();

        //Check if the table exists before creating
        if(!temp.checkIfTableExists()){
            log.info('table: '+temp.schema.table+' does not exist.');
            //Create the table
            temp.createTable();
        }

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

            log.info('find called.');

            //var query = this.driver.queryProvider.select(this.schema, predicate);
            //var results = this.driver.query(query, this.schema, modelManager);
            //return results;
        };

        //Obtain all of the models
        model.prototype.findAll = function () {
            var query = this.driver.queryProvider.selectAll(this.schema);
            var results = this.driver.query(query, this.schema, modelManager);
            return results;
        };

        //Creates a table in the database
        model.prototype.createTable=function(){
            log.info('creating table: ['+this.schema.table+']');
            var query = modelManager.driver.queryProvider.create(this.schema);
            log.info('query: '+query);
            var results=modelManager.driver.query(query,this.schema);
            return results;
        };

        //Checks whether the table already exists
        model.prototype.checkIfTableExists=function(){
            log.info('checking if table: ['+this.schema.table+'] exists.');
            var query=modelManager.driver.queryProvider.checkIfTableExists(this.schema);

            var results=modelManager.driver.query(query,this.schema);

            //if the number of results is more than 0 then a table exists
            if(results.length>0){
                return true;
            }
            return false;
        };

        //Save the model details to the underlying database
        model.prototype.save=function(){
            log.info('save called');

            var query = modelManager.driver.queryProvider.insert(this.schema,this);
            log.info('query: '+query);
            var results=modelManager.driver.query(query,this.schema);
            return results;
        };
    }

    return{

        ModelManager: ModelManager

    };

};