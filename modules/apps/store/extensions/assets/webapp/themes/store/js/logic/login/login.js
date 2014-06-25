$(function(){
    console.info('Loaded login script');

    var login = function() {
        if (!$("#form-login").valid())
            return;
        $('#btn-signin').addClass('disabled').text('Signing in');

        var username = $('#username').val();
        var password = $('#password').val();

        $.ajax({
            type: 'POST',
            url: '/store/apis/user/login',
            data: JSON.stringify({
            	 username: username,
                 password: password
            }),
            success: function (data) {
                if (!data.error) {
                    var assetId = $('#modal-login').data('value');
                    if(assetId == "" || assetId == null){
              		   location.reload();  
                    }else  {
                 	  window.location = '/store/assets/webapp/'+ assetId;
                    }               
                } else {
                    showError(data.message);
                }
            },
            contentType: 'application/json',
            dataType: 'json'
        });
    };

    $('#btn-signin').bind('click', login);
})
