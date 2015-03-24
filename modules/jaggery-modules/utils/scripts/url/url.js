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
var url = {};
(function() {
	var log=new Log('utils-url');
    url.popServerDetails = function(obj) {
        var process = require('process');
        var localIP = process.getProperty('server.host');
        var httpPort = process.getProperty('http.port');
        var httpsPort = process.getProperty('https.port');
        var value = '';
        var carbonLocalIP = process.getProperty('carbon.local.ip');

        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                value = obj[key];
                if ((typeof value === 'string') && value.indexOf('%https.host%') > -1) {
                    value=value.replace('%https.host%', 'https://' + localIP + ':' + httpsPort);
                } else if ((typeof value === 'string') && value.indexOf('%http.host%') > -1) {
                    value=value.replace('%http.host%', 'http://' + localIP + ':' + httpPort);
                } else if ((typeof value === 'string') && value.indexOf('%https.carbon.local.ip%') > -1) {
                    value=value.replace('%https.carbon.local.ip%', 'https://' + carbonLocalIP + ':' + httpsPort);
                } else if ((typeof value === 'string') && value.indexOf('%http.carbon.local.ip%') > -1) {
                    value=value.replace('%http.carbon.local.ip%', 'http://' + carbonLocalIP + ':' + httpPort);
                }
                obj[key] = value;
            }
        }
        return obj;
    };
}(url));