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
var request = {};
(function(request) {
    var hasOwnProperty = function(obj, element) {
        return Object.prototype.hasOwnProperty.call(obj, element);
    };
    var isObject = function(object) {;
        return typeof object === 'object';
    };
    /*
     * ECMA Standard (ECMA-262 : 5.1 Edition)*/
    var decodes = function(encodedURI) {
        return decodeURIComponent(encodedURI);
    };
    request.getQueryOptions = function(queryString) {
        var opt={};
        var sep = opt.sep || '&',
            assign = opt.assign || '=',
            compoArray = [];
        var obj = {};
        var decodedURI = decodes(queryString);
        decodedURI.split(sep).forEach(function(comp) {
            comp.split(assign).some(function(element, index, array) {
                if (hasOwnProperty(obj, element.toString())) {
                    compoArray.push(obj[element]);
                    compoArray.push(array[1]);
                    obj[element] = compoArray;
                } else {
                    Object.defineProperty(obj, element, {
                        enumerable: true,
                        writable: true,
                        value: array[1]
                    });
                }
                return true;
            });
        });
        return obj;
    };
}(request))