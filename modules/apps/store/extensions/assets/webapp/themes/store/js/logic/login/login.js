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
    
    var register = function() {
		if (!$("#form-register").valid())
			return;
		
		caramel.ajax({
            		type: 'POST',
            		url: '/apis/user/register',
            		data: JSON.stringify({
            			username : $('#inp-username-register').val(),
   				password : $('#inp-password-register').val()
            		}),
            		success: function (data) {
		        	if (!data.error) {
				  	$('#messageModal').html($('#confirmation-data').html());
				      	$('#messageModal h3.modal-title').html(('APP Store - Notification'));
					$('#messageModal a.btn-primary').html('OK');
					$('#messageModal div.modal-body').html();
					$('#messageModal').modal();
					$('#modal-register').modal('hide');
					$('#messageModal a.btn-primary').click(function() {
					$('#messageModal').modal('hide');
					$('#modal-login').modal('show'); 
				  	});
		                } else {
		        		showError(data.message);
		        	}
            		},
            		contentType: 'application/json',
            		dataType: 'json'
        	});
	};
    
	$('#btn-register-submit').click(register);

})
