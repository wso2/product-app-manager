/*
 Description: The script is used to delete an asset
 Filename: delete.asset.js
 Created Date: 22/7/2014
 */

$(function(){

$('.btn-delete').on('click', function(e) {
        //The type of asset
        var type = $('#meta-asset-type').val();
        var provider = $(this).data("provider");
        var name = $(this).data("name");
        var version = $(this).data("version");


        var status = isExistInExternalStore(provider, name, version);
        if(status) {
            var msg = "This app is published to one or more external stores .\n" +
                "Please remove this app from external stores before delete";
            var head = "Delete Failed";
            showMessageModel(msg, head, type);
            return false;
        }

		var data = {};

		var id = $(this).attr('data-app-id');

            e.preventDefault();
            e.stopPropagation();

             var confirmDel = confirm("Are you sure you want to delete this app?");
            if (confirmDel == true) {
                $.ajax({
                    url: '/publisher/api/asset/delete/' + type + '/' + id,
                    type: 'POST',
                    contentType: 'application/json',
                    success: function(response) {
                        var result = JSON.parse(response);
                        if (result.isDeleted) {
                            showDeleteModel("Successfully deleted the Asset","Deleted Successfully",type);
                        } else if(result.isDeleted == false){
                            showDeleteModel("Cannot Delete. Asset is already subscribed.","Asset Subscribed",type);
                        }else{
                            showDeleteModel("Asset is not successfully deleted","Delete Failed",type);
                        }
                    },
                    error: function(response) {
                        showDeleteModel("Asset is not successfully deleted","Delete Failed",type);
                    }
                });
            }

	});

	var showDeleteModel=function(msg,head,type){

		$('#messageModal2').html($('#confirmation-data1').html());
		$('#messageModal2 h3.modal-title').html((head));
		$('#messageModal2 div.modal-body').html('\n\n'+ (msg)+ '</b>');
		$('#messageModal2 a.btn-other').html('OK');
		$('#messageModal2').modal();
		$("#messageModal2").on('hidden.bs.modal', function(){
			window.location = '/publisher/assets/' + type + '/';
		});
		
	};
});

function isExistInExternalStore(provider, name, version) {
    var publishedInExternalStores = false;
    $.ajax({
        async: false,
        url: '/publisher/api/asset/get/external/stores/webapp/' + provider + '/' + name + '/' + version,
        type: 'GET',
        processData: true,
        success: function (response) {
            if (!response.error) {
                var appStores = response.appStores;

                if (appStores != null && appStores != undefined) {
                    for (var i = 0; i < appStores.length; i++) {
                        if (appStores[i].published) {
                            publishedInExternalStores = true;
                            break;
                        }
                    }
                }
            }
            return publishedInExternalStores;

        },
        error: function (response) {

        }
    });

    return publishedInExternalStores;
}