$(function(){

	/*
	Creates a new asset
	*/
	var type=$('#meta-asset-type').val();

    /**
     * Handle create asset logic
     * loop all input from form area and post to asset creation
     */
    $('#btn-create-asset').on('click',function(){
			var fields=$('#form-asset-create :input');
			var data={};
			fields.each(function(){
				if(this.type!='button')
				{
					data[this.id]=this.value;
				}
			});

			$.ajax({
				url:caramel.context + '/asset/'+type,
				type:'POST',
				data: data,
				success:function(response){
					alert('asset added.');
					window.location = caramel.context + '/assets/' + type + '/';
				},
				error:function(response){
					alert('Failed to add asset.');
				}
			});
    });

});
