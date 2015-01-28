var $stream = $stream || $('#stream');

var didILike = function (review, username) {
    var likes = review.likes && review.likes.items;
    if (likes) {
        for (var j = 0; j < likes.length; j++) {
            var like = likes[j];
            if (like.id == username) {
                return true;
            }
        }
    }
    return false;
};

var usingTemplate = function (callback) {
    caramel.partials({activity: 'themes/' + caramel.themer + '/partials/activity.hbs'}, function () {
        callback(Handlebars.partials['activity']);
    });
};

var redrawReviews = function (sortBy, callback) {
    $('.com-sort .selected').removeClass('selected');
    $.get('apis/object.jag', {
        target: target,
        sortBy: sortBy
    }, function (obj) {
        var reviews = obj.attachments || [];
        usingTemplate(function (template) {
            var str = "";
            for (var i = 0; i < reviews.length; i++) {
                var review = reviews[i];
                var iLike = didILike(review, user);
                review.iLike = iLike;
                console.log(iLike);
                str += template(review);
            }
            $stream.html(str);
            callback && callback();
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

