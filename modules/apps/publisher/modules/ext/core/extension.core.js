/*
Description: Core functionality of the extension mechanism
Filename:extension.core.js
Created:7/8/2013
 */

var utility = require('/modules/utility.js').rxt_utility();

var extension_core = function () {

    function AdapterManager(options) {
        this.parser = {};
        this.adapters = [];
        utility.config(options, this);
    }

    /*
     * Initializes the adapter by loading up all of the templates
     */
    AdapterManager.prototype.init = function () {

        // Go through all of the imports and load all of the scripts
        for each(var template in this.parser.templates)
        {

            // Only process if there are imports
            if (template.import) {

                // Go through each import
                for each(var item
            in
                template.import
            )
                {

                    var instance = require(item);

                    this.adapters.push(new AdapterContainer(instance));
                }
            }
        }


    }

    function ActionManager(options){
        this.templates=[];
        this.actionContainers=[];
        utility.config(options,this);
    }

    /*
    The function goes through each template and creates an action container
    for each template
     */
    ActionManager.prototype.init=function(){
        var container=null;
        for each (var template in this.templates){

             container=new ActionContainer({template:template});
             container.init();

            this.actionContainers.push(container);
        }
    }

    /*
    Obtains the required action for the template
     */
    ActionManager.prototype.getAction=function(templateName,actionName){
        var action=null;

        //Find the appropriate action container
        var container=this.findContainer(templateName);

        if(!container){

            return null;
        }

        //Obtain the appropriate action
        //TODO: Remove this switch -let the container handle it
        switch(actionName){
          case 'save':
               action=container.saveMap;
               break;
          default:
               break;
         }

        return action;
    }

    /*
    The function locates a particular action container
     */
    ActionManager.prototype.findContainer=function(templateName){

        for each(var container in this.actionContainers){
                  if(container.template.name==templateName){
                      return container;
                  }
        }

        return null;
    }

    /*
    Stores the mapping of the actions
     */
    function ActionContainer(options){
        this.template=null;
        this.saveMap=null;

        utility.config(options,this);
    }

    ActionContainer.prototype.init=function(){
        this.saveMap=this.createActionMap('save',this.template);
    }


    /*
     Examines the template and creates a mapping of the actions based on meta fields
     */
    ActionContainer.prototype.createActionMap=function(action,template){

        actionMap={};
        var log=new Log();
        //Go through each field in the template and identify default save actions
        for each(var table in template.tables){

            //Go through each field
            for each(var field in table.fields){
               // log.info(template.name+' ' +table.name+' '+stringify(field));

                //Check if the field has meta properties
                if(field.meta){



                    //Locate the save property
                    var actionInstance=field.meta[action];

                    if(actionInstance){
                         log.info('save action');

                        //Create a property if it is not already present
                        if(!actionMap.hasOwnProperty(action)){
                            log.info('inserted new array');
                            actionMap[actionInstance]=[];//Create an array to store the field
                        }

                        //Save to the map
                        actionMap[actionInstance].push(field);
                    }
                }
            }
        }
        log.info(stringify(actionMap));
        return actionMap;
    }


    /*
     Locates an adapter matching the required type @type: The type of the
      adapter
     @return: The adapter
     */
    AdapterManager.prototype.find = function (type) {

        for each(var adapter in this.adapters)
        {
            //print(adapter.meta.type);
            if (adapter.meta.type == type) {
                return adapter;
            }
        }
        return null;
    }

    /*
    The class is used to store the instance of the adapter along with its meta data
    @instance: An instance of the external script object module.
     */
    function AdapterContainer(instance) {
        this.meta = instance.meta;
        this.instance = instance.module();
    }

    /*
      Executes the logic of the adapter @context: The context within which the
      adapter must be executed
      @context: The set of data within which the adapter must be executed
     */
    AdapterContainer.prototype.execute = function (context) {
        return this.instance.execute(context);
    }

    /*
    The class is used to manage fields
     */
    function FieldManager(options) {
        this.parser = null;
        utility.config(options, this);
    }

    /*
     The function processes all templates in the parser
     */
    FieldManager.prototype.init = function () {
        //Go through all of the templates
        for each(var template in this.parser.templates)
        {
            this.process(template);
        }
    }

    /*
      The function goes through each field in the provided template injects any
      fieldProperties
     */
    FieldManager.prototype.process = function (template) {

        // Go through each field properties
        for each(var fieldProperty in template.fieldProperties)
        {

            if (fieldProperty.field == '*') {
                //All the fields in all of the tables
                for each(var table in template.tables)
                {
                    addProperty(table, fieldProperty);
                }
            }
            else {
                var scope = fieldProperty.field.split('.');
                var table = scope[0];
                var field = scope[1];

                if (field == '*') {
                    //Find the specified table
                    var tableInstance = this.parser.getTable(table, template);

                    addProperty(tableInstance, fieldProperty);
                }
                else {
                    var fieldInstance = this.parser.getFieldFromTable(field, table, template);

                    addPropertyToField(fieldInstance, fieldProperty);
                }

            }
        }
    }


    /*
      Adds the provided property to the table
     */
    function addProperty(table, property) {
        for each(var field in table.fields)
        {
            addPropertyToField(field, property);
        }
    }

    /*
      Adds the provided property to the field
     */
    function addPropertyToField(field, property) {

        if (!field.meta) {
            field.meta = {};
        }

        field.meta[property.name] = property.value;
    }

    return{
        AdapterManager: AdapterManager,
        ActionManager:ActionManager,
        FieldManager: FieldManager
    }
}