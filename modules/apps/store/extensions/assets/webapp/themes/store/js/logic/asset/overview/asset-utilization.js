$(function(){

    console.info('The subscription widget has been loaded');
    var APP_NAME_FIELD='#subsAppName';
    var TIER_FIELD='#subsAppTier';
    var API_URL='/store/resources/webapp/v1/subscription/app';
    var API_UNSUBSCRIPTION_URL='/store/resources/webapp/v1/unsubscription/app';

    $('#btnSubscribe').on('click',function(){
			getAppDetails();
    });
    
    
    $('#btnUnsubscribe').on('click',function(){
    	 removeAppDetails();
   });
    
 

    var getAppDetails=function(){

         if(metadata){
             console.log('Found metadata');
             var appName=getAppName();
             var tier=getTier();

             //Obtain the required information
             var subscription={};
             var apiDetails=metadata.apiAssetData.attributes;
             subscription['apiName']=apiDetails.overview_name;
             subscription['apiVersion']=apiDetails.overview_version;
             subscription['apiTier']=tier;
             subscription['apiProvider']=apiDetails.overview_provider;
             subscription['appName']="DefaultApplication";


             subscribeToApi(subscription);
         }
    };
    
    var removeAppDetails=function(){

        if(metadata){
            console.log('Found metadata');
            var appName=getAppName();
            var tier=getTier();

            //Obtain the required information
            var subscription={};
            var apiDetails=metadata.apiAssetData.attributes;
            subscription['apiName']=apiDetails.overview_name;
            subscription['apiVersion']=apiDetails.overview_version;
            subscription['apiTier']=tier;
            subscription['apiProvider']=apiDetails.overview_provider;
            subscription['appName']="DefaultApplication";


            unsubscribeToApi(subscription);
        }
   };
    

    var getAppName=function(){
       return $(APP_NAME_FIELD)?$(APP_NAME_FIELD).val():'';
    };

    var getTier=function(){
       return $(TIER_FIELD)?$(TIER_FIELD).val():'';
    };

    /*
    The method invokes the API call which will subscribe the provided application to the given api
     */
    var subscribeToApi=function(subscription){
        $.ajax({
           url:API_URL,
           type:'POST',
           data:subscription,
           success:function(response){
        	   if(JSON.parse(response).error == false){
        		   console.info('Successfully subscribed to Web app: '+subscription.apiName);
        		   alert('Succsessfully subscribed to the '+subscription.apiName+' Web App.');
        		   $('#btnUnsubscribe').show();
                   $('#btnSubscribe').hide();
        	   }else{
              		console.info('Error occured in subscribe to web app: '+subscription.apiName);
                   	alert('Error occured in subscribed to the '+subscription.apiName+' Web App.');
               }
           },
           error : function(response) {
      			alert('Error occured in subscribe');
      	   }
        });
    };
    
    
    var unsubscribeToApi=function(subscription){

        $.ajax({
           url:API_UNSUBSCRIPTION_URL,
           type:'POST',
           data:subscription,
           success:function(response){
        	   if(JSON.parse(response).error == false){
               	console.info('Successfully unsubscribed to web app: '+subscription.apiName);
               	alert('Succsessfully unsubscribed to the '+subscription.apiName+' Web App.');
               $('#btnUnsubscribe').hide();
               $('#btnSubscribe').show();
           	   }else{
           		console.info('Error occured in unsubscribe to web app: '+subscription.apiName);
               	alert('Error occured in unsubscribed to the '+subscription.apiName+' Web App.');
           	   }
        	   
           },
           error : function(response) {
   			alert('Error occured in unsubscribe');
   		  }
        });
    };
    
    
  
});