var QueryString = function () {
    // This function is anonymous, is executed immediately and
    // the return value is assigned to QueryString!
    var query_string = {};
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        // If first entry with this name
        if (typeof query_string[pair[0]] === "undefined") {
            query_string[pair[0]] = pair[1];
            // If second entry with this name
        } else if (typeof query_string[pair[0]] === "string") {
            var arr = [ query_string[pair[0]], pair[1] ];
            query_string[pair[0]] = arr;
            // If third or later entry with this name
        } else {
            query_string[pair[0]].push(pair[1]);
        }
    }
    return query_string;
}();

var $radio = $('.auto-submit-star');
$radio.rating({
    callback: function (value) {
    }
});

var $btn = $('#btn-post');
$btn.click(function (e) {
    e.preventDefault();
    var $textArea = $('#com-body');
    var rating = Number($('input.star-rating-applied:checked').val());

    if (!rating) {

    } else {
        var activity = {"verb": "post",
            "object": {"type": "rating", "content": $textArea.val(), rating: rating},
            "target": {"id": QueryString.target}
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
            $('#stream').prepend(newComment);

        });
    }
});


