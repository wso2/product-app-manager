/*
 Description: The file houses the utility logic
 Filename:utility.js
 Created Date: 28/7/2013
 */
var rxt_utility = function () {

    /*
     The method is used to create a JSON object using
     an xml object.
     @xmlElement: An xml element object to be processed
     @return: A pseudo object containing the properties of the
     xml element.
     */
    function createJSONObject(xmlElement) {

        var pseudo = {};

        //Extract all attributes
        var attributes = xmlElement.@*;

        //Fill the pseudo object with the attributes of the element
        for (var attributeKey in attributes) {
            var attribute = attributes[attributeKey];
            pseudo[attribute.localName()] = attribute.toString();
        }

        return pseudo;
    }


    /*
     The function converts an E4X Xml object to a JSON object
     This function has been adapted from the work of Oleg Podsechin available at
     https://gist.github.com/olegp/642667
     It uses a slightly modified version of his algorithm , therefore
     all credit should be attributed to Oleg Podsechin.
     IMPORTANT:
     1. It does not create a 1..1 mapping due to the differences
     between Xml and JSON.It is IMPORTANT that you verify the structure
     of the object generated before using it.
     2. The input xml object must not contain the xml header information
     This is a known bug 336551 (Mozilla Developer Network)
     Source: https://developer.mozilla.org/en/docs/E4X
     Please remove the header prior to sending the xml object for processing.
     @root: A starting element in an E4X Xml object
     @return: A JSON object mirroring the provided Xml object
     */
    function recursiveConvertE4XtoJSON(root) {

        log.debug('Root: ' + root.localName());

        //Obtain child nodes
        var children = root.*;

        //The number of children
        var numChildren = children.length();

        //No children
        if (numChildren == 0) {

            //Extract contents
            return createJSONObject(root);
        }
        else {

            //Create an empty object
            var rootObject = createJSONObject(root);

            //Could be multiple children
            for (var childElementKey in children) {

                var child = children[childElementKey];

                log.debug('Examining child: ' + child.localName());

                //If the child just contains a single value then stop
                if (child.localName() == undefined) {

                    log.debug('Child is undefined: ' + child.toString());

                    //Change the object to just a key value pair
                    rootObject[root.localName()] = child.toString();
                    return rootObject;
                }

                //Make a recursive call to construct the child element
                var createdObject = recursiveConvertE4XtoJSON(child);

                log.debug('Converted object: ' + stringify(createdObject));

                //Check if the root object has the property
                if (rootObject.hasOwnProperty(child.localName())) {

                    log.debug('key: ' + child.localName() + ' already present.');
                    rootObject[child.localName()].push(createdObject);
                }
                else {

                    log.debug('key: ' + child.localName() + ' not present.');
                    rootObject[child.localName()] = [];
                    rootObject[child.localName()].push(createdObject);

                }
            }

            log.debug('root: ' + root.localName());

            return rootObject;
        }
    }

    return{
        /*The function takes a set of options
         and configures a target object
         @options: A set of options to configure the target
         @targ: The object to be configured
         */
        config: function (options, targ) {


            //Avoid if no options given or the
            //target is undefined
            if ((!options) || (!targ)) {
                return targ;
            }

            //Go through each field in the target object
            for (var key in targ) {

                //Avoid processing functions
                if (typeof targ[key] != 'function') {

                    var value = options[key];

                    //If a value is present
                    //do the configuration
                    if (value) {
                        targ[key] = value;
                    }
                }
            }
        },

        findInArray: function (array, fn) {
            for each(var item
            in
            array
            )
            {
                if (fn(item)) {
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
        each: function (array, fn) {
            for (var index in array) {
                fn(array[index], index);
            }
        },
        /*
         The function locates an item in the provided array
         based on the predicate function
         @array: The array to be searched
         @fn: A predicate function which returns when there is a match,else false
         */
        find: function (array, fn) {
            for (var index in array) {
                if (fn(array[index])) {
                    return array[index];
                }
            }

            return null;
        },

        /*
        The function executes the logic if a particular key is present
        in an object
        @object: The object to be checked
        @key: The property to be checked
        @logic:The logic to be executed
         */
        isPresent:function(object,key,logic){
            //Check if the object has a property called key
            if(object.hasOwnProperty(key)){
                logic(object[key]);
            }
        },

        /*
         The function copies the properties defined in the array from object A to B
         @objectA: The object to be targeted
         @objectB: The object which will recieve the values of A
         @propArray: An array of properties which occur in objectA

         */
        copyProperties: function (objectA, objectB, propArray) {
            for (var index in propArray) {
                var prop = propArray[index];
                objectB[prop] = objectA[prop];
            }
        },

        /*
         The function converts an array into a csv list
         @array: An array of values
         @returns: A CSV string of the array
         */
        createCSVString: function (array) {

            //Send it back without processing if it is not an array
            if (!(array instanceof Array)) {
                return array;
            }

            var csv = '';
            var count = 0;
            var item = null;

            //Go through each element in the array
            for (var index in array) {
                item = array[index];
                if (count > 0) {
                    csv += ','
                }
                csv += item;
                count++;

            }

            return csv;
        },


        /*
         File related utility functions
         */
        fileio: {
            /*
             The function returns all files with a given extension
             in the provided path.
             NOTE: Hidden and temporary files are ignored
             @path: The path of the directory
             @returns: An array of files in the given path is returned
             */
            getFiles: function (path, extension) {
                //Replace . if the user sends it with the extension
                extension = extension.replace('.', '');

                var dir = new File(path);
                var files = [];
                if (dir.isDirectory) {

                    var list = dir.listFiles();

                    for (var index in list) {

                        var item = list[index];

                        //Extract the extension
                        var fileName = item.getName().split('.');
                        //The extension will always be the last element of a file name when it is split by .
                        var foundExt = fileName[fileName.length - 1];

                        if ((item.getName().indexOf('~') == -1) && (foundExt == extension)) {
                            files.push(item);
                        }
                    }
                }

                return files;
            }
        },

        /*
         Xml related utility functions
         */
        xml: {

            convertE4XtoJSON:function(root){
                return recursiveConvertE4XtoJSON(root);
            }
        }
    }
}
