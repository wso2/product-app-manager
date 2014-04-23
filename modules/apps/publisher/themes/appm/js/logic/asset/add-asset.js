/**
 * Description: The following script is used to create an asset.It uses the form manager
 *              to manage the input fields
 *
 */
$(function () {

    var formManager = new FormManager('form-asset-create');

    //Initialize the plugins
    formManager.init();

    PageFormContainer.setInstance(formManager);

    $('#btn-create-asset').on('click', function (e) {
	e.preventDefault();


        //Perform validations
        var report = formManager.validate();

        //Display the errors
        if (report.failed) {
            var msg = processClientErrorReport(report.form.fields);
            showAlert(msg, 'error');
            return;
        }

        var formData = formManager.getFormData();//formManager.validate();
        postData(formData);
    });

    /*
     The function is used to build a report message indicating the errors in the form
     @report: The report to be processed
     @return: An html string containing the validation issues
     */
    var processErrorReport = function (report) {
        var msg = '';
        for (var index in report) {

            for (var item in report[index]) {
                msg += report[index][item] + "<br>";
            }
        }

        return msg;
    }

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
    }

    /**
     * The method calls the add asset api to create a new asset
     * @param formData
     */
    var postData = function (formData) {
        var type = $('#meta-asset-type').val();

        $.ajax({
            url: '/publisher/asset/' + type,
            type: 'POST',
            processData: false,
            contentType: false,
            dataType: 'json',
            data: formData,
            success: function (response) {
                var result = response;

                //Check if the asset was added
                if (result.ok) {
                    showAlert('Asset added successfully.', 'success');
                    window.location = '/publisher/assets/' + type + '/';
                } else {
                    var msg = processErrorReport(result.report);
                    showAlert(msg, 'error');
                }

            },
            error: function (response) {
                showAlert('Failed to add asset.', 'error');
            }
        });
    }
});
