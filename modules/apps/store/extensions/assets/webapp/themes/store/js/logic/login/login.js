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
            url: '/store/extensions/assets/webapp/login',
            data: {
                username: username,
                password: password
            },
            success: function (data) {
                if (!data.error) {
                    location.reload();
                } else {
                    showError(data.message);
                }
            }
        });
    };

    $('#btn-signin').bind('click', login);
})