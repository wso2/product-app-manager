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
var guestDomain = 'localhost:9443';

function onMessage1(messageEvent) {
    console.log(messageEvent);
    if (messageEvent.origin == "https://" + guestDomain) {
        var height = messageEvent.data.expanded;
        if (height) {
            $('#socialIfr').height(height);
        }
    }
}


var windowProxy1;
window.onload = function () {
    windowProxy1 = new Porthole.WindowProxy('http://localhost:9763/social/themes/default/js/lib/proxy.html', 'socialIfr');
    windowProxy1.addEventListener(onMessage1);
};