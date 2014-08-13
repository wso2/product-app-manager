$(function() {

	var showError = function (message) {
		var msg = message.replace(/[0-9a-z.+]+:\s/i, '');
		$('#register-alert').html(msg).fadeIn('fast');
		$('#btn-signin').text('Sign in').removeClass('disabled');
	};

    	var login = function() {
		if (!$("#form-login").valid())
			return;
		$('#btn-signin').addClass('disabled').text('Signing in');

		var username = $('#inp-username').val();
		var password = $('#inp-password').val();

        	caramel.ajax({
            		type: 'POST',
            		url: '/apis/user/login',
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

	var register = function() {
		if (!$("#form-register").valid())
			return;

		var username = $('#inp-username-register').val();
		var password = $('#inp-password-register').val();
		var confirmPassword = $('#inp-password-confirm').val();
		
		if(password == confirmPassword){
			
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
		}else{
			showError("Password and Password Repeat do not match. Please re-enter.");
		}
	};

	$('#btn-signout').live('click', function() {
		caramel.post("/apis/user/logout", function(data) {
			location.reload();
		}, "json");
	});

	$('#btn-signin').bind('click', login);

	$('#modal-login input').bind('keypress', function(e) {
		if (e.keyCode === 13) {
			login();
		}
	});

	$('#inp-username-register').change(function() {
		var username = $(this).val();
		caramel.ajax({
            		type: 'POST',
            		url: '/apis/user/exists',
            		data: JSON.stringify({
                		username: $('#inp-username-register').val()
            		}),
            		success: function (data) {
		        	if (data.error || data.exists) {
		        		$('#register-alert').html(data.message).fadeIn('fast');               
		        	} else {
	  				$('#register-alert').fadeOut('slow');
		        	}
            		},
		    	contentType: 'application/json',
		    	dataType: 'json'
        	});
	});

	$('#btn-register-submit').click(register);

	$('#modal-register input').keypress(function(e) {
		if (e.keyCode === 13) {
			register();
		}
	});


	$('#sso-login').click(function() {
		$('#sso-login-form').submit();
	});

	$('.store-menu > li > a').click(function(){
		var url = $(this).attr('href');
		window.location = url;
	});

    	$('.store-menu > li > ul > li > a').click(function(){
        	var url = $(this).attr('href');
        	window.location = url;
    	});


	$('.dropdown-toggle').click(function(){
		window.location = $(this).attr('href');
	});

});

