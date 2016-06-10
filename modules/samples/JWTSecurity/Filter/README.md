#JWT Security Filter Readme


This is a JWT Security filter which can be use to avoid second login page for AppManager users for j2ee secured web applications.
Here we are going to show you how to apply this filter to an application with an example.
```
Prerequisites 
```

1. JWTSecurityFilter source or jar file
Download from - https://github.com/visithamanujaya/JWTSecurityFilter/tree/master

2. Sample web application 
Download from - https://github.com/visithamanujaya/Sample_WebApp_For_JWT_Security_Filter

3. Install AppManager

4. Install Tomcat
```
Configurations
```
Step – 1	

	Build the web application using maven and deploy it in tomcat.

Step – 2
	
	Build the Filter souse and create JWTSecurityFilter.jar Or download the JWTSecurityFilter.jar directly

Step – 3
  
 	Copy  JWTSecurityFilter.jar
	/WEB-INF/lib folder of the sample web application

Step – 4	
	
	Create JWTSecurityConstraints.xml file in WEB-INF/JWTSecurityConstraints.xml
	and define security constraints which are defined in web.xml file of the application. 
	(already created sample JWTSecurityConstraints.xml can be found in source of the filter.)

E.g

A security constraint defined in web.xml

	 <security-constraint>
		<display-name>SecurityConstraint</display-name>
		<web-resource-collection>
		    <web-resource-name>WRCollection</web-resource-name>
		    <url-pattern>/marketing.jsp</url-pattern>
		</web-resource-collection>
		<auth-constraint>
		    <role-name>admin</role-name>
		    <role-name>MarketingOfficer</role-name>
		</auth-constraint>
		<user-data-constraint>
		    <transport-guarantee>NONE</transport-guarantee>
		</user-data-constraint>
	 </security-constraint>

How the above should be defined in CustomData.xml, here you have to consider only about url pattern and the user role.
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<JWTSecurityConstraints>
<SecurityConstraint>
        <urlPattern>/marketing.jsp</urlPattern>
        <authConstraint>
            <roleName>admin</roleName>
	    <roleName>Marketing</roleName>
        </authConstraint>
    </SecurityConstraint>
</JWTSecurityConstraints>
```


Step – 5

	Delete all security constraints and login configurations from web.xml
	
Step – 6

Define Filter and Filter-Mapping in web.xml and save the file.
```
<filter>
    <filter-name>f1</filter-name>
    <filter-class>org.wso2.JWTSecurity.servlets.JWTSecurityFilter</filter-class>
	<init-param>
	<param-name>alias</param-name>
	<param-value>wso2carbon</param-value>
	</init-param>
	<init-param>
	<param-name>trustStorePath</param-name>
	<param-value>/home/visitha/WAT/AppManager/wso2appm-1.2.0-SNAPSHOT/repository/resources/security/client-truststore.jks</param-value>
	</init-param>
	<init-param>
	<param-name>trustStorePassword</param-name>
	<param-value>wso2carbon</param-value>
	</init-param>
</filter>
  <filter-mapping>
    <filter-name>f1</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```
E.g Final web.xml should be like this
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.4"
         xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

    <display-name>hello2_basicauth</display-name>

    <error-page>
        <error-code>403</error-code>
        <location>/Error.jsp</location>
    </error-page>

<filter>
    <filter-name>f1</filter-name>
    <filter-class>org.wso2.JWTSecurity.servlets.JWTSecurityFilter</filter-class>
		 
  </filter>
  <filter-mapping>
    <filter-name>f1</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
</web-app>
  ```      
Step – 7

	Go to AppManager and create corresponding users and their roles then deploy the app from AppManager.

Step – 8

	Log with different users and test the application.
