/*
 Description: This module is used to handle the subscriptions of the user.The supported actions include:
 1. Listing the subscription details of each application owned by the user
 2. Listing the subscriptions of each application
 3. Allowing an application to subscribe to an API
 4. Removing the subscription of an application
 */
var serviceModule = (function () {

    var log = new Log('subscriptions');

    function Subscriber() {
        this.instance = null;
    }

    Subscriber.prototype.init = function (context, session) {
        this.instance = context.module('subscription');
    };

    /*
     Allows an application to subscribe to an application
     @options.apiName : The name of the API
     @options.apiVersion: The version of the API
     @options.apiTier : The tier of the API
     @options.apiProvider : The provider of the API
     @options.appName: The name of the application
     @options.user:
     */
    Subscriber.prototype.addSubscription = function (options) {
        
        var apiData = {};
        apiData['name'] = options.apiName;
        apiData['version'] = options.apiVersion;
        apiData['provider'] = options.apiProvider;
        var result = this.instance.addAPISubscription(apiData, options.subscriptionType, options.apiTier, options.appName, options.user, options.enterprises);
        return result;
    };

    Subscriber.prototype.removeSubscription = function (options) {

        var apiData = {};
        apiData['name'] = options.apiName;
        apiData['version'] = options.apiVersion;
        apiData['provider'] = options.apiProvider;
        var result = this.instance.removeAPISubscription(apiData, options.apiTier, options.appName, options.user);

        return result;
    };
    
    Subscriber.prototype.checkSubscriptionWorkflow = function () {

        var result = this.instance.checkSubscriptionWorkflow();
        return result;
    };

    /*
     The function returns all applications that have subscriptions
     options.user: The name of the user whose apps must be returned
     */
    Subscriber.prototype.getAppsWithSubs = function (options) {
        var result = this.instance.getAllSubscriptions(options.user);
        return (result)?result.applications:[];
    };


    Subscriber.prototype.getSubsForApp=function(options){
        var result= this.instance.getAPISubscriptionsForApplication(options.user,"DefaultApplication");
        return (result)?result.subscriptions:[];
    };


    Subscriber.prototype.getSubscription = function(appInfo, appName, subscriptionType, user){

        var result = this.instance.getAPISubscription(appInfo, appName, subscriptionType, user); 

        return result;
    }

    /*
     Returns all available enterprises for the app.
    */
    Subscriber.prototype.getEnterprisesForApplication = function(options){
        var result= this.instance.getEnterprisesForApplication(options.appName, options.ssoProviderName, options.ssoProviderVersion);
        return result;
    }

    return{
        SubscriptionService: Subscriber
    }
})();