
var QueryString = function () {
  // This function is anonymous, is executed immediately and
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
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
} ();

$('.auto-submit-star').rating({
    callback: function (value, link) {
        if (value == undefined) {
            value = 0;
        }

    }
});

$('#btn-post').click(function (e) {
    e.preventDefault();
    var activity = {"verb": "post",
        "object": {"type": "comment", "content":$('#com-body').val()},
        "target": {"id": QueryString.target}
    };

    var body = $('#com-body').val(),
        rating = $('input.star-rating-applied:checked').val();

    $.get('apis/comments.jag', {
        activity: JSON.stringify(activity)
    }, function () {
        location.reload();
    });

})


