var meta={
	use:'action',
    purpose:'save',
    source:'custom',
	type:'form',
	applyTo:'*',
	required:['model','template'],
	name:'asset.lifecycle.action.save'
};

/*
 Description: Saves the lifeCycle field.
 Filename:asset.exporter.js
 Created Dated: 11/8/2013
 */

var module=function(){

    var configs=require('/config/publisher.json');
    var log=new Log();

	return{
		execute:function(context){

           log.info('Entered : '+meta.name);

           var model=context.model;
           var template=context.template;
           var type=template.shortName;

           log.info('Entered '+meta.name);
           log.info(stringify(context.actionMap));

           //Get the id of the model
           var id=model.getField('*.id').value;

           //Invoke an api call with the life cycle state
           var lifeCycle=model.getField('*.lifeCycle').value;

           var rxtManager=application.get(configs.app.RXT_MANAGER);

           var artifactManager=rxtManager.getArtifactManager(type);

           var asset=context.parent.export('asset.exporter');

           log.info('Attempting to attach the lifecycle :'+lifeCycle+'to asset with id: '+id);

           artifactManager.attachLifecycle(lifeCycle,asset);

           log.info('Finished attaching the lifecycle to the asset'+stringify(asset));
		}
	};
};

