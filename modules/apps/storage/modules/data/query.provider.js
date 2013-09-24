/*

 */
var queryProvider = function () {
    var queryMap = {};
    queryMap['resource'] = {};
    queryMap['resource']['create'] = 'CREATE TABLE resource ( uuid VARCHAR(250), tenantId VARCHAR(250), content BLOB );';
    queryMap['resource']['insert'] = 'INSERT INTO resource (uuid,tenantId,content) VALUES ({1},{2},{3});';

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