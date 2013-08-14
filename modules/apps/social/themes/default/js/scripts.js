 $('.auto-submit-star').rating({
        callback: function (value, link) {		
		if(value==undefined){
			value=0;
		}
        	$('.rate-num-assert').html('('+value+')');
            caramel.post('/apis/rate', {
                asset: $('#assetp-tabs').data('aid'),
                value: value || 0
            }, function (data) {

            });
        }
    });
