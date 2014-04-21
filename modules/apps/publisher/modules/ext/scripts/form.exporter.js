var meta = {
    use: 'export',
    type: 'form',
    required: ['model', 'template']
};

/*
 Description: Converts the data and template to format that can be used to render a form.
 Filename:form.exporter.js
 Created Dated: 11/8/2013
 */

var module = function () {

    var log = new Log();

    /**
     * The function creates a options data object which will be used
     * to render drop downs
     * @param field  An object containing field information
     * @param fieldTemplate  : An object describing the structure of the field
     * @param data The options data will be added to this object
     */
    var buildOptionsObject = function (field, fieldTemplate, data) {
        //Only apply the logic if the field type is options
        if (!data.isOptions) {
            return;
        }

        var optionData = {};
        var options = csvToArray(fieldTemplate.value || '');
        optionData['selected'] = field.value;

        //Drop the selected value
        optionData['options'] = options.filter(function (element) {
            return element != field.value ? true : false;
        });

        data['optionData'] = optionData;

    };

    /**
     * The function is used to support unbounded tables
     * An unbounded table is treated as one single field containing
     * @param table
     * @param fieldArray
     * @param template   The template used to render an asset
     */
    var addUnboundTable = function (table, fieldArray, template) {
        var tableTemplate = template.getTable(table.name);
        var field;
        var fieldTemplate;
        var fieldToOutput;

        if (tableTemplate.maxoccurs == 'unbounded') {
            var data = {};
            data['isUnboundTable'] = true;
            data['name'] = table.name;
            data['fields'] = [];          //The fields contained with the unbounded table
            data['isRequired'] = false;
            data['isTextBox'] = false;
            data['isTextArea'] = false;
            data['isOptions'] = false;
            data['isOptionsText'] = false;
            data['isDate'] = false;

            data['isReadOnly'] = false;
            data['isEditable'] = true;
            data['isFile'] = false;
            data['columnLabels'] = [];
            data['columnNames'] = [];


            //Go through all the fields in the table
            for (var key in table.fields) {

                fieldToOutput = {};
                field = table.fields[key];
                fieldTemplate = template.getField(table.name, field.name);

                data.columnLabels.push(fieldTemplate.label);
                data.columnNames.push(fieldTemplate.name);

                fillFieldToOutput(fieldToOutput, field, fieldTemplate, table);

                data.fields.push(fieldToOutput);
            }

            create2DFields(data);

            fieldArray.push(data);

            return true;
        }

        return false;
    };

    /**
     * The function creates a 2d map of the fields so it can be rendered
     * easily in a tabular form
     * @param data
     */
    var create2DFields = function (data) {
        var fieldMap = [];
        var fieldRow;

        //var rows = data.fields[0].value.length;
        var rows = getFieldWithMostValues(data);

        //If there are no rows then create an empty row
        if(rows==0){
            createEmptyRow(data);
            return;
        }

        for (var index = 0; index < rows; index++) {

            fieldRow = [];

            //Populate a cloned field instance for each column of the row
            for (var fieldIndex in data.fields) {

                //var fieldValue = data.fields[fieldIndex].value[index];
                var fieldValue = getValueAtIndex(data.fields[fieldIndex], index);
                var clonedField = cloneField(data.fields[fieldIndex]);
                clonedField.value = fieldValue;
                //Give a unique name so the name can be used as an id
                clonedField.name = clonedField.name + 'ubField' + index;
                fieldRow.push(clonedField);
            }

            fieldMap.push(fieldRow);
        }

        data.fields = fieldMap;
    };

    var cloneField = function (field) {
        var clone = {};
        for (var key in field) {
            clone[key] = field[key];
        }

        return clone;
    };

    /**
     * The function locates the field with the most number of values
     * @param data
     * @returns The field which has the most number of values,else the first field
     */
    var getFieldWithMostValues = function (data) {
        var maxField = data.fields[0];
        var numValues;
        for (var index in data.fields) {

            //All fields will contain atleast one value (including the empty fields)
            numValues = getNumOfValues(data.fields[index]);

            if (getNumOfValues(maxField) < numValues) {
                maxField = data.fields[index];
            }
        }

        return getNumOfValues(maxField);
    };

    /**
     * The function inspects the value of a field to determine the number of individual values it contains
     * @param field
     * @returns The number of values contained within the field
     */
    var getNumOfValues = function (field) {
        var count = 0;

        if (!field.value) {
            return count;
        }

        if (field.value instanceof Array) {
            count = field.value.length;
        }
        else{
            //Check if the value is a csv
            count=field.value.split(',').length;
        }

        return count;
    };

    /**
     * The function safely obtains a value at a given index of a field value.If the field is not
     * an array then the immediate value is returned.If the field value is an array then the value
     * at the provided index is returned after a boundary check
     * @param field A field instance with a value
     * @param index The index of the value in the value array
     * @returns The value of the index at specified index if it is an array,else the immediate value of the field
     */
    var getValueAtIndex = function (field, index) {
        var result = ''; //We return an empty value by default

        //Check if the field value is an array
        if (field.value instanceof Array) {

            //Determine if the index is within the bounds of the array
            if (field.value.length >= index) {
                result = field.value[index];
            }
        }
        else {
            //Check if csv

            var valueArray = field.value.split(',');

            if(valueArray[index]){
                result=valueArray[index];
            }
            else{
                result=valueArray;
            }

        }

        return result;
    };


    /**
     * The function creates an empty field row
     * @param data
     */
    var createEmptyRow = function (data) {
        var fieldRow = [];
        var fieldMap = [];

        for (var index in data.fields) {
            data.fields[index].name=data.fields[index].name+'ubField0';
            fieldRow.push(data.fields[index]);
        }

        fieldMap.push(fieldRow);

        data.fields = fieldMap;

    };

    /**
     * The function will create a composite data object based on a field template
     * and a field instance.
     * @param data  The object which will be the composite representation of the field and the field template
     * @param field The field instance to be inspected
     * @param fieldTemplate  The template of the field
     */
    var fillFieldToOutput = function (data, field, fieldTemplate, table) {
        data['name'] = table.name + '_' + field.name;
        data['label'] = (fieldTemplate.label) ? fieldTemplate.label : field.name;
        data['isRequired'] = (fieldTemplate.required) ? true : false;
        data['isTextBox'] = (fieldTemplate.type == 'text') ? true : false;
        data['isTextArea'] = (fieldTemplate.type == 'text-area') ? true : false;
        data['isOptions'] = (fieldTemplate.type == 'options') ? true : false;
        data['isOptionsText'] = (fieldTemplate.type == 'option-text') ? true : false;
        data['isDate'] = (fieldTemplate.type == 'date') ? true : false;

        data['isReadOnly'] = (fieldTemplate.meta.readOnly) ? fieldTemplate.meta.readOnly : false;
        data['isEditable'] = (fieldTemplate.meta.editable) ? fieldTemplate.meta.editable : false;
        data['isFile'] = (fieldTemplate.type == 'file') ? true : false;

        data['value'] = field.value;

        data['valueList'] = csvToArray(fieldTemplate.value || '');

        buildOptionsObject(field, fieldTemplate, data);
    };

    /*
     * Go through each table and extract field data
     */
    function fillFields(table, fieldArray, template) {

        //Handle unbounded table seperately
        if (addUnboundTable(table, fieldArray, template)) {
            return;
        }

        //var username=obtainUserNameFromSession();
        //log.debug('logged in user: '+username);
        //Go through each field
        for each(var field
    in
        table.fields
    )
        {

            //Obtain the field details from the template
            var fieldTemplate = template.getField(table.name, field.name);

            //We ignore the field if it is not in the template
            if (!fieldTemplate) {
                log.debug('Ignoring field: ' + stringify(fieldTemplate));
                return;
            }
            //Ignore the field which is set as hidden
            if (fieldTemplate.meta.hidden == "false") {
                var data = {};

                data['name'] = table.name + '_' + field.name;
                data['label'] = (fieldTemplate.label) ? fieldTemplate.label : field.name;
                data['isRequired'] = (fieldTemplate.required) ? true : false;
                data['isTextBox'] = (fieldTemplate.type == 'text') ? true : false;
                data['isTextArea'] = (fieldTemplate.type == 'text-area') ? true : false;
                data['isOptions'] = (fieldTemplate.type == 'options') ? true : false;
                data['isOptionsText'] = (fieldTemplate.type == 'option-text') ? true : false;
                data['isDate'] = (fieldTemplate.type == 'date') ? true : false;

                data['isReadOnly'] = (fieldTemplate.meta.readOnly) ? fieldTemplate.meta.readOnly : false;
                data['isEditable'] = (fieldTemplate.meta.editable) ? fieldTemplate.meta.editable : false;
                data['isFile'] = (fieldTemplate.type == 'file') ? true : false;

                //log.info(field.name+' = '+stringify(field.value));

                data['value'] = getNormalizedValue(field);//field.value;

                data['valueList'] = csvToArray(fieldTemplate.value || '');

                buildOptionsObject(field, fieldTemplate, data);

                fieldArray.push(data);
            }

        }

        return fieldArray;
    }

    var getNormalizedValue=function(field){
        var value=field.value;

        //Check if the value is an array
        if(field.value instanceof Array){
            value=field.value.join(',');
        }

        return value;
    };

    /*
     The function obtains the currently logged in user from the session
     */
    function obtainUserNameFromSession() {

        var username = 'unknown';
        try {
            username = require('store').server.current(session).username;//.get('LOGGED_IN_USER');
        }
        catch (e) {
            log.debug('Unable to retrieved logged in user from sessions.The following exception was thrown: ' + e);
        }
        return username;
    }

    /*
     * Fills all of the tables except the *(global)
     */
    function fillTables(model, template) {

        var fieldArray = [];

        //Go through each table in the model
        for each(var table
    in
        model.dataTables
    )
        {

            //Ignore if *
            if (table.name != '*') {
                fillFields(table, fieldArray, template);
            }
        }

        log.debug('Fields: ' + stringify(fieldArray));

        return fieldArray;
    }

    /*
     The function converts a string comma seperated value list to an array
     @str: A string representation of an array
     TODO: Move to utility script
     */
    function csvToArray(str) {
        var array = str.split(',');
        return array;
    }

    function fillMeta(model, template) {
        var meta = {};
        meta['shortName'] = template.shortName;
        meta['singularLabel'] = template.singularLabel;
        meta['pluralLabel'] = template.pluralLabel;

        log.debug('Meta: ' + stringify(meta));
        return meta;
    }

    function fillInfo(model) {
        var info = {};

        var field = model.getField('*.id');
        info['id'] = field ? field.getValue() : '';

        field = model.getField('*.lifecycle');
        info['lifecycle'] = field ? field.getValue() : '';

        field = model.getField('overview.version');
        info['version'] = field ? field.getValue() : '';

        field = model.getField('*.lifecycleState');

        info['lifecycleState'] = field ? field.getValue() : '';

        log.debug('Info: ' + stringify(info));

        return info;
    }

    return{
        execute: function (context) {

            log.debug('Entered: ' + meta.type);

            var model = context.model;
            var template = context.template;

            var struct = {};

            //TODO: Move this check outside
            if ((!model) || (!template)) {
                log.debug('Required parameters: ' + meta.required + 'not available to adapter');
                throw 'Required model and template data not present';
            }

            var tables = fillTables(model, template);

            struct['fields'] = tables;
            struct['meta'] = fillMeta(model, template);
            struct['info'] = fillInfo(model);

            log.debug('Leaving: ' + meta.type);

            return struct;

        }
    }
};
