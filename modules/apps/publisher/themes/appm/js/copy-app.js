$( "#btnCopyApp" ).click(function() {
   var newVersion = $('#inputVersion').val();
   var assetId =  $('#inputId').val();
   var appName =  $('#inputName').val();
   var provider = $('#inputProvider').val();
   var oldVersion =  $('#inputOldVersion').val();


    $.ajax({
        url: '/publisher/api/asset/web/copy/'+ assetId,
        type: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        data: {newVersion: newVersion, oldVersion: oldVersion, provider: provider, "appName": appName },
        dataType: 'json',
        success: function(response) {

            showAlert("New version of the " + appName + " app created successfully!",  'success');
            $('#inputVersion').val("");
        },
        error: function(response) {
            showAlert('Error occured while creating the app', 'error');

        }
    });




});