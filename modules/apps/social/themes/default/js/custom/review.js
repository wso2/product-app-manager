var $radio = $('.auto-submit-star');
var $btn = $('#btn-post');
var $textArea = $('#com-body');
var $stream = $('#stream');
var $firstReview = $('.com-first-review');
var $alert = $('.com-alert');
var windowProxy;

var onMessage = function (messageEvent) {
    console.log(messageEvent);
};
var publish = function (activity, onSuccess) {
    if (activity.target) {
        activity.context = {"id": target};
    } else {
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

var showAlert = function(msg){
	$alert.html(msg).fadeIn("fast").css('display','inline-block');
}
var showLoading = function(status){
	if(status){
		$alert.html('').css('display','inline-block').addClass('com-alert-wait');
	} else {
		$alert.hide().removeClass('com-alert-wait');
	}
}
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
	var review = $textArea.val();
	
    if (!review && !rating) {
		showAlert("Please add your Review and Rating");
	} else if(!review){
		showAlert("Please add your Review");
	} else if(!rating){
		showAlert("Please add your Rating");
    } else {
        var activity = {"verb": "post",
            "object": {"objectType": "review", "content": review, rating: rating}
        };

        $btn.attr('disabled', 'disabled');
        showLoading(true);
        
        publish(activity, function (published) {
            if ($firstReview.length) $firstReview.hide();
            $btn.removeAttr('disabled');

            if (published.success) {
                showLoading(false);
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


