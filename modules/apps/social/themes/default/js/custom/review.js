var $radio = $('.auto-submit-star');
var $btn = $('#btn-post');
var $textArea = $('#com-body');
var $stream = $('#stream');
var windowProxy;

var onMessage = function (messageEvent) {
    console.log(messageEvent);
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
            "object": {"type": "rating", "content": $textArea.val(), rating: rating},
            "target": {"id": target}
        };

        var body = $textArea.val();

        $btn.attr('disabled', 'disabled');
        $.get('apis/comments.jag', {
            activity: JSON.stringify(activity)
        }, function () {
            $btn.removeAttr('disabled');
            $radio.rating('select', null);
            $textArea.val('');
        });

        caramel.partials({activity: 'themes/' + caramel.themer + '/partials/activity.hbs'}, function () {
            var newComment = Handlebars.partials['activity'](activity);
            $stream.prepend(newComment);
            if (adjustHeight) {
                adjustHeight();
            }

        });
    }
});


