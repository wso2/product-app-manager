#JWT Security Valve for tomcat Readme


This is a JWT Security valve which can be use to avoid second login page for AppManager users for j2ee secured web applications.
Here we are going to show you how to apply this filter to an application with an example.
```
Prerequisites 
```

1. JWTSecurityValve source or jar file
Download from - https://github.com/visithamanujaya/JWTSecurityFilter/tree/master

2. Sample web application 
Download from - https://github.com/visithamanujaya/Sample_WebApp_For_JWT_Security_Filter

3. Install AppManager

4. Install Tomcat
```
Configurations
```
Step – 1	

Copy Realm.jar in to Tomcat lib folder.

Step – 2
	
Edit the context.xml in tomcat and add the valve as shown below. 
Add an alias to refer the public key in the key store, trust store path to get the key store and trust store password.
```
<Context>
    <Valve className="org.wso2.tomcat.authenticator.JWTAuthenticatorValve" alias="wso2carbon"  trustStorePath = "/home/visitha/WAT/AppManager/wso2appm-1.2.0-SNAPSHOT/repository/resources/security/client-truststore.jks" trustStorePassword = "wso2carbon"/>
</Context>
```
Step – 3

	Go to AppManager and create corresponding users and their roles then deploy the app from AppManager.

Step – 4

	Log with different users and test the application.
