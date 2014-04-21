$(function () {

    var ADD_BUTTON_ID = '-add-row';
    var addButtonTemplate = "<input class='btn' id='{{id}}-add-row' type='button' value='Add'>";
    var deleteButtonTemplate = "<input class='btn-danger' type='button'  value='Delete'>";
    var compiledButtonTemplate = Handlebars.compile(addButtonTemplate);

    var DELETE_BUTTON_CELL_OFFSET = 1;

    function UnboundTablePlugin() {

    }

    UnboundTablePlugin.prototype.init = function (element) {
        console.log('Activating the UnboundTablePlugin');
        console.log('*********************************');

        //Get a reference to the table
        var table = $(getTableId(element))[0];

        //Add the add row button to the table
        populateAddButton(table, element);

        //Add a delete button to each row
        addDeleteButtons(table);
    };


    /**
     * The function goes through each row in a table and adds a delete button
     * which can be used to remove the current row
     * @param table
     */
    var addDeleteButtons = function (table) {
        for (var index = 1; index < table.rows.length; index++) {

            //Get the cell which will have the delete button
            var cell = getCellAtXY(table, index, getDeleteButtonCellIndex(table));

            //If the designated cell exists add the delete button
            if (cell) {
                $(cell).html(deleteButtonTemplate);

                $(getElementInCell(cell)).on('click', function () {

                   // var item = $(this)[0];
                   // $(item).closest('tr').remove();
                   // PageFormContainer.getInstance().removeDynamicElement(item.parentNode.parentNode);
                });
            }
        }
    };

    var populateDeleteButton = function (table, rowIndex) {
        var cell = getCellAtXY(table, rowIndex, getDeleteButtonCellIndex(table));

        if (cell) {
            $(cell).html('');
            $(cell).html(deleteButtonTemplate);

            $(getElementInCell(cell)).on('click', function () {
                alert('Delete clicked for new row');

                var item = $(this)[0];
                $(item).closest('tr').remove();
                PageFormContainer.getInstance().removeDynamicElement(item.parentNode.parentNode);
                // console.log(item.parentNode.parentNode);
                // table.deleteRow(item.parentNode);
            });
        }
    };

    /**
     * The function creates an add button which is used to add a new row
     * to the unbounded table
     * @param table
     * @param element
     */
    var populateAddButton = function (table, element) {

        $(getControlContainerId(element)).html(compiledButtonTemplate(element));

        $('#' + element.id + ADD_BUTTON_ID).on('click', function () {

            var table = getTable(element);

            var row = table.rows[2];

            var clonedRow = $(row).clone()[0];

            //populateDeleteButton(row);

            //table.insertRow(clonedRow);

            //table.insertRow()
            $(table).append(clonedRow);

            populateDeleteButton(table, table.rows.length - 1);

            generateUniqueIdsForCellContents(table, clonedRow, table.rows.length);

            PageFormContainer.getInstance().addDynamicElement(clonedRow);
        });
    };

    var getTableId = function (element) {
        return '#' + element.id;
    };

    var getTable = function (element) {
        return $(getTableId(element))[0];
    };

    var getControlContainerId = function (element) {
        return '#' + element.id + '-controls';
    };

    /**
     * The function returns the index of the cell which should contain the delete button.
     * The delete button is always placed in the last cell of a row
     * @param table
     * @return The index of the cell which contains the delete button
     */
    var getDeleteButtonCellIndex = function (table) {

        //Check if there are any cells
        if ((!table.rows) && (!table.rows[0].cells)) {
            console.log('Table is empty');
            return 0;
        }

        var cellCount = table.rows[0].cells.length;
        return cellCount - DELETE_BUTTON_CELL_OFFSET;

    };

    /**
     * The function returns a cell at a given row and column index
     * @param table
     * @param rowIndex
     * @param columnIndex
     * @returns {*}
     */
    var getCellAtXY = function (table, rowIndex, columnIndex) {
        //Get the row
        var row;
        var cell;

        if (!table.rows[rowIndex]) {
            return cell;
        }

        row = table.rows[rowIndex];

        if (!row.cells[columnIndex]) {
            return cell;
        }

        cell = row.cells[columnIndex];

        return cell;
    };

    /**
     * The function returns the element that is within a given cell
     * @param cell
     */
    var getElementInCell = function (cell) {
        return $(cell).children()[0];
    };


    /**
     * The function generates a unique id for each cell element in a row
     * @param row
     * @param index
     */
    var generateUniqueIdsForCellContents = function (table, row, key) {
        //Get all cells
        for (var index = 0; index < row.cells.length; index++) {
            var cell = row.cells[index];
            var element = getElementInCell(cell).children[0];

            if ((element) && (element.id)) {
                var existingId = element.id;
                element.value = '';

                element.id = createUnboundFieldName(existingId, key);
            }

        }
    };

    var UNBOUND_KEY = 'ubField';

    var getUnboundFieldName = function (key) {
        //Check for the presence of the unbound identifier
        if (key.indexOf(UNBOUND_KEY) < 0) {
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

    var createUnboundFieldName = function (key, index) {

        //Extract the unbound field name
        var fieldName = getUnboundFieldName(key);

        //Add the index
        return fieldName + UNBOUND_KEY + index;
    };

    FormManager.register('UnboundTablePlugin', UnboundTablePlugin);
}());