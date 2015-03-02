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
var SERVER_EVENTS = 'server.events';

/**
 * Fetches callback object of this module from the application context.
 */
var callbacks = function () {
    var cbs = application.get(SERVER_EVENTS);
    if (cbs) {
        return cbs;
    }
    cbs = {};
    application.put(SERVER_EVENTS, cbs);
    return cbs;
};

/**
 * Fetches specified event object from the application context.
 * @param event
 * @return {*|Array}
 */
var events = function (event) {
    var cbs = callbacks();
    return cbs[event] || (cbs[event] = []);
};

/**
 * Registers an event listener in the server.
 * @param event
 * @param fn
 * @return {*}
 */
var on = function (event, fn) {
    var group = events(event);
    group.push(fn);
    return fn;
};

/**
 * Removes specified event callback from the listeners.
 * If this is called without fn, then all events will be removed.
 * @param event
 * @param fn callback function used during the on() method
 */
var off = function (event, fn) {
    var index, cbs,
        group = events(event);
    if (fn) {
        index = group.indexOf(fn);
        group.splice(index, 1);
        return;
    }
    cbs = callbacks();
    delete cbs[event];
};

/**
 * Executes event callbacks of the specified event by passing data.
 * @param event
 */
var emit = function (event) {
    var group = events(event),
        log = new Log(),
        args = Array.prototype.slice.call(arguments, 1);
    log.debug('Emitting event : ' + event);
    group.forEach(function (fn) {
        try {
            fn.apply(this, args);
        } catch(e) {
            log.error(e);
        }
    });
};

