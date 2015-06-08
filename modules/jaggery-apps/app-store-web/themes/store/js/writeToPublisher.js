/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/*
 * events[] used to store the events which this library excepts from the users
 * and also the results (eg: item, action, timestamp).
 * These values are returned to the publisher
 */
var events = [];

/*
 * Time gap which user needs to publish. value for this variable will be sent as
 * a parameter from the user (json object)
 */
var eventPublishTime;

/*
 * current page name. sent as an external parameter from the user
 */
var page;

/*
 * this Json object should be provided externally
 *
 * isActive: when '1' it execute this Js else this Js will be ignored
 *
 * path:publisher Jaggery path
 *
 * appData: user need to send relevant data (in each page user need to execute
 * this Js) and these data will be returned to publisher
 *
 * appControls: user can mention html controls here. only for the changes in
 * these controls this Js will execute
 *
 * publishedPeriod:Time gap which user needs to publish
 *
 *
 * required format: jsonPublisher =
 * {"isActive":"1","path":"/site/blocks/store/ajax/eventpublish.jag" ,
 * "appData":{"appId":"11-111-11", "userId":"admin"} ,"appControls":{ "0" :
 * "input", "1" : "select", "2" : "textarea","3" : "a"}
 * ,"publishedPeriod":"120000" ,"pageName":"testPage"};
 */

var isActive = false;

var jsonObj = {};

/*
 * When New Page Clicked user need to call this function and pass the json object built
 * @param action : page event name
 * @param jsonPublisherTemp : external json object
 */
function initializeUserActivity(action, jsonObjTemp) {
    jsonObj = jsonObjTemp;
    page = jsonObj["pageName"];
    addUserActivity(page, action);
};

/*
 * This method used to fill the events
 * @param item : page controller
 * @param action : page event name
 */
function addUserActivity(item, action) {
    var event = {};
    var strAppData = JSON.stringify(jsonObj.appData);
    /*
     * Add appData values to event[]
     */
    eval('event=' + strAppData);
    event.item = item;
    event.action = action;
    event.timestamp = $.now();
    events[events.length] = event;
};

/*
 * Publisher method. This method posts the data to the path specified
 * @param pageUnload : if the page unload event
 */
function publishEvents(pageUnload) {
    if (pageUnload) {
        addUserActivity(page, "page-unload");
    } else {
        addUserActivity(page, "same-page");
    }

    var copiedEvents = events;
    events = [];


    /*
     * Ajax post to Publish
     */
    $.ajax({
        url: jsonObj["path"],
        data: {
            'events': JSON.stringify(copiedEvents)
        },
        type: 'post',
        success: function (output) {
            //do nothing. Since this is a hidden UI function do not interrupt the user.
        },
        error: function (request, status, error) {
            log.warn("Error while publishing events - " + JSON.stringify(copiedEvents));
        }
    });

    if (!pageUnload) {
        setTimeout(function () {
            publishEvents(false);
        }, eventPublishTime);
    }
    return;
};

var addClickEvents = function (e) {
    var inputTypes = [];
    for (var prop in jsonObj.appControls) {
        inputTypes.push(jsonObj.appControls[prop]);
    }
    var target = $(e.target);
    for (var i = 0; i < inputTypes.length; i++) {
        if (target.is(inputTypes[i])) {
            var item = target.attr("name") ? target.attr("name") : target
                .attr("id");
            if (!item) {
                item = 'noname';
            }
            addUserActivity(item, "click");
            break;
        }
    }
};

$(document).ready(
    function ($) {
        /*
         * check if the parameters are not null
         */
        if (jsonObj["isActive"] != null && jsonObj["path"] != null
            && jsonObj["appData"] != null
            && jsonObj["appControls"] != null
            && jsonObj["publishedPeriod"] != null) {
            if (jsonObj["isActive"] == "1") {
                isActive = true;
            }
        }

        if (isActive) {
            eventPublishTime = jsonObj["publishedPeriod"];
            $(document).click(addClickEvents);

            setTimeout(function () {
                publishEvents(false);
            }, eventPublishTime);
        }
    });

$(window).bind('beforeunload', function () {
    if (isActive) {
        publishEvents(true);
    }
});
