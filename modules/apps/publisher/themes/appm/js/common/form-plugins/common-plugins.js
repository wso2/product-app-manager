/**
 * Description: The following script contains a set of commonly used plug-ins
 */
$(function () {

    /**
     * The plugin is used to indicate a field is required as well as perform a check
     * to see if the user has entered a value
     * @constructor
     */
    function RequiredField() {

    }

    /**
     * The plug-in will only be applied if the required property has been set to true
     * @param element
     * @returns True if the plug-in should be applied else false
     */
    RequiredField.prototype.isHandled = function (element) {
        var isRequired = element.meta.required ? element.meta.required : false;
        return isRequired;
    };

    RequiredField.prototype.init = function (element) {
        $('#' + element.id).after('<span class="label-required">*</span>');
        $('#' + element.id).attr('required', 'true');
    };

    RequiredField.prototype.validate = function (element) {
        var value = $('#' + element.id).val();
        if (value == '') {
            return {msg: 'Field: ' + element.id + ' is a required field.'};
        }
    };

    function ReadOnlyField() {

    }

    ReadOnlyField.prototype.init = function (element) {
        $('#' + element.id).attr('disabled', true);
    };


    /**
     *
     * Text Field Value Extractor plugin
     * Description: Obtains the value of a text field
     *
     */

    function TextFieldValueExtractor() {

    }

    TextFieldValueExtractor.prototype.init = function (element) {

    };

    TextFieldValueExtractor.prototype.getData = function (element) {
        var data = {};
        data[element.id] = $('#' + element.id).val();
        return data;
    };

    /**
     * The plugin will print the value of a text field to the console when ever it changes
     * @constructor
     */
    function PrintValueToConsole() {

    }

    PrintValueToConsole.prototype.init = function (element) {
        $('#' + element.id).on('change', function () {
            console.log('Value changed');
        })
    };

    function DefaultFileUploadPlugin() {

    }

    DefaultFileUploadPlugin.prototype.getData = function (element) {
        var file = $('#' + element.id).get('0').files[0];
        var data = {};
        data[element.id] = file;
        return data;
    };

    function FileUpdatePlugin() {

    }

    FileUpdatePlugin.prototype.init = function (element) {
        var isRequired = element.meta.required ? element.meta.required : false;
        if (isRequired) {
            $('#' + element.id).after('<span class="label-required">*</span>');
            $('#' + element.id).attr('required', 'true');
        }
    }


    /**
     * The method will check if the user has uploaded a file,if not then the original file
     * will be returned
     * @param element The file element to be operated on
     */
    FileUpdatePlugin.prototype.getData = function (element) {
        var file = $('#' + element.id).get('0').files[0];
        var data = {};
        //Check if there is a file,if not set it to the original file
        if (!file) {
            file = element.meta.originalFile;
        }
        data[element.id] = file;
        return data;
    };


    /**
     * The DatePickPlugin is used to render date fields
     * @constructor
     */
    function DatePickerPlugin() {

    }


    DatePickerPlugin.prototype.init = function (element) {
        var e = '#' + element.id;
        var format = element.meta.dateFormat;
        $(e).datepick({dateFormat: format});
    };

    DatePickerPlugin.prototype.getData = function (element) {
        var data = {};
        data[element.id] = $('#' + element.id).val(); //TODO: Change the returned value
        return data;
    };

    function FormMetaReader() {

    }

    FormMetaReader.prototype.init = function (element) {
        var assetId = $('#' + element.id).data('assetId') || null;
        var assetType = $('#' + element.id).data('assetType') || null;

        element.meta.assetId = assetId;
        element.meta.assetType = assetType;
    };


    /**
     * The function gathers normalizes the data from one multiple rows
     * to a column level
     * It collects all data from a row and organizes it based the column,
     * @constructor
     */
    function UnboundDataCompiler() {

    }


    UnboundDataCompiler.prototype.getData = function (element, data) {
        //alert('Data compiler called');

        for (var key in data) {
            //If it is an unbounded field
            if (isUnboundField(key)) {
                var fieldName = getUnboundFieldName(key);

                //Check if an entry exists
                if (!data.hasOwnProperty(fieldName)) {
                    data[fieldName] = [];
                }

                data[fieldName].push(data[key]);
                delete data[key];
            }
        }

        console.log(data);

    };

    var UNBOUND_KEY = 'ubField';

    var getUnboundFieldName = function (key) {
        //Check for the presence of the unbound identifier
        if (key.indexOf(UNBOUND_KEY)<0) {
            return key;
        }

        var removePoint = key.indexOf(UNBOUND_KEY);
        var lengthToRemove = UNBOUND_KEY.length;

        var newKey = key.substring(0, removePoint);
        return newKey;
    };

    /**
     * The function checks whether the key indicates an unbounded field
     * @param key
     */
    var isUnboundField = function (key) {
        if (key.indexOf(UNBOUND_KEY) >= 0) {
            return true;
        }
        return false;
    };


    FormManager.register('RequiredField', RequiredField);
    FormManager.register('ReadOnlyField', ReadOnlyField);
    FormManager.register('TextFieldValueExtractor', TextFieldValueExtractor);
    FormManager.register('PrintValueToConsole', PrintValueToConsole);
    FormManager.register('DefaultFileUploadPlugin', DefaultFileUploadPlugin);
    FormManager.register('DatePickerPlugin', DatePickerPlugin);
    FormManager.register('FileUpdatePlugin', FileUpdatePlugin);
    FormManager.register('UnboundDataCompiler', UnboundDataCompiler);
});