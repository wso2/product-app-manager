$('.auto-submit-star').rating({
	callback : function(value, link) {
		if (value == undefined) {
			value = 0;
		}

	}
});

$('#btn-post').click(function(e) {
	e.preventDefault();

	var body = $('#com-body').val(), 
		rating = $('input.star-rating-applied:checked').val();
		
		$.post('apis/v1/comments', {
				body:body,
				id : 1,
				parent : 1,
				parent_type : 'default'
				}, function(){
			console.log("Posted");
		});

})
