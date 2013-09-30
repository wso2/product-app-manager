$(function(){

	/*
	Creates a new asset
	*/

	//var id=$('#meta-asset-id').html();
	var type=$('#meta-asset-type').val();
	
		
		
		$('#btn-create-asset').on('click',function(){
			var fields=$('#form-asset-create :input');
			var data={};
            var formData=new FormData();
			fields.each(function(){
				if(this.type!='button')
				{
					//console.log(this.value);
					data[this.id]=this.value;
                    formData=fillForm(this,formData);
				}
			});

			$.ajax({
				url:'/publisher/asset/'+type,
				type:'POST',
				data: formData,
                cache:false,
                contentType:false,
                processData:false,
				success:function(response){
					alert('asset added.');
					window.location='/publisher/assets/'+type+'/';
				},
				error:function(response){
					alert('Failed to add asset.');
				}
			});
			

			
			//$.post('/publisher/asset/'+type, data);

		});
	//}


    /*
    The function is used to add a given field to a FormData element
    @field: The field to be added to the formData
    @formData: The FormDara object used to store the field
    @return: A FormData object with the added field
     */
    function fillForm(field,formData){

        var fieldType=field.type;

        if(fieldType=='file'){
           console.log('added '+field.id+' file.');
           formData.append(field.id,field.files[0]);
        }
        else{
           formData.append(field.id,field.value);
        }

        return formData;
    }
});
