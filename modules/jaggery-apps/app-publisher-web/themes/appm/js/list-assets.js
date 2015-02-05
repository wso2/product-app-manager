$(".btn-action" ).click(function(e) {
var app = $(this).data("app");
var action = $(this).data("action");

//alert(app + action);


    jQuery.ajax({
        url: '/publisher/api/lifecycle/' + action + '/webapp/' + app,
        type: "PUT",
        success: function (response) {
            //Convert the response to a JSON object
            var statInfo = JSON.parse(response);
            var isAsynch = statInfo.asynch;
            if (isAsynch == false && action == 'Approve') {
                showMessageModel("Your request to publish the application is awaiting administrator approval.", "Awaiting administrator approval", "webapp");
            } else {
                location.reload();
            }
        },
        error: function (response) {
            showMessageModel("Error occured while updating life-cycle state : " + action, "Lify-cycle update failed", "webapp");
        }
    });
	
	$( document ).ajaxComplete(function() {
		 location.reload();
	});

 e.stopPropagation();
});



$( ".tab-button" ).click(function() {
	
	var status = $(this).data("status");
	
	$( ".app-row" ).each(function( index ) {
		$(this).css("display", "none");		
		var appRowStatus = $(this).data("status");
		if(status == "All"){
			$(this).css("display", "table-row");
		}else if(status == appRowStatus){
			$(this).css("display", "table-row");
		}
	});
	

});

$( ".btn-view-app" ).click(function(e) {


    // alert($(this).data("id"));
    //alert($(this).data("category") );
    if($(this).data("category") === "webapp"){
        var url =$(this).data("url");
    }else {
        if ($(this).data("url") == 'undefined') {
            if ($(this).data("category") === "android") {
                var url = "https://play.google.com/store/apps/details?id=" + $(this).data("package");
            } else if ($(this).data("category") === "ios") {
                {
                    var url = "https://itunes.apple.com/en/app/id" + $(this).data("appid");
                }

            }

        }else{
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

    $('#messageModal2').html($('#confirmation-data1').html());
    $('#messageModal2 h3.modal-title').html((head));
    $('#messageModal2 div.modal-body').html('\n\n' + (msg) + '</b>');
    $('#messageModal2 a.btn-other').html('OK');
    $('#messageModal2').modal();
    $("#messageModal2").on('hidden.bs.modal', function () {
        window.location = '/publisher/assets/' + type + '/';
    });

};

function updateQRCode(text) {

        var element = document.getElementById("qrcode");
    
        

        var bodyElement = document.body;
        if(element.lastChild)
          element.replaceChild(showQRCode(text), element.lastChild);
        else
          element.appendChild(showQRCode(text));

      }