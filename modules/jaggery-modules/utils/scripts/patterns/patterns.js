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
var patterns = {};

(function () {

    var DEF_ERR_ARITY = 3;
    var DEF_HANDLE_ARITY = 2;
    var log = new Log('utils.patterns.GenericPipe');

    function GenericPipe(options) {
        this.errHandlerArity = DEF_ERR_ARITY || options.errArity;
        this.handlerArity = DEF_HANDLE_ARITY || options.handlerArity;
        this.plugins = [];
        this.finalHandler = function () {
        };
    }


    /**
     *The function registers the provided plugin
     */
    GenericPipe.prototype.plug = function (plugin, options) {
        var options = options || {};
        //Only a function
        if (plugin instanceof Function) {
            this.plugins.push({
                handle: plugin,
                options: options
            });
        }
        //Is it a plugin object
        else if (plugin instanceof Object) {
            plugin.options = options;
            this.plugins.push(plugin);
        }

        return this;
    };

    GenericPipe.prototype.finally = function (plugin) {
        this.finalHandler = plugin;
        return this;
    };

    GenericPipe.prototype.resolve = function (data, req, res, session) {
        var context = {};
        context.req = req;
        context.res = res;
        context.session = session;
        context.data = data;
        handle(context, this.plugins, this.errHandlerArity, this.handlerArity, this.finalHandler);
    };

    var handle = function (context, plugins, errArity, handlerArity, finallyHandler) {
        var index = 0;
        var currentPlugin;

        var recursiveHandle = function (err) {

            currentPlugin = plugins[index];

            index++;

            //Check if there is a plugin
            if (!currentPlugin) {
                //log.warn('No plugin found at index: ' + index);
                return;
            }

            //Populate the options object for the plugin
            context.options=currentPlugin.options;;

            //Check if an error has been provided
            if (err) {
                //Can the current plugin handle the err
                if (currentPlugin.handle.length == errArity) {
                    try {
                        currentPlugin.handle(err, context,recursiveHandle);
                    }
                    catch (e) {
                        recursiveHandle(e);
                    }
                }
                else {
                    recursiveHandle(err);
                }
            }
            //There is no error so try to invoke the current plugin
            else {
                if (currentPlugin.handle.length == handlerArity) {
                    try {


                        currentPlugin.handle(context,recursiveHandle);
                    } catch (e) {
                        recursiveHandle(e);
                    }
                }
                else {
                    recursiveHandle();
                }
            }
        };

        recursiveHandle();
        finallyHandler(context);
    };

    patterns.GenericPipe = GenericPipe;

}());
