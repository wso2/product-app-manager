$(function(){

    console.info('The subscription widget has been loaded');
    var APP_NAME_FIELD='#subsAppName';
    var TIER_FIELD='#subsAppTier';
    var API_URL='/store/resources/webapp/v1/subscription/app';

    $('#btnSubscribe').on('click',function(){
         console.log('Hello');
         getAppDetails();
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
             subscription['appName']=appName;

             console.log(subscription);

             subscribeToApi(subscription);
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
           success:function(){
               console.info('Successfully subscribed to API: '+subscription.apiName);
               alert('Succsessfully subscribed to the '+subscription.apiName+' API.');
           }
        });
    };
});