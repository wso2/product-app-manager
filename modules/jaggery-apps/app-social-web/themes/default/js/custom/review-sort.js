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
var $stream = $stream || $('#stream');
var $more = $('#more');
var $empty_list = $('#empty_list');

var usingTemplate = function (callback) {
    caramel.partials({activity: 'themes/' + caramel.themer + '/partials/activity.hbs'}, function () {
        callback(Handlebars.partials['activity']);
    });
};

var redrawReviews = function (sortBy, callback) {
    $('.com-sort .selected').removeClass('selected');
    $.get('apis/object.jag', {
        target: target,
        sortBy: sortBy,
        offset: 0,
        limit: 10
    }, function (obj) {
        var reviews = obj || [];
        usingTemplate(function (template) {
            var str = "";
            for (var i = 0; i < reviews.length; i++) {
                var review = reviews[i];
                str += template(review);
            }
            $stream.html(str);
            $('.load-more').attr("value", 10);
            $more.show();
            $empty_list.text("");
            //callback && callback();
            adjustHeight();
        });
    })
};

$(document).on('click', '.com-sort a', function (e) {
    var $target = $(e.target);
    if (!$target.hasClass('selected')) {
        redrawReviews($target.text().toUpperCase());
        $target.addClass('selected');
    }
});

