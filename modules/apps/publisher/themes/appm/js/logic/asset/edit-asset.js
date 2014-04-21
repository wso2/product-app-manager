$(function () {
    var formManager = new FormManager('form-asset-edit');

    formManager.init();

    PageFormContainer.setInstance(formManager);

    $('#editAssetButton').on('click', function () {
        var data = formManager.getData();
        var report = formManager.validate();

        console.log(formManager.formMap);

        //Check if there are any validation failures
        if (report.failed) {
            var output=processClientErrorReport(report.form.fields);
            showAlert(output,'error');
            return;
        }

        var assetId=formManager.formMap.meta.assetId;
        var assetType=formManager.formMap.meta.assetType;
        var formData=formManager.getFormData();

        updateAsset(assetId,assetType,formData);
    });

    /*
     The function is used to build a report message indicating the errors in the form
     @report: The report to be processed
     @return: An html string containing the validation issues
     */
    var processErrorReport=function (report) {
        var msg = '';
        for (var index in report) {

            for (var item in report[index]) {
                msg += report[index][item] + "<br>";
            }
        }

        return msg;
    };


    /*
     The function is used to build a report message indicating the errors in the form
     @report: The report to be processed
     @return: An html string containing the validation issues
     */
    var processClientErrorReport = function (report) {
        var msg = '';
        for (var index in report) {

            for (var item in report[index]) {
                msg += report[index][item].msg + "<br>";
            }
        }

        return msg;
    };

    /**
     * The function makes a backend call to update the asset
     * @param assetId The id of the current asset
     * @param assetType  The current assets type
     * @param formData An object containing the data to be updated
     */
    var updateAsset=function(assetId,assetType,formData){
        var url = '/publisher/api/asset/' + assetType + '/' +assetId;
        $.ajax({
            type:'POST',
            url:url,
            data:formData,
            dataType:'json',
            processData:false,
            contentType:false,
            success:function(response){
                var result =response;

                //Check if the asset was added
                if (result.ok) {
                    showAlert('Asset updated successfully.', 'success');
                } else {
                    var msg = processErrorReport(result.report);
                    showAlert(msg, 'error');
                }
            },
            error:function(err){
                showAlert('Failed to update asset','error');
            }
        })
    };
});