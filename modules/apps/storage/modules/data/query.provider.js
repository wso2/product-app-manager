/*

 */
var queryProvider = function () {
    var queryMap = {};
    var log=new Log();
    queryMap['resource'] = {};
    queryMap['resource']['create'] = 'CREATE TABLE resource ( uuid VARCHAR(250), tenantId VARCHAR(250),fileName VARCHAR(250), contentLength INT,contentType VARCHAR(150), content BLOB );';
    queryMap['resource']['insert'] = 'INSERT INTO resource ({1}) VALUES (?,?,?,?,?,?);';
    queryMap['resource']['select'] = 'SELECT * FROM resource WHERE {1};';

    //The function checks the schema and returns a query for creating a table in the desired database
    function create(schema) {
        var query = queryMap[schema.table]['create'];
        return query;
    }

    /*
    The function builds the insert query
     */
    function insert(schema, model) {
        var query = queryMap[schema.table]['insert'];
        var fields=[];
        var field;

        //Get the list of properties
        for(var index in schema.fields){
            field=schema.fields[index];
            fields.push(field.name);
        }

        var fieldsString=fields.join(',');

        query=query.replace('{1}',fieldsString);
        return query;
    }

    /*
     The function creates a select statement based on the schema and predicate
     */
    function select(schema, predicate) {
        var query = queryMap[schema.table]['select'];

        //We need to take the predicate and create the query
        var whereClause=buildWhereClause(predicate);

        query=query.replace('{1}',whereClause);

        return query;
    }

    /*
    The function is used to create a where clause from the provided predicate
     */
    function buildWhereClause(predicate){
        var clause='';
        var clauseArray=[];
        for(var key in predicate){
            clause=" "+key+"='"+predicate[key]+"' ";
            clauseArray.push(clause);
        }
        return clauseArray.join('AND');
    }

    function selectAll(schema, predicate) {
        var query = 'SELECT * FROM {1}; ';

        return query;
    }

    function checkIfTableExists(schema) {
        var tableName = schema.table.toUpperCase();
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