/*
Description: Handles the management of folders (bundles) containing the details of assets.
Filename:  bundler.js
Created Date:13/8/2013
 */

var utility=require('/modules/utility.js').rxt_utility();

var bundle_logic=function(){
    var log=new Log();

    /*
    The supported extensions
     */
    var FILE_TYPES={
        "Image":"Image"
    };

    function Bundle(options){
        this.path='';
        this.name='';
        this.extension='';
        this.type='';
        this.isDirectory=false;
        this.instance=null;

        utility.config(options,this);

        //A resource can have many resources inside it
        this.children=[];
    }

    Bundle.prototype.add=function(resource){
        this.children.push(resource);
    };

    function BundleContainer(options){
        this.bundle=null;
        this.queryBundles=[];
        utility.config(options,this);

    }

    BundleContainer.prototype.hasChildren=function(){
        return (this.bundle.children.length>0);
    };

    BundleContainer.prototype.getName=function(){
        return this.bundle.name;
    };

    BundleContainer.prototype.isDirectory=function(){
        return this.bundle.isDirectory;
    };

    BundleContainer.prototype.getExtension=function(){
        return this.bundle.extension;
    }

    BundleContainer.prototype.getContents=function(){
        var content='';
        if(this.bundle.instance){
            this.bundle.instance.open('r');
            content=this.bundle.instance.readAll();
            this.bundle.instance.close();
            return content;
        }

        return '';
    }

    BundleContainer.prototype.toString=function(){
        return 'bundle enclosed: '+this.bundle.name+' children: '+this.bundle.children.length;
    }

    BundleContainer.prototype.get=function(predicate){
        var qContainer=new BundleContainer();

        log.debug('get called with : '+stringify(predicate));
        log.debug('current bundle: '+this.bundle.name);
        if(!this.bundle){
            log.debug('cannot get when there is no bundle present.');
            return qContainer;
        }

        var bundlesFound=[];
        recursiveFind(this.bundle,predicate,bundlesFound);

        qContainer=new BundleContainer({
            queryBundles:bundlesFound
        });
        log.debug('get has found: '+bundlesFound.length);
        return qContainer;
    };

    /*
    The function returns the first query result
     */
    BundleContainer.prototype.result=function(){
        if((this.queryBundles)&&(this.queryBundles.length>0)){
            return new BundleContainer({bundle:this.queryBundles[0]});
        }

        return null;
    }

    /*
    The method allows iteration over each child bundle or
    each query result
    @iterator: A function call back which is invoked for each bundle
     */
    BundleContainer.prototype.each=function(iterator){
        var tempBundle;

        //If the bundle container contains a reference to
        //a single bundle then iterate through the children
        if(this.bundle){
            log.debug('Iterating child bundles');

            for(var index in this.bundle.children){
                tempBundle=new BundleContainer({
                    bundle:this.bundle.children[index]
                });

                iterator(tempBundle);
            }
        }
        else{

            //Go through all the queried bundles
            for(var index in this.queryBundles){
                tempBundle=new BundleContainer({
                    bundle:this.queryBundles[index]
                });

                iterator(tempBundle);
            }
        }

    };

    BundleContainer.prototype.first=function(iterator){
        log.debug('queried bundles '+this.queryBundles.length);
        var queryBundleCount=(this.queryBundles)?this.queryBundles.length:0;

        //Check if there are any queryBundles
        if(queryBundleCount!=0){

            var temp=new BundleContainer({
                bundle:this.queryBundles[0]});
           return iterator(temp);
        }
    };

    function BundleManager(options){
         this.rootBundle=null;
         this.path='';
         utility.config(options,this);

         if(this.path!=''){
             this.rootBundle=recursiveBuild(new File(this.path));
         }
    }

    BundleManager.prototype.getRoot=function(){
        return new BundleContainer({bundle:this.rootBundle});
    };

    /*
    Allows a particular bundle to be queried.Only the first matching
    result is returned.
    @predicate: The predicate used in finding a match
    @return: A BundleContainer object containing the results of the query
     */
    BundleManager.prototype.get=function(predicate){
        var result=[];
        var bundleContainer=new BundleContainer();

        //Locate the matching bundle using the predicate
        recursiveFind(this.rootBundle,predicate,result);


        //Get the first result
        if(result.length>0){
            bundleContainer=new BundleContainer({
                bundle:result[0]
            });
            return bundleContainer;
        }

        log.debug('A matching bundle was not found for query: '+stringify(predicate));

        return bundleContainer;
    };

    /*
     The function finds a resource based on the provided criteria
     @root: The root to be searched
     */
    function recursiveFind(root,predicate,found){

        //Check if the root is a leaf
        if(root.children.length==0){

            //Check if the current root is a match
            if(utility.isEqual(predicate,root)){
                log.debug('Found a match as  leaf: '+root.name);
                return root;
            }

            return null;
        }
        else{

            //Check if the directory will be a match
            if(utility.isEqual(predicate,root)){
                log.debug('Found a match as a root: '+root.name);
                return root;
            }

            var foundResource;
            var currentResource;

            //Go through each resource in the sub resources
            for(var index in root.children){

                currentResource=root.children[index];

                foundResource=recursiveFind(currentResource,predicate,found);

                //Check if a resource was found.
                if(foundResource){
                    log.debug('adding bundle: '+foundResource.name);
                    found.push(foundResource);
                }
            }

            //return found;
        }
    }

    /*
    The function recursively builds the bundle structure based on a given file location
    @file: The root location of the bundles

     */
    function recursiveBuild(file){

        //Check if it is a directory in order to identify whether it is a child
        if(!file.isDirectory()){

            var resource =new Bundle({
                name:file.getName(),
                extension:utility.fileio.getExtension(file),
                instance:file
            });

            log.debug(file.getName()+' not a directory ');

            return resource;
        }
        else{

            log.debug(file.getName()+' will be a root bundle.');

            //Create a resource of root type
            var dir=new Bundle({
                isDirectory:true,
                name:file.getName(),
                instance:file
            });

            //Obtain the sub resources within the given directory
            var resources=file.listFiles();

            log.debug('resources found: '+resources.length);

            //Go through each file
            for(var index in resources){

                var current=recursiveBuild(resources[index],dir);
                log.debug('adding: '+current.name+' as a child resource of '+dir.name);

                dir.add(current);
            }
            return dir;
        }
    }

    return{
        build:recursiveBuild,
        find:recursiveFind,
        Bundle:Bundle,
        BundleManager:BundleManager
    }
};



