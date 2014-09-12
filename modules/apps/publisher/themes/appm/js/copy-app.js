$( "#btnCopyApp" ).click(function() {
   var newVersion = $('#inputVersion').val();
   var assetId =  $('#inputId').val();


    $.ajax({
        url: '/publisher/api/asset/copy/'+ assetId,
        type: 'POST',
        contentType: 'application/json',
        data: {newVersion: newVersion},
        dataType: 'json',
        success: function(response) {
            alert(response);
        },
        error: function(response) {
            //showAlert('Error occured while fetching entitlement policy content', 'error');
        }
    });



});