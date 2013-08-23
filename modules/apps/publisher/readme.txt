
Description
-----------

Publisher application for UES.

It allows the following functionality;
	- Login has been integrated
	- View assets
	- Add a new asset (gadgets and sites)
	- * An asset when it is created is attached to a default life cycle,and moved to the first life cycle state
	- Delete an existing asset (gadgets and sites) [Feature is present via API]

Changes:
----------

23/8/2013	Major Changes:
		--------------
		Note: An asset in order to be visible in the store must have its state set to PUBLISHED when it is created
		Changes to the extension mechanism can be found in /ext2/ folder of modules and config
		IMPORTANT: Requires the changes to artfiact.js in SAM PULL #2 JAG
		

13/8/2013	Major Changes:
		--------------	
		Replaced the api code with the model approach used by the site controllers
		Assets are now auto attached to a sample lifecycle when they are created 
		Assets are automatically promoted to the first stage of a lifecycle when created
		API and site controllers now return identical output where appropriate;
			GET /asset/type/{id} and GET /asset/type
		A model approach to handling rxts:
			var model=modelManager.getModel('gadget');
		Supported operations:
			model.import(<name of importer>, <input data>);
			var output=model.export(<name of exporter>);
			model.save();	//Invokes save actions defined on a field by field basis

		Minor Changes:
		--------------
		Changed the structure of this document to better reflect the state of the app!
		Added log info and debug statements where appropriate
		Removed unused code
		Added comments where appropriate , all files now have a description
			
		
Additions:
----------
23/8/2013	
		API: (Refer to API for complete changes)
		Lifecycle API
		-------------
			/publisher/api/lifecycle/checkList/{asset-type}/{asset-id} : Returns the check list for current lifecycle state
			/publisher/api/lifecycle/{ACTION}/{asset-type}/{asset-id}  : Performs the ACTION -(Promote/Demote)

			
13/8/2013	/tests/jmeter: 
			publisher_test_plan.jmx	Simple JMeter test plan for the site and API ( I am still wondering how I can use it to test the site)
		modules/ext/scripts: 
			asset.exporter.js		Exports model data to an asset format that can be used with the ArtifactManager
			form.importer.js		Imports asset data from a post array
			asset.action.save.js		Saves an asset using the ArtifactManager
			asset.lifecycle.action.save.js	Attaches a lifecycle to an asset using the ArtifactManager
			

Bugs:
-----
x		The created assets can only be viewed by a logged in user

13/8/2013	Asset types other than sites are classed as sites (e.g. Books)		
		Lifecycle states cannot be promoted or demoted
		Lifecycles cannot be detached

TODO:
-----
		Add update support for assets
		Change the rxt extension configuration file to be more user friendly
		Change the meta variable that needs to be defined for each external adpater
			
Note:
-----

-The publisher does not allow update operations
-Please copy and paste the lifecycle found in the registry.xml of the DEPENDENCIES folder . 


API
---

The following API calls have been implemented

	Noun	Url					Description

	GET  	/publisher/api/asset/{type}			Returns a template JSON object describing the structure of an asset
	GET  	/publisher/api/asset/{type}/{id}		Returns an asset of the given {type} and matching the {id}
	POST 	/publisher/api/asset/{type}			Creates a new asset of the given {type}
	DELETE 	/publisher/api/asset/{type}/{id}		Deletes an asset 
	POST 	/publisher/api/lifecycle/{type}/{id}		Attaches a lifecycle for the specified asset type with the id
	POST  api/lifecycle/{asset-type}/{artifact-id}  	Attach the provided lifecycle to the artifact
 	DELETE api/lifecycle/{asset-type}/{artifact-d}		Detaches the current lifecycle from the artifact
 	PUT  api/lifecycle/{state}/{asset-type}/{artifact-id}	Promotes an artifact to the lifecycle state provided
 	GET api/lifecycle/{asset-type}/{artifact-id}           	Returns the current lifecycle state of an artifact
 	GET api/lifecycle/checklist/{type}/{id}                 Returns the check list for an artifact
	
	


	


