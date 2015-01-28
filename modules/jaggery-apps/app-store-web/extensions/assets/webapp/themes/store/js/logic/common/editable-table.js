var Edtable;

$(function () {
    var UPDATE_API = '/store/resources/webapp/v1/application/a';
    var DELETE_API = '/store/resources/webapp/v1/application/';

    function EditableTable(configs) {
        init('#' + configs.tableContainer);
        this.resolve
    }

    var init = function (tableContainer) {

        var id;
        //Go through each row
        $(tableContainer + ' tr').each(function () {

            id = $(this).data('id');
            $(this).find('td').each(function () {
                var action = $(this).data('action');
                var resolveScript = "Edtable.resolve('" + id + "','" + action + "');";
                if (action) {
                    console.info('action: ' + action + ' for id: ' + id);
                    if (action == 'delete') {
                        $(this).html('<button class="btn btn-danger" onClick="' + resolveScript + '">Delete</button>');
                    }
                    else if (action == 'edit') {
                        $(this).html('<button class="btn" onClick="' + resolveScript + '">Edit</button>');
                    }

                }
            });
        });
    };


    Edtable = EditableTable;

    Edtable['resolve'] = function (id, action) {
        var tr = $('#row-' + id);
        if (action == 'delete') {
            deleteRow(id,tr);
        }
        else if (action == 'edit') {
            populateRow(id, tr);
        }
        else if (action == 'cancel') {
            resetRow(id, tr);
        }
        else if (action == 'save') {
            saveRow(id, tr);
        }
    };

    var populateRow = function (id, tr) {

        $(tr).find('td').each(function () {
            console.info('editing');
            var td = this;
            var fieldType = $(td).data('field');
            var action = $(td).data('action');
            var transitionsString = $(td).data('transitions');
            var transitions;

            if (fieldType) {
                populateTextbox(td);
            }

            if (transitionsString) {
                transitions = transitionsString.split(',');
                populateTransitions(id, td, transitions);
            }
        });
    };

    var populateTextbox = function (td) {
        //Save the existing value
        var existingValue = $(td).data('value');
        $(td).html('<input class="input-small" type="text" value="' + existingValue + '"/>');
    };

    var populateTransitions = function (id, td, transitions) {
        var resolveScript;
        $(td).html('');
        for (var index in transitions) {
            console.info('adding save and cancel');
            resolveScript = "Edtable.resolve('" + id + "','" + transitions[index] + "');";
            $(td).append('<button class="btn"  onClick="' + resolveScript + '">' + transitions[index] + '</button>')
        }
    }

    var resetRow = function (id, tr) {
        $(tr).find('td').each(function () {
            var td = this;
            var existingValue = $(td).data('value');
            var action = $(td).data('action');
            $(td).html(existingValue);
            var resolveScript = "Edtable.resolve('" + id + "','" + action + "');";
            if (action == 'edit') {
                $(td).html('<button class="btn" onClick="' + resolveScript + '">Edit</button>');
            }
        });
    };


    var saveRow = function (id, tr) {
        var data = createDataObject(id, tr);
        callSaveAPI(data, id, tr);
    };

    var createDataObject = function (id, tr) {
        var data = {};

        //Go through each data cell
        $(tr).find('td').each(function () {
            //Obtain the field name
            var fieldName = $(this).data('fieldName');
            var fieldType = $(this).data('field');

            if (fieldName) {
                //Obtain the old value
                var oldValue = $(this).data('value');
                data[fieldName] = oldValue;

                if (fieldType) {
                    var newValue = $(this).find('input').val();

                    var newFieldName = getNewFieldName(fieldName);

                    //Obtain the new value
                    data[newFieldName] = newValue;
                }

            }

        });

        return data;

    };

    var callSaveAPI = function (data, id, tr) {
        $.ajax({
            type: 'PUT',
            url: UPDATE_API,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function () {
                //Save the new values to the data fields
                $(tr).find('td').each(function () {
                    var fieldType = $(this).data('field');

                    //Only write data if it was a editable field type
                    if (fieldType) {
                        var oldValue = $(this).data('value');
                        var fieldName = $(this).data('fieldName');
                        var newFieldName = getNewFieldName(fieldName);
                        if (fieldName) {
                            $(this).data('value', data[newFieldName]);
                        }
                    }

                });

                resetRow(id, tr);
            }
        });

    };

    var getNewFieldName = function (oldFieldName) {
        var restOfNameWithoutFirstLetter = oldFieldName.substring(1);
        var firstLetter = oldFieldName.charAt(0);

        return 'new' + firstLetter.toUpperCase() + restOfNameWithoutFirstLetter;
    };

    /*
    The function invokes the api to delete an application
     */
    var deleteRow=function(id,tr){
        var dataObject=createDataObject(id,tr);
        $.ajax({
            type:'DELETE',
            url:DELETE_API+dataObject.appName,
            success:function(){
                alert('Asset deleted successfully!');
                $('#row-' + id).remove();
            }
        })
    };


});