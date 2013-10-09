var $radio = $('.auto-submit-star');
var $btn = $('#btn-post');
var $textArea = $('#com-body');
var $stream = $('#stream');
var $firstReview = $('.com-first-review');
var windowProxy;

var onMessage = function (messageEvent) {
    console.log(messageEvent);
};
var publish = function (activity, onSuccess) {
    if(activity.target){
        activity.context = {"id": target};
    }else{
        activity.target = {"id": target};
    }
    activity.actor = {"id": user, "objectType": "person" };
    $.get('apis/comments.jag', {
        activity: JSON.stringify(activity)
    }, onSuccess)
};

var adjustHeight = function () {
    windowProxy.post({'expanded': $(document).height()});
};

$(function () {
    windowProxy = new Porthole.WindowProxy();
    windowProxy.addEventListener(onMessage);
    adjustHeight();
});

$radio.rating({
    callback: function (value) {
    }
});

$btn.click(function (e) {
    e.preventDefault();
    var rating = Number($('input.star-rating-applied:checked').val());

    if (!rating) {

    } else {
        var activity = {"verb": "post",
            "object": {"objectType": "review", "content": $textArea.val(), rating: rating}
        };

        $btn.attr('disabled', 'disabled');
        publish(activity, function (published) {
            if ($firstReview.length) $firstReview.hide();
            $btn.removeAttr('disabled');

            if (published.success) {
                $radio.rating('select', null);
                $textArea.val('');

                activity.id = published.id;
                caramel.partials({activity: 'themes/' + caramel.themer + '/partials/activity.hbs'}, function () {
                    var newComment = Handlebars.partials['activity'](activity);
                    $stream.prepend(newComment);
                    if (adjustHeight) {
                        adjustHeight();
                    }
                });
            }
        });

    }
});

$stream.on('click', '.icon-thumbs-up', function (e) {
    var $likeBtn = $(e.target);
    var $review = $likeBtn.parents('.com-review');
    var id = $review.attr('data-target-id');
    var $likeCount = $review.find(".com-like-count");

    var activity = {"verb": "like",
        "object": {"objectType": "like"},
        "target": {"id": id}
    };

    publish(activity, function () {
        $likeCount.text(Number($likeCount.text()) + 1);
    });

});


