/*
 Description: The script is used to edit an asset
 */
$(function () {
    console.log('edit-asset loaded');

    $('#editAssetButton').on('click', function () {
        var data = {};

        //Obtain the current url
        var url=window.location.pathname;

        //The type of asset
        var type=$('#meta-asset-type').val();

        //The id
        //Break the url into components
        var comps=url.split('/');

        //Given a url of the form /pub/api/asset/{asset-type}/{asset-id}
        //length=5
        //then: length-2 = {asset-type} length-1 = {asset-id}
        var id=comps[comps.length-1];

        //Extract the fields
        var fields = $('#form-asset-edit :input');

        //Create the data object which will be sent to the server
        fields.each(function () {

            if (this.type != 'button') {
                data[this.id] = this.value;
            }
        });

        var url='/publisher/api/asset/'+type+'/'+id;

        //Make an AJAX call to edit the asset
        $.ajax({
            url:url,
            type:'PUT',
            data:data,
            contentType: "application/json",
            success:function(response){
                alert('Asset updated successfully.');
            },
            error:function(response){
                alert('Unable to update asset.');
            }
        })

    });

});
