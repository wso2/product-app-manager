$(document).ready(function(){
	$('.dropdown-toggle').dropdown();
	$('#myTab a').click(function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	});
});
