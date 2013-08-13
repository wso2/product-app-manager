var meta={
	use:'action',
    purpose:'save',
	type:'form',
    source:'default',
	applyTo:'*',
	required:['model','template'],
	name:'asset.action.save'
};

/*
Description:Saves the contents of the model to an artifact instance and then retrieves the
            id
Filename: asset.action.save.js
Created Date: 8/8/2013
 */


var module=function(){

    var configs=require('/config/publisher.json');
    var log=new Log();

	return{
		execute:function(context){

            log.info('Entered : '+meta.name);

            log.info(stringify(context.actionMap));

            var model=context.model;
            var template=context.template;

            var name=model.getField('overview.name').value;
            var shortName=template.shortName;

            log.info('Artifact name: '+name);

            log.info('Converting model to an artifact for use with an artifact manager');

            //Export the model to an asset
            var asset=context.parent.export('asset.exporter');

            log.info('Finished exporting model to an artifact');

            //Save the artifact
            log.info('Saving artifact with name :'+name);


            //Get the artifact using the name
            var rxtManager=application.get(configs.app.RXT_MANAGER);


            var artifactManager=rxtManager.getArtifactManager(shortName);

            artifactManager.add(asset);

            //name='test-gadget-7';

            log.info('Finished saving asset : '+name);

            var artifact=artifactManager.find(function(adapter){
               return (adapter.attributes.overview_name==name)?true:false;
            },1);

            log.info('Locating saved asset: '+stringify(artifact)+' to get the asset id.');

            var id=artifact[0].id||' ';

            log.info('Setting id of model to '+id);

            //Save the id data to the model
            model.setField('*.id',id);

            log.info('Finished saving asset with id: '+id);
		}
	}
};
