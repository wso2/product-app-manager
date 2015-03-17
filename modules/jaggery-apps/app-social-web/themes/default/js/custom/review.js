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
var $radio = $('.auto-submit-star');
var $btn = $('#btn-post');
var $textArea = $('#com-body');
var $stream = $('#stream');
var $firstReview = $('.com-first-review');
var $alert = $('.com-alert');
var $sort = $('.com-sort');
var $lastReview = $('.load-more');
var $more = $('#more');
var $empty_list = $('#empty_list');
var windowProxy;


var publish = function (activity, onSuccess) {
    if (activity.target) {
        activity.context = {"id": target};
    } else {
        activity.target = {"id": target};
    }
    $.post('apis/comments.jag', {
        activity: JSON.stringify(activity)
    }, onSuccess)
};


var showAlert = function (msg) {
    $alert.html(msg).fadeIn("fast").css('display', 'inline-block');
};

var showLoading = function (status) {
    if (status) {
        $alert.html('').css('display', 'inline-block').addClass('com-alert-wait');
    } else {
        $alert.hide().removeClass('com-alert-wait');
    }
};

$radio.rating({
    callback: function (value) {
    }
});

$btn.click(function (e) {
    e.preventDefault();
    var rating = Number($('input.star-rating-applied:checked').val());
    var review = $textArea.val();

    if (!review && !rating) {
        showAlert("Please add your Review and Rating");
    } else if (!review) {
        showAlert("Please add your Review");
    } else if (!rating) {
        showAlert("Please add your Rating");
    } else {
        var activity = {"verb": "post",
            "object": {"objectType": "review", "content": review, rating: rating, "likes" : {"totalItems": 0}, "dislikes" : {"totalItems": 0}}
        };

        $btn.attr('disabled', 'disabled');
        showLoading(true);

        var pos = target.indexOf(':');
        var aid = target.substring(pos + 1);
        var type = target.substring(0, pos);


        var addAndRenderNew = function (successCallback) {
            $('#newest').addClass('selected');
                publish(activity, function (published) {
                    if ($firstReview.length) {
                        $firstReview.hide();
                        $sort.removeClass('com-sort-hidden');
                    }
                    $btn.removeAttr('disabled');


                    if (published.success) {
                        showLoading(false);
                        $radio.rating('select', null);
                        $textArea.val('');

                        activity.id = published.id;
                      //Remove carbon.super tenant domain from username
                        var pieces = user.split(/[\s@]+/);
                        if(pieces[pieces.length-1] == 'carbon.super'){
                            user= pieces[pieces.length-2];
                        }
                        activity.actor = {id: user};
                        usingTemplate(function (template) {
                            var newComment = template(activity);
                            $stream.prepend(newComment);
                            successCallback && successCallback();
                        });
                    }
                });
        };

        addAndRenderNew(function(){
            redrawReviews();
        });
    }
});

$stream.on('click', '.icon-thumbs-down', function (e) {
    e.preventDefault();
    var $likeBtn = $(e.target);
    var $review = $likeBtn.parents('.com-review');
    var id = $review.attr('data-target-id');
    var $likeCount = $review.find('.com-dislike-count');

    var activity = { target: {id: id}, object : {} };

    if ($likeBtn.hasClass('selected')) {
        activity.verb = 'undislike';
        publish(activity, function () {
            $likeCount.text((Number($likeCount.text()) - 1) || '');
        });
        $likeBtn.removeClass('selected');
    } else {
        activity.verb = 'dislike';
        publish(activity, function () {
            $likeCount.text(Number($likeCount.text()) + 1);
        });
        $likeBtn.addClass('selected');
    }
});

$stream.on('click', '.icon-thumbs-up', function (e) {
    e.preventDefault();
    var $likeBtn = $(e.target);
    var $review = $likeBtn.parents('.com-review');
    var id = $review.attr('data-target-id');
    var $likeCount = $review.find('.com-like-count');

        var activity = { target: {id: id}, object :{}};

    if ($likeBtn.hasClass('selected')) {
        activity.verb = 'unlike';
        publish(activity, function () {
            $likeCount.text((Number($likeCount.text()) - 1) || '');
        });
        $likeBtn.removeClass('selected');
    } else {
        activity.verb = 'like';
        publish(activity, function () {
            $likeCount.text(Number($likeCount.text()) + 1);
        });
        $likeBtn.addClass('selected');
    }

});

$more.on('click', '.load-more', function (e) {
    e.preventDefault();
    var offset = parseInt($('.load-more').attr("value"));
        $.get('apis/object.jag', {
            target: target,
            sortBy : $('.com-sort .selected').attr('id'),
            offset: offset,
            limit: 10
        }, function (obj) {
            var reviews = obj || [];

            if(jQuery.isEmptyObject(reviews) || reviews.length < 10){
                $more.hide();
                $empty_list.text("No more activities to retrieve.");
            }

            usingTemplate(function (template) {
                var str = "";
                for (var i = 0; i < reviews.length; i++) {
                    var review = reviews[i];
                    str += template(review);
                }
                $stream.append(str);
                //callback && callback();
                adjustHeight();
                $('.load-more').attr("value", parseInt(offset) + 10);
            });
        })

});

$stream.on('click', '.com-delete', function (e) {
    e.preventDefault();
    var $deleteBtn = $(e.target);
    var $review = $deleteBtn.parents('.com-review');
    var id = $review.attr('data-target-id');

    $.get('apis/comments.jag',{
        id:id
    }, function(obj){
        if(obj.success){
        $review.remove();
        }
    });
});