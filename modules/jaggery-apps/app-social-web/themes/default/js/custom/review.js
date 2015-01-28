var $radio = $('.auto-submit-star');
var $btn = $('#btn-post');
var $textArea = $('#com-body');
var $stream = $('#stream');
var $firstReview = $('.com-first-review');
var $alert = $('.com-alert');
var $sort = $('.com-sort');
var windowProxy;


var publish = function (activity, onSuccess) {
    if (activity.target) {
        activity.context = {"id": target};
    } else {
        activity.target = {"id": target};
    }
    $.get('apis/comments.jag', {
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
            "object": {"objectType": "review", "content": review, rating: rating}
        };

        $btn.attr('disabled', 'disabled');
        showLoading(true);

        var pos = target.indexOf(':');
        var aid = target.substring(pos + 1);
        var type = target.substring(0, pos);


        var addAndRenderNew = function (successCallback) {
            $('#newest').addClass('selected');
            $.get("/store/apis/rate", {
                id: aid,
                type: type,
                value: rating
            }, function (r) {
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
                        activity.actor = {id: user};
                        usingTemplate(function (template) {
                            var newComment = template(activity);
                            $stream.prepend(newComment);
                            successCallback && successCallback();
                        });
                    }
                });
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

    var activity = { target: {id: id} };

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

    var activity = { target: {id: id} };

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

