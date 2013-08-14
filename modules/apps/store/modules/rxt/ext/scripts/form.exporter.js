var meta={
	use:'export',
	type:'form',
	required:['model','template']
};

var module=function(){
	
	
	/*
	 * Go through each table and extract field data
	 */
	function fillFields(table,fieldArray,template){
		
		//Go through each field
		for (var field in table.fields){
			
			//Obtain the field details from the template
			var fieldTemplate=template.getField(table.name,field.name);
			
			//We ignore the field if it is not in the template
			if(!fieldTemplate){
				return;
			}
			var data={};
			
			data['name']=table.name.toLowerCase()+'_'+field.name.toLowerCase();
			data['label']=(fieldTemplate.label)?fieldTemplate.label:field.name;
			data['isRequired']=(fieldTemplate.required)?true:false;
			data['isTextBox']=(fieldTemplate.type=='text')?true:false;
			data['isOptions']=(fieldTemplate.type=='options')?true:false;
			data['value']=field.value;
			
			data['valueList']=csvToArray(fieldTemplate.value||'');
			
			fieldArray.push(data);
		}
		
		//print('<br/>'+stringify(fieldArray)+'<br/>');
		
		return fieldArray;
	}
	
	/*
	 * Fills all of the tables except the *(global)
	 */
	function fillTables(model,template){
		
		var fieldArray=[];

        print(model);
		//Go through each table in the model
		for (var table in model.dataTables){
			
			//Ignore if *
			if(table.name!='*'){
				//print('filled '+table.name);
				fillFields(table,fieldArray,template);
			}
		}
		
		//print(fieldArray);
		
		return fieldArray;
	}
	
	function csvToArray(str){
		var array=str.split(',');
		return array;
	}
	
	function fillMeta(model,template){
		var meta={};
		meta['shortName']=template.shortName;
		meta['singularLabel']=template.singularLabel;
		meta['pluralLabel']=template.pluralLabel;
		return meta;
	}
	
	function fillInfo(model){
		var info={};
		
		var field=model.getField('*.id');
		info['id']=field?field.getValue():'';
		
		field=model.getField('*.lifecycle');
		info['lifecycle']=field?field.getValue():'';
		
		field=model.getField('*.lifecycleState');
		//info['lifecycleState']=model.getField('*.lifecycleState').getValue()||'';
		info['lifecycleState']=field?field.getValue():'';
		
		return info;
	}
	
	return{
		execute:function(context){
            var model=context.model;
			var template=context.template;
			
			var struct={};
			
			//var formTemplate={};
			//var formMetaData={};
			//var formAssetData={};
			//var formTemplateFields=[];
			//print(model);
			//TODO: Move this check outside
			if((!model)||(!template)){
				throw 'Required model and template data not present';
			}

            //print('nope!');
			var tables= fillTables(model,template);
			
			struct['fields']=tables;
			struct['meta']=fillMeta(model,template);
			struct['info']=fillInfo(model);
			
			//print(tables);
			
			return struct;
			
			/*formMetaData['shortName']=template.shortName;
			formMetaData['singularName']=template.singularName;
			formMetaData['pluralName']=template.pluralName;
			
			formAssetData['version']=1.0;
			formAssetData['lifecycle']='';
			formAssetData['lifecycleState']='';
			
			formTemplate['meta']=formMetaData;
			formTemplate['asset']=formAssetData;
			formTemplate['fields']=formTemplateFields;
			
			return formTemplate;*/
			
		}
	}
};
