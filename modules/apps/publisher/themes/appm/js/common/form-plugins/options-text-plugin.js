/**
 * Description: The following script is used to render an options text field as it appears in the Registry UI
 *              The plugin handles the following actions;
 *              1. When the page is loaded any fields using this plugin will be populated with the
 *                 options text template
 *              2. If there are any previously selected values they will be taken from the data-selected attribute
 *
 * Filename:options-text-plugin.js
 */
$(function () {

    var BUTTON_ID = 'optionsTextAddButton';
    var TABLE_ID = 'optionsTextTable';
    var TABLE_CSS = 'options-table';
    var BUTTON_ACTIVATE_EVENT = 'click';
    var CELL_INDEX_SELECT = 0;
    var CELL_INDEX_TEXT = 1;
    var CELL_INDEX_BUTTON = 2;

    var optionMasterLayout = "<div><input id='" + BUTTON_ID + "{{id}}' type='button' value='{{addButtonName}}' class='btn' />"
        + "<table id='" + TABLE_ID + "{{id}}' class='" + TABLE_CSS + "'></table>"
        + "</div>";
    var optionsSelectLayout = "<select><option selected>{{selectedOption}}</option>{{#each options}} <option>{{this}}</option> {{/each}}</select>";
    var optionsDeleteButtonLayout = "<input class='btn' type='button' value='Delete' />";
    var optionsTextfieldLayout = "<input type='text' value='{{selectedText}}' />";

    var compiledOptionMasterLayout = Handlebars.compile(optionMasterLayout);
    var compiledSelectLayout = Handlebars.compile(optionsSelectLayout);
    var compiledDeleteButtonLayout = Handlebars.compile(optionsDeleteButtonLayout);
    var compiledTextfieldLayout = Handlebars.compile(optionsTextfieldLayout);

    function OptionsText() {
    }

    /**
     * The function will render the options text field ,If there are any selected values they will also
     * be rendered
     * @param element The field to which the plugin is applied
     */
    OptionsText.prototype.init = function (element) {
        var compiledTemplate = compiledOptionMasterLayout({id: element.id, addButtonName: element.meta.addButton});

        //Add the basic template
        $('#' + element.id).html(compiledTemplate);

        //Wait till the table has been added
        var table = $(getTableId(element))[0];

        //Attach the add  row event
        $(getAddButtonId(element)).on(BUTTON_ACTIVATE_EVENT, function () {
            //Create a new row in the table
            createNewOptionTableRow(table, element);
        });

        //Add any rows for existing data
        createExistingTableRow(table, element);
    };


    /**
     * The function returns the id of the add button
     * @param elementId The field managed by the plugin
     */
    var getAddButtonId = function (element) {
        return '#' + BUTTON_ID + element.id;
    };


    /**
     * The function returns the id of the table used in the options text widget
     * @param element The field managed by the plugin
     * @returns The id of the table
     */
    var getTableId = function (element) {
        return '#' + TABLE_ID + element.id;
    };

    /**
     * The function renders a single empty table row
     * @param table
     */
    var createNewOptionTableRow = function (table, element) {
        var selectData = getDataForNewRow(element);
        createRow(table, selectData, '');
    };

    /**
     * The function renders a row that has already been created.It obtains the data of the already created
     * row from the selected data attribute
     * @param table  The table in which the new row will be created
     * @param element The field which is managed by the plugin
     */
    var createExistingTableRow = function (table, element) {
        var data = getExistingRows(table, element);
        var selectionOptions = getDataForNewRow(element);

        for (var index in data) {
            selectionOptions.selectedOption = data[index].selected;
            createRow(table, selectionOptions, data[index].text);
        }
    };

    /**
     * The function creates an object with all existing option text rows.
     * The exisitng rows are read from the selected data attribute and should contain data in a csv list
     * with each element been a key value pair (selection and text)
     * @param table
     * @param element
     * @returns {Array}
     */
    var getExistingRows = function (table, element) {
        var selected = element.meta.selected;
        selected = selected.split(',');
        var entry;
        var data = [];

        for (var index in selected) {
            entry = selected[index].split(':');
            data.push({selected: entry[0], text: entry[1]});
        }

        return data;
    };

    /**
     * The function creates an empty row in the provided table and fills it with the
     * data provided
     * @param table The table in which the new row will be created
     * @param selectData The select options
     * @param selectText The text to be placed
     */
    var createRow = function (table, selectData, selectText) {
        var row = table.insertRow(-1);
        var cellSelect = row.insertCell(CELL_INDEX_SELECT);
        var cellText = row.insertCell(CELL_INDEX_TEXT);
        var cellButton = row.insertCell(CELL_INDEX_BUTTON);

        cellSelect.innerHTML = compiledSelectLayout(selectData);
        cellText.innerHTML = compiledTextfieldLayout({selectedText: selectText});
        cellButton.innerHTML = compiledDeleteButtonLayout();

        //Connect the delete button which will remove the current row
        $(cellButton).on(BUTTON_ACTIVATE_EVENT, function (event) {
            var rowIndex = event.currentTarget.parentNode.rowIndex;
            var tableNode = event.currentTarget.parentNode.parentNode;

            tableNode.deleteRow(rowIndex);
        });
    };

    /**
     * The function obtains the data for a new row
     * @param element
     * @returns {{}}
     */
    var getDataForNewRow = function (element) {
        var data = {};
        var options = element.meta.options.split(',') || [];
        data.selectedOption = options[0];

        data.options = options;

        //Remove the selected option from the available options
        data.options.filter(function (item) {
            return(item == data.selectedOption) ? true : false;
        });

        return data;
    };

    /**
     * The function collects all user entered data into one object
     * @param element
     * @returns An array of strings containing the inputs for the rows as selection: text
     */
    var getUserEnteredData = function (element) {
        var table = $(getTableId(element))[0];
        var row;
        var data = [];

        //Go through each row and extract the data and send as a comma separated value
        for (var index = 0; index < table.rows.length; index++) {
            row = table.rows[index];
            data.push(getDataInRow(row));
        }

        return data.join(',');
    };

    /**
     * The function obtains all of the  user entered data for a given row
     * @param row  A single row
     * @returns A string representation of the contents of the row (e.g. form of selection: text)
     */
    var getDataInRow = function (row) {
        var cellSelection = row.cells[CELL_INDEX_SELECT];
        var cellText = row.cells[CELL_INDEX_TEXT];

        cellSelection = $(cellSelection).children()[0];
        cellText = $(cellText).children()[0];

        var selectedValue = $(cellSelection).val();
        var textValue = $(cellText).val();

        if ((selectedValue == '') || (textValue == '')) {
            return '';
        }

        return selectedValue + ':' + textValue;
    };


    /**
     * The function will obtain all of the values that have been selected by the user
     * @returns A data object containing the input of the user for the managed field
     */
    OptionsText.prototype.getData = function (element) {
        var data = {};
        var result = getUserEnteredData(element);
        data[element.id] = result;
        return data;
    };

    FormManager.register('OptionsText', OptionsText);
});