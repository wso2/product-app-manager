var ext_domain=require('extension.domain.js').extension_domain();
var utility=require('/modules/utility.js').rxt_utility();

/*
Description: All operations on the rxt data are exposed through a proxy object
FileName:extension.management.js
Created Date: 8/8/2013
 */

var extension_management=function(){
	
	/*
	 * A proxy which interacts with the user
	 */
	function Model(options){
		this.dataModel=new ext_domain.DataModel();
		this.adapterManager=null;
		this.template=null;	//The template of the data stored in the model
		utility.config(options,this);
		this.init();
	}
	
	/*
	 * Preload the tables and fields in the template
	 * to the data model
	 */
	Model.prototype.init=function(){
		//Go through each table
		for each(var table in this.template.tables){
			
			//Go through each field
			for each(var field in table.fields){
				
				this.dataModel.setField(table.name+'.'+field.name,field.value);
			}
		}
	}

    /*
    Sets the field value
    @fieldName: The field  name in the form {table}.{field_name}
    @value: The value of the field
     */
	Model.prototype.set=function(fieldName,value){
		this.dataModel.setField(fieldName,value);
	}

    /*
    Gets the field value
    @fieldName: the field in the form {table_name}.{field_name}
    @return:If the field is present then a field object is returned ,else null
     */
	Model.prototype.get=function(fieldName){
		return this.dataModel.getField(fieldName);
	}

    /*
    Exports the model data to the provided type
    @type: The exporter type to use
    @returns: An object of the format outputed by the specified exporter
     */
	Model.prototype.export=function(type){
		var adapter=this.adapterManager.find(type);
		return adapter.execute({model:this.dataModel,template:this.template});
	}

    /*
    Imports the provided data into the model using the specified importer type
    @type: The type of importer to use
    @inputData: An object containing data that is to be imported into the model
     */
	Model.prototype.import=function(type,inputData){
		var adapter=this.adapterManager.find(type);
		var context=this.getContext();
		context['inputData']=inputData;
		adapter.execute(context);
	}

    Model.prototype.save=function(){
        //Obtain the actions

    }

    //TODO: Remove this method
	Model.prototype.getContext=function(){
		return {model:this.dataModel,template:this.template};
	}
	
	/*
	 * The class is used to create models based on
	 * predefined templates
	 */
	function ModelManager(options){
		this.parser=null;
		this.adapterManager=null;
		utility.config(options,this);
	}

	/*
	 Creates a model of the specified type
	 @type: The type of model to create
	 @return: A model of the provided type
	 */
	ModelManager.prototype.getModel=function(type){
		//Obtain the template of the model from the parser
		for each(var template in this.parser.templates){
			
			if(template.applyTo==type){
				return new Model({template:template,adapterManager:this.adapterManager});
			}
		}
		
		return null;
	}
	
	return{
		ModelManager:ModelManager
	}
	
};