/*

 */
var queryProvider = function () {
    var queryMap = {};
    var log=new Log();
    queryMap['resource'] = {};
    queryMap['resource']['create'] = 'CREATE TABLE resource ( uuid VARCHAR(250), tenantId VARCHAR(250),contentLength INT,contentType INT, content BLOB );';
    queryMap['resource']['insert'] = 'INSERT INTO resource ({1}) VALUES (?,?,?,?,?);';

    //The function checks the schema and returns a query for creating a table in the desired database
    function create(schema) {
        log.info('table name: '+schema.table);
        var query = queryMap[schema.table]['create'];
        return query;
    }

    /*
    The function builds the insert query
     */
    function insert(schema, model) {
        var query = queryMap[schema.table]['insert'];

        var values=[];
        var fields=[];
        var field;

        log.info(schema);

        //Get the list of properties
        for(var index in schema.fields){
            field=schema.fields[index];
            log.info('field: '+field.name);
            values.push(model[field.name]);
            fields.push(field.name);
        }

        var valuesString=values.join(',');
        var fieldsString=fields.join(',');

        query=query.replace('{1}',fieldsString);
        //query=query.replace('{2}',valuesString);

        query='SELECT * FROM resource;';
        return query;
    }

    function select(schema, predicate) {
        var query = 'SELECT {1} FROM {2} {3};';

        return query;
    }

    function selectAll(schema, predicate) {
        var query = 'SELECT * FROM {1}; ';

        return query;
    }

    function checkIfTableExists(schema) {
        var tableName = schema.table.toUpperCase();
        //var tableName='TBLTESTRESOURCE';
        var query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + tableName + "' AND TABLE_SCHEMA='PUBLIC'; ";

        return query;
    }



    return{
        create: create,
        insert: insert,
        select: select,
        selectAll: selectAll,
        checkIfTableExists: checkIfTableExists
    }

};