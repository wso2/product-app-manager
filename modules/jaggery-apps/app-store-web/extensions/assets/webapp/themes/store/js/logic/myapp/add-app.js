/*
Description: The following script is used to perform addition of an application
Created Date: 9/1/2014
Filename:add-app.js
 */
$(function(){
   console.info('Loaded add-app logic');
    var ADD_API_URL='/store/resources/webapp/v1/application/new';
    var ADD_API_METHOD='POST';

   var initFormSubmissionLogic=function(){

       $('#btnAddApp').on('click',function(){
            var data=readAppForm();

            //Read the data
            console.info('Creating app with : '+JSON.stringify(data));

           $.ajax({
               type:ADD_API_METHOD,
               url:ADD_API_URL,
               data:data,
               success:function(){
                   console.log('Application was successfully added');
                   handleSuccessfulAppAddition();
               }
           });

       });
   };

   /*
   The function creates a data object containing the application information entered by the user
    */
   var readAppForm=function(){
       var data={};
       data['appName']=$('#appName')?$('#appName').val():'empty';
       data['appTier']= $('#appTier')?$('#appTier').val():'empty';
       data['appCallbackUrl']=$('#appCallbackURL')?$('#appCallbackURL').val():'empty';
       data['appDescription']=$('#appDescription')?$('#appDescription').val():'empty';

       return data;
   };

   /*
   The function is invoked when the application is successfully added
    */
   var handleSuccessfulAppAddition=function(){
       window.location='/store/extensions/assets/webapp/myapps';
   };

   initFormSubmissionLogic();
});