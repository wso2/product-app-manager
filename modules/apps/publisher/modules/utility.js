
	/*
	Description: The file houses the utility logic
	Filename:utility.js
	Created Date: 28/7/2013
	*/
var rxt_utility=function(){
	return{
		/*The function takes a set of options 
		and configures a target object
		@options: A set of options to configure the target
		@targ: The object to be configured
		*/
		config:function(options,targ){

			
			//Avoid if no options given or the
			//target is undefined
			if((!options)||(!targ)){	
				return targ;
			}

			//Go through each field in the target object
			for(var key in targ){
				
				//Avoid processing functions
				if(typeof targ[key]!='function'){

					var value=options[key];

					//If a value is present
					//do the configuration
					if(value){
					   targ[key]=value;
					}
				}
			}
		},

		findInArray:function(array,fn){
                               for each(var item in array){
                                    if(fn(item)){
                                        return item;
                                    }
                               }

                               return null;
        },
        /*
        The function iterates through each element in the array
        @array: The array to be iterated
        @fn: A function which will recieve each item in the array
         */
        each:function(array,fn){
            for(var index in array){
                fn(array[index],index);
            }
        },
        /*
        The function locates an item in the provided array
        based on the predicate function
        @array: The array to be searched
        @fn: A predicate function which returns when there is a match,else false
         */
        find:function(array,fn){
            for(var index in array){
                if(fn(array[index])){
                    return array[index];
                }
            }

            return null;
        },

        /*
        The function copies the properties defined in the array from object A to B
        @objectA: The object to be targeted
        @objectB: The object which will recieve the values of A
        @propArray: An array of properties which occur in objectA

         */
        copyProperties:function(objectA,objectB,propArray){
              for(var index in propArray){
                  var prop=propArray[index];
                  objectB[prop]=objectA[prop];
              }
        },

        /*
        The function converts an array into a csv list
        @array: An array of values
        @returns: A CSV string of the array
         */
        createCSVString:function(array){

            //Send it back without processing if it is not an array
            if(!(array instanceof Array)){
                return array;
            }

            var csv='';
            var count=0;
            var item=null;

            //Go through each element in the array
            for (var index in array){
                item=array[index];
                if(count>0){
                    csv+=','
                }
                csv+=item;
                count++;

            }

            return csv;
        },



        /*
        File related utility functions
         */
        fileio:{
            /*
            The function returns all files with a given extension
            in the provided path.
            NOTE: Hidden and temporary files are ignored
            @path: The path of the directory
            @returns: An array of files in the given path is returned
             */
            getFiles:function(path,extension){
                //Replace . if the user sends it with the extension
                extension=extension.replace('.','');

                var dir=new File(path);
                var files=[];
                if(dir.isDirectory){

                      var list=dir.listFiles();

                      for(var index in list){

                         var item =list[index];

                         /*TODO: This will not process files with the extension fragment e.g. extension json
                         filename: tjson.js .In such a case the file will be processed as having a
                         a json extension.
                         */

                         //Extract the extension
                         var fileName=item.getName().split('.');
                         //The extension will always be the last element of a file name when it is split by .
                         var foundExt=fileName[fileName.length-1];


                         if((item.getName().indexOf('~')==-1)&&(foundExt==extension)){
                             files.push(item);
                         }
                      }
                }

                return files;
            }
        }
	}
}
