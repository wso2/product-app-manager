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
var caramel = require('caramel');
var event = require('/modules/event.js');

caramel.configs({
    context: '/social',
    cache: true,
    negotiation: true,
    themer: function () {
        return 'default';
    }
});

var carbon = require('carbon');

var configs = require('/configs/social.js').config();
var STORE_CONFIG_PATH = '/_system/config/social/configs/social.json';

var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var log = new Log();

event.on('tenantCreate', function (tenantId) {
    var carbon = require('carbon'),
        system = server.systemRegistry(tenantId);
    system.put(STORE_CONFIG_PATH, {
        content: JSON.stringify({
            "permissions": {
                "login": {
                    "/permission/admin/login": ["ui.execute"]
                }
            }
        }),
        mediaType: 'application/json'
    });
});

event.on('tenantLoad', function (tenantId) {
    var carbon = require('carbon'),
        config = server.configs(tenantId),
        reg = server.systemRegistry(tenantId),
        um = server.userManager(tenantId);

    //check whether tenantCreate has been called
    if (!reg.exists(STORE_CONFIG_PATH)) {
    }
    event.emit('tenantCreate', tenantId);

    config[user.USER_OPTIONS] = {
        "permissions": {
            "login": {
                "/permission/admin/login": ["ui.execute"]
            }
        }
    };
});

