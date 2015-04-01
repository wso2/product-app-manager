/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
var reflection = {};
/**
 * Description: The script encapsulates any reflection related utility functions
 */
(function() {
    var log = new Log('utils-reflection');
    reflection.copyPropKeys = function(from, to) {
        for (var key in from) {
            if (from.hasOwnProperty(key)) {
                to[key] = '';
            }
        }
        return to;
    };
    /**
     * The function recursively copies all property keys in an object
     * @param from
     * @param to
     */
    reflection.copyAllPropKeys = function(from, to) {
        recurse(from, to, function(from, to, key) {
            if (from[key] instanceof Object) {
                to[key] = from[key];
            } else {
                to[key] = null;
            }
        });
    };
    reflection.copyAllPropValues = function(from, to) {
        recurse(from, to, function(from, to, key) {
            //Create an instance if the property does not exist
            if (!to[key]) {
                to[key] = {};
            }
            //Copy the values over
            if (!(from[key] instanceof Object)) {
                to[key] = from[key];
            } else {
                log.debug('Not copying values of key: ' + key);
            }
        });
    };
    /**
     * The function will only copy public properties
     * @param from
     * @param to
     */
    reflection.copyPublicPropValues = function(from, to) {
        recurse(from, to, function(from, to, key) {
            //Ignore any hidden properties
            if (key.charAt(0) == '_') {
                log.warn('Drop key: ' + key);
                return;
            }
            //Create an instance if the property does not exist
            if (!to[key]) {
                to[key] = {};
            }
            //Copy the values over
            if (!(from[key] instanceof Object)) {
                to[key] = from[key];
            } else {
                log.warn('Not copying values of key: ' + key);
            }
        });
    };
    reflection.inspect = function(from, to, cb) {
        recurse(from, to, cb);
    };
    /**
     * The function recursively traverses an object and then invokes the provided
     * callback
     * @param root
     * @param clone
     * @param cb
     */
    var recurse = function(root, clone, cb) {
        var key;
        //Check if the root is an object
        if (!(root instanceof Object)) {
            return;
        } else {
            var keys = Object.keys(root);
            //Go through all the other keys in the current root
            for (var index in keys) {
                key = keys[index];
                cb(root, clone, key);
                recurse(root[key], clone[key], cb);
            }
        }
    };
    reflection.copyProps = function(from, to) {
        for (var key in from) {
            if (from.hasOwnProperty(key)) {
                to[key] = from[key];
            }
        }
        return to;
    };
    reflection.getProps = function(obj) {
        var props = {};
        for (var key in obj) {
            if (!(obj[key] instanceof Function)) {
                props[key] = obj[key];
            }
        }
        return props;
    };
    reflection.printProps = function(obj) {
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                log.info('key: ' + key);
            }
        }
    };
    /**
     * The function determines if a property is hidden based on _
     * @param key
     * @returns {boolean}
     */
    reflection.isHiddenProp = function(key) {
        if (key == '') {
            return false;
        }
        return (key.charAt(0) == '_') ? true : false;
    };
    var getDiff = function(a, b, diff) {};
    /**
     * The function calculates the differences between two simple JSON objects
     * @param a  The object with which b is compared
     * @param b  The target of the comparison
     * @return An object which records the differences between the two objects
     */
    reflection.diff = function(a, b) {};
    /**
     * The function merges the two provided objects to create a new
     * object.In the case where b has the same property as a; the property of b
     * will have precedence
     * @param  {[type]} a [description]
     * @param  {[type]} b [description]
     * @return A new object having the properties of both object a and b
     */
    reflection.merge = function(a, b) {
        var newObj = {};
        //Copy the properties of a first
        for (var key in a) {
            newObj[key] = b[key];
        }
        //Override with the properties of b
        for (var key in b) {
            newObj[key] = b[key];
        }
        return newObj;
    };
    /**
     * The function allows a child class to override a select set of methods of
     * a parent class.The original methods of the parent can be accessed
     * using the this._super keyword
     * @param  {[type]} parent The parent class instance to be overriden
     * @param  {[type]} child  The child class instance containing methods which will override the parent
     */
    reflection.override = function(parent, child) {
        //Make a clone of the parent
        var super = parse(stringify(parent));
        for (var childKey in child) {
            for (var parentKey in parent) {
                //Only override those methods that are common
                if (childKey === parentKey) {
                    var parentPtr = parent[parentKey];
                    var childPtr = child[childKey];
                    //Update the clone with the old parent method
                    super[parentKey] = parentPtr;
                    parent[parentKey] = childPtr;
                    /*parent[parentKey] = function() {
                        var result=childPtr.apply(this, arguments)||null;
                        return result;
                    };*/
                }
            }
        }
        //Allow the child object to call methods of the parent
        parent._super = super;
    };
    reflection.overrideAll=function(parent,child){
       //Make a clone of the parent
        var super = parse(stringify(parent));
        for (var childKey in child) {
            for (var parentKey in parent) {
                //Only override those methods that are common
                if ( (child.hasOwnProperty(childKey))&&(parent.hasOwnProperty(parentKey)) ) {
                    var parentPtr = parent[parentKey];
                    var childPtr = child[childKey];
                    //Update the clone with the old parent method
                    super[parentKey] = parentPtr;
                    parent[parentKey] = childPtr;
                    /*parent[parentKey] = function() {
                        var result=childPtr.apply(this, arguments)||null;
                        return result;
                    };*/
                }
            }
        }
        //Allow the child object to call methods of the parent
        parent._super = super; 
    };
    reflection.isArray = function(object) {
        if (Object.prototype.toString.call(object) === '[object Array]') {
            return true;
        }
        return false;
    };
}());