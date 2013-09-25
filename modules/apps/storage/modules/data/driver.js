/*
 The class provides the template for a database driver with common functionality
 */

var driver = function () {
    var log = new Log('default.driver');
    var utility = require('/modules/utility.js').utility();

    function DBDriver() {
        this.queryProvider = null;
        this.queryTranslator = null;
        this.instance = null; //The instance of the db
    }

    DBDriver.prototype.init = function (options) {
        log.info('init method called');
        utility.config(options, this);
    };

    DBDriver.prototype.connect = function (config) {
        var connectionString = '' || config.connectionString;
        var username = config.username;
        var password = config.password;
        var dbConfig = config.dbConfig || {};

        try {
            log.info('connecting to ' + connectionString);
            this.instance = new Database(connectionString, username, password, dbConfig);
        }
        catch (e) {
            throw e;
        }
    };

    DBDriver.prototype.disconnect = function () {
        this.instance.close();
        log.info('disconnected from db');
    };

    /*
     The function is used to issue a query to the database
     @query: The query to be executed
     @modelManager: An instance of the model manager which is using the db driver
     @cb: An optional callback
     @return: The results of the query after translation
     */
    DBDriver.prototype.query = function (query, schema, modelManager, model, options) {
        //var result=[];
        var options=options||{};
        var isParam=options.PARAMETERIZED||false;
        var result;

        if (isParam) {
            log.info('parameterized query');
            var args = getValueArray(model, schema, query);
            //result = this.instance.query.apply(this.instance, args) || [];
            result=this.instance.query(query,model.uuid,model.content,model.contentType,model.tenantId,model.contentLength);
        }
        else {
            log.info('not parameterized');
            result = this.instance.query(query) || [];
        }

        var processed;
        processed = this.queryTranslator.translate(schema, modelManager, result);


        return processed;
    };

    /*
     The function
     */
    function getValueArray(model, schema, query) {
        var values = [];
        values.push(query);
        var field;
        for (var index in schema.fields) {
            field = schema.fields[index];
            values.push(model[field.name]);
        }

        log.info(values);
        return values;
    }

    return{
        DBDriver: DBDriver
    }
}


