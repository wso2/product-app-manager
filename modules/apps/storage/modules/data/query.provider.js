/*

 */
var queryProvider=function(){

   //The function checks the schema and returns a query for creating a table in the desired database
   function create(schema){

       //Go through each field
       var query='CREATE TABLE {1} ( {2} );';
   }

   function insert(schema,predicate){
       var query='INSERT INTO {1} ( {2} ) VALUES ( {3} );';
   }

   function select(schema,predicate){
       var query='SELECT {1} FROM {2} {3};';
   }

   function selectAll(schema,predicate){
       var query='SELECT * FROM {2}; ';
   }

   function checkIfTableExists(schema){
       var query='IF EXISTS {1} PRINT "true" ELSE PRINT "false";';
   }

   return{
       create:create,
       insert:insert,
       select:select,
       selectAll:selectAll,
       checkIfTableExists:checkIfTableExists
   }

};