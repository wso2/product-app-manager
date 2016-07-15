---------------------------------------------------------------------------------
	Introduction
---------------------------------------------------------------------------------
This document explains how to setup WSO2 Data Analytics Server to collect and analyze runtime statistics from the APP Manager. 

---------------------------------------------------------------------------------
	Requirements
---------------------------------------------------------------------------------
Configuring WSO2 DAS
Configuring the MySQL database
Uploading the App Manager analytics file
Configuring WSO2 App Manager
Tracking the Web App usage
Viewing the run time statistics

---------------------------------------------------------------------------------
Prerequisites
---------------------------------------------------------------------------------
WSO2 APP Manager (Version - 1.2.0)
WSO2 Data Analytics Server (Version - 3.0.0+)
An RDBMS instance (MySQL, MsSQL, Oracle, H2 etc.) 
org_wso2_carbon_analytics_appm-1.0.0.car file

---------------------------------------------------------------------------------
Configuring WSO2 DAS
---------------------------------------------------------------------------------
Download WSO2 Data Analytics Server from this location: http://wso2.com/products/data-analytics-server/.


If AppM and DAS run on the same machine, open the <DAS_HOME>/repository/conf/carbon.xml file and increase the default service port of DAS by setting the offset value as follows:
<Offset>3</Offset>
This increments all ports used by the server by 3, which means the WSO2 DAS server will run on port 9446. Port offset is used to increment the default port by a given value. It avoids possible port conflicts when multiple WSO2 products run on same host.


Define the datasource declaration according to your RDBMS in the <DAS_HOME>/repository/conf/datasources/master-datasources.xml file.
Note : This DB is used to push the summarized data after analyzing is done by WSO2 DAS. Later, WSO2 App Manager uses this DB to fetch the summary data and display it on the App Manager publisher statistics UI. MySQL databases are used here as an example. However, it is also possible to configure it with H2,Oracle etc. Note that you should always use the WSO2AM_STATS_DB as the datasoure name.


        <datasource>
          <name>WSO2AM_STATS_DB</name>
          <description>The datasource used for setting statistics to APP Manager</description>
          <jndiConfig>
            <name>jdbc/WSO2AM_STATS_DB</name>
            </jndiConfig>
          <definition type="RDBMS">
            <configuration>
              <url>jdbc:mysql://localhost:3306/WSO2APPMSTATS_DB?autoReconnect=true&amp;useSSL=false</url>
              <username>root</username>
              <password>root</password>
              <driverClassName>com.mysql.jdbc.Driver</driverClassName>
              <maxActive>50</maxActive>
              <maxWait>60000</maxWait>
              <testOnBorrow>true</testOnBorrow>
              <validationQuery>SELECT 1</validationQuery>
              <validationInterval>30000</validationInterval>
              <defaultAutoCommit>false</defaultAutoCommit>
              </configuration>
            </definition>
        </datasource>

The autocommit option should be disabled when working with WSO2 DAS. Set this in the JDBC URL or by adding <defaultAutoCommit>false</defaultAutoCommit> under the datasource <configuration> property as shown above:

If you are using MySQL as the database, download and paste the MySQL driver to the <DAS_HOME>/repository/components/lib directory. 


---------------------------------------------------------------------------------
Configuring the MySQL database
---------------------------------------------------------------------------------
Create new database called WSO2APPMSTATS_DB in the mysql DB server.
Execute dbsript/RDBMS.sql script provided inside org_wso2_carbon_analytics_appm-1.0.0.car on the created database.

---------------------------------------------------------------------------------
Uploading the App Manager analytics file
---------------------------------------------------------------------------------
WSO2 DAS uses SparkSQL to analyze the data. All definitions about the data published from WSO2 App Manager and the way it should be analyzed using Spark are shipped to WSO2 DAS as a .car file. 

Start the WSO2 DAS server and log in to the Management Console.
Navigate to the Carbon Applications section under Manage and click Add.
Point to the org_wso2_carbon_analytics_appm-1.0.0.car file and upload it.


---------------------------------------------------------------------------------
Configuring WSO2 APP Manager
---------------------------------------------------------------------------------
Set the following configurations in <AppM_HOME>/repository/conf/app-manager.xml file as follows. Change the default values of the <DASServerURL>, <DASUsername>, and <DASPassword> properties accordingly.
   
<Analytics>

    <UIActivityDASPublishEnabled>true</UIActivityDASPublishEnabled>
    <Enabled>true</Enabled>
    <ThriftPort>7614</ThriftPort>
    <DASServerURL>tcp://localhost:7614</DASServerURL>
    <DASUsername>admin</DASUsername>
    <DASPassword>admin</DASPassword>
    <DataSourceName>jdbc/WSO2AM_STATS_DB</DataSourceName>

</Analytics>

Specify the datasource definition in <APPM_HOME>/repository/conf/datasources/master-datasources.xml file as follows. 


        <datasource>
          <name>WSO2AM_STATS_DB</name>
          <description>The datasource used for setting statistics to APP Manager</description>
          <jndiConfig>
            <name>jdbc/WSO2AM_STATS_DB</name>
            </jndiConfig>
          <definition type="RDBMS">
            <configuration>
              <url>jdbc:mysql://localhost:3306/WSO2APPMSTATS_DB?autoReconnect=true&amp;useSSL=false</url>
              <username>root</username>
              <password>root</password>
              <driverClassName>com.mysql.jdbc.Driver</driverClassName>
              <maxActive>50</maxActive>
              <maxWait>60000</maxWait>
              <testOnBorrow>true</testOnBorrow>
              <validationQuery>SELECT 1</validationQuery>
              <validationInterval>30000</validationInterval>
              </configuration>
            </definition>
        </datasource>

Note :The JNDI name should be matched with the one given in the DAS configurations.

Download and paste the MySQL driver to the <APPM_HOME>/repository/components/lib directory.

