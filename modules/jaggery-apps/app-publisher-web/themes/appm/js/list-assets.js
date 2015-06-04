$(".btn-action").click(function (e) {
	$(this).hide(); //to avoid user from click again before the operation proceeds
	var app = $(this).data("app");
	var action = $(this).data("action");

	if (action == "Reject") {
		showCommentModel("Reason for Rejection", action, app, "webapp");
	} else {
		jQuery.ajax({
			url: '/publisher/api/lifecycle/' + action + '/webapp/' + app,
			type: 'PUT',
			success: function (data, text) {
				var msg = data.message[0];
				for (var i = 1; i < data.message.length; i++) {
					msg = msg + "</br>" + data.message[i];
				}
				showMessageModel(msg, data.status, 'webapp');
			},
			error: function (request, status, error) {
				var data = jQuery.parseJSON(request.responseText);
				var msg = data.message[0];
				for (var i = 1; i < data.message.length; i++) {
					msg = msg + "</br>" + data.message[i];
				}
				showMessageModel(msg, data.status, 'webapp');
			}
		});
	}

	// Stop even propagation since it would trigger the click event listeners for the table rows.
	e.stopPropagation();
});


$(".btn-reject-proceed").click(function () {
	var comment = $("#commentText").val();
	if (comment.trim() == "") {
		alert("Please provide a comment.");
		return false;
	}

	var app = $("#webappName").val();
	var action = $("#action").val();
	jQuery.ajax({
		url: '/publisher/api/lifecycle/' + action + '/webapp/' + app,
		type: "PUT",
		data: JSON.stringify({comment: comment}),
		success: function (msg) {
			location.reload();
		}
	});

});


$(".btn-deploySample").click(function (e) {
	jQuery.ajax({
		url: '/publisher/api/asset/webapp/deploySample',
		type: "GET",
		dataType: "json",
		async: false,
		success: function (msg) {
			if (msg.isError == "true") {
				$(document).ajaxComplete(function () {
					location.reload();
				});
			}
		}
	});

});

$(".tab-button").click(function () {

	var status = $(this).data("status");

	$(".app-row").each(function (index) {
		$(this).css("display", "none");
		var appRowStatus = $(this).data("status");
		if (status == "All") {
			$(this).css("display", "table-row");
		} else if (status == appRowStatus) {
			$(this).css("display", "table-row");
		}
	});


});

$(".btn-view-app").click(function (e) {


	// alert($(this).data("id"));
	//alert($(this).data("category") );
	if ($(this).data("category") === "webapp") {
		var url = $(this).data("url");
	} else {
		if ($(this).data("url") == 'undefined') {
			if ($(this).data("category") === "android") {
				var url = "https://play.google.com/store/apps/details?id=" + $(this).data("package");
			} else if ($(this).data("category") === "ios") {
				{
					var url = "https://itunes.apple.com/en/app/id" + $(this).data("appid");
				}

			}

		} else {
			var url = window.location.protocol + "//" + window.location.host + $(this).data("url");
		}
	}
	$("#appModalAppURL").attr("href", url);
	$("#appModalAppURL").html(url);
	updateQRCode(url);

	$("#appModal").modal('show');
	e.stopPropagation();


});

var showMessageModel = function (msg, head, type) {
	$('#messageModal2 #commentText').html('');
	$('#messageModal2').html($('#confirmation-data1').html());
	$('#messageModal2 h3.modal-title').html((head));
	$('#messageModal2 #myModalLabel').html((head));
	$('#messageModal2 div.modal-body').html('\n\n' + (msg) + '</b>');
	$('#messageModal2 a.btn-other').html('OK');
	$('#messageModal2').modal();
	$("#messageModal2").on('hidden.bs.modal', function () {
		window.location = '/publisher/assets/' + type + '/';
	});

};

var showCommentModel = function (head, action, app, type) {
	$('#messageModal3').html($('#confirmation-data1').html());
	$('#messageModal3 h4.modal-title').html((head));
	$('#messageModal3 #webappName').val(app);
	$('#messageModal3 #action').val(action);
	$('#messageModal3').modal();
	$("#messageModal3").on('hidden.bs.modal', function () {
		window.location = '/publisher/assets/' + type + '/';
	});
};

function updateQRCode(text) {

	var element = document.getElementById("qrcode");
	var bodyElement = document.body;
	if (element.lastChild)
		element.replaceChild(showQRCode(text), element.lastChild);
	else
		element.appendChild(showQRCode(text));

}