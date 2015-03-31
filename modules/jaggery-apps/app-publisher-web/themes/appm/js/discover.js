$(".btn-create-discovered" ).click(function(e) {
    var app = $(this).data("app");
    var action = $(this).data("action");
    var id = $(this).data("id")

    //proxy context entered
    var proxyContext = $('#proxy-context-'+id).val();

    var postData = {"proxy_context_path" : proxyContext};
    if(action=="Reject") {
        showCommentModel("Reason for Rejection",action,app);
    }else{

    $('#discover-create-asset-status').modal('show');
    jQuery.ajax({
        url: '/publisher/api/discover/asset/'+action+'/'+'webapp/'+id,
        type: "POST", data: postData, async:false, dataType : 'json',
        success: function (response) {
//            alert(response);
            $('#discover-create-asset-status #statusText').text('Application is being imported...');
            //Convert the response to a JSON object
            var statInfo = response.data;
            if(statInfo.ok == 'true') {
                var statusText = $('#discover-create-asset-status #statusText');
                statusText.removeClass('alert-error');
                statusText.addClass('alert-info');
                statusText.text('Created the Application :'+statInfo.appName);
                $('#discover-create-asset-status #info-table').show();
                $('#discover-create-asset-status #applicationName').text(statInfo.appName);
                $('#discover-create-asset-status #proxyContext').show();
                $('#discover-create-asset-status #proxyContext').text(statInfo.proxyContext);
            }else{
                alert(statInfo.message);
            }
        },
        error: function (response) {
            var statusText = $('#discover-create-asset-status #statusText');
            statusText.removeClass('alert-info');
            statusText.addClass('alert-error');
            statusText.text('Failed to create the application.');
            $('#discover-create-asset-status #info-table').hide();
//            $('#discover-create-asset-status #errorInfo').html(response.responseText);
        }
    }, 'json');
    }
    e.stopPropagation();
});