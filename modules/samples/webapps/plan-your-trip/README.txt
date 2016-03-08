How to :
--------

1) Drop the plan-your-trip.war in the <tomcat_home>/webapp folder, and restart the tomcat server.

2) Enter the http://localhost:<tomcat_port>/plan-your-trip in the browser (E.g. : http://localhost:8080/plan-your-trip). You should be presented the login screen of the plan your trip webapp. You can enter the credentials Username : admin and Password : password to login to the webapp.

3) Start the app manager server if not already started.

4) Navigate to the publisher webapp, by entering the URL : http://localhost:9443/publisher in your browser.

6) Now you need to add in the claims required by the webapp in app manager. These claims are the ones that will be sent back to the webapp via a jwt token for authentication, when accessing the app via app manager, gateway URL. All but one claim used by the app is present in the carbon server. You can add this one by navigating to the carbon server, configurations, claim-configurations and click on http://wso2.org/claims. Add a new claim with the following details.

		Name	FrequentFlyerID
		Claim Uri	http://wso2.org/ffid
		Mapped Attribute (s)	description
		Supported by Default	true
		Required	true
		Read-only	false

Once done. Navigate to the profile of the user, whom will be using app manager to access the app. And add values for the below mentioned schemes.
		- http://wso2.org/claims/streetaddress
		- http://wso2.org/ffid
		- http://wso2.org/claims/telephone


7) Crate an application by following the below details.
	* Name : travelWebapp
	* Context : /travel
	* Version : 1.0.0
	* Transports : http
	* Web App Url : http://localhost:<tomcat_port>/plan-your-trip  (E.g. : http://localhost:8080/plan-your-trip)
	* In the Single "Sign On Configuration" Section, add the following claims.
		- http://wso2.org/claims/streetaddress
		- http://wso2.org/ffid
		- http://wso2.org/claims/telephone
	and click "create" application

8) Navigate to the store, by entering the URL : http://localhost:9443/store in your browser and log in.

9) Click on the travel application and hit the subscribe button. Once done, click on the gateway URL. Once prompted for the credentials, enter the your username and password, and you will be redirected to your application and the values configured for the claims will be displayed.


Building from Source
--------------------

1) Navigate to the source/plan-your-trip folder.

2) Open a console and type "mvn clean install"

3) Once, built the inside the target folder you will find the .war file, for the web application. Drop this jar inside the webapps directory of your servlet container and you are good to go.
