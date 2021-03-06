# This repository is no longer maintained.

This repository is no longer used for development. Issue reports and pull requests will not be attended.

WSO2 App Manager was created as a mechanism to offer a standalone solution for publishing and managing all aspects of an application and its lifecycle. We currently have this capability within [WSO2 Identity Server (WSO2 IS)](https://github.com/wso2/product-is), and we encourage you to continue to use WSO2 IS to manage your applications.

---

WSO2 App Manager
========================
[![Build Status](https://wso2.org/jenkins/job/product-app-manager/badge/icon)](https://wso2.org/jenkins/job/product-app-manager)

WSO2 App Manager (AppM) is a powerful platform for creating, managing,
consuming and monitoring web/mobile Applications. It combines tried and tested SOA best practices
with modern day Application provisioning, management principles, governing and security
to solve a wide range of enterprise challenges associated with managing many number of
applications (Mobile and Web).

* WSO2 App Manager consists of several loosely coupled modules.
    - Application publisher
    - Application store
    - Application gateway
    - Mobile Device Manager

Application Publisher allows Wab or Mobile application creators/publishers to easily
create and publish Applications. The Applications can be managed with strong
governance model which consists od well-established concepts such as versioning and
life-cycles. Application consumers can user the Application Store module to discover
published Applications and access them with controlled, secure and reliable manner.
AppM also provides Single Sign On (SSO) from end for all the applications provisioned
through the Store. Web gateway is built in to the AppM so that the web calls are
routed to the respective backend web application.


WSO2 App Manager is based on the revolutionary WSO2 Carbon [Middleware a' la carte]
framework. All the major features have been developed as reusable Carbon
components.

To learn more about WSO2 Application Manager please visit <http://wso2.com/products/app-manager/>.

Key Features
=============

* Application publishers:
    - Simple web-based UI for defining Applications(Web and Mobile)
    - Easily modify existing Applications and move them across life cycle states
    - Specify and attach documentation to defined Applications
    - Create new versions of existing Applications
    - Specify SLAs under which each Application is exposed to the consumers
    - Track and monitor Application usage

* Application consumers:
    - Rich web portal to discover published Applications
    - Create applications, subscribe and obtain Application keys
    - Browse documentation and samples associated with each Application
    - Rate Applications and comment on their features, usability and other related aspects
    - Single sign on and pass the authorization information with JWT/SAML

System Requirements
==================================

1. Minimum memory - 1GB
2. Processor      - Core 2 1.2GHz or equivalent at minimum
3. Java SE Development Kit 1.7.0 or higher
4. The Management Console requires you to enable Javascript on the Web browser,
   with MS IE 9 or above. This can be easily achieved by using the default settings of your browser.
5. Maven, which is a build tool, is required to compile and run the sample clients.
   Maven version 3.0 is recommended.
6. To build WSO2 Application Manager from the Source distribution, it is necessary that
   you have JDK 1.7.x version and Maven 3.0.0 or later

Installation & Running
==================================

1. Extract the wso2appm-1.2.0.zip and go to the 'bin' directory
2. Run the wso2server.sh or wso2server.bat as appropriate
3. Application Publisher web application is running at http://localhost:9763/publisher.
   You may login to the Publisher using the default administrator credentials
   (user: admin, pass: admin).
4. Application Store web application is running at http://localhost:9763/store.
   You may login to the Store using the default administrator credentials
   (user: admin, pass: admin).

Documentation
==============

On-line product documentation is available at:
    <https://docs.wso2.com/display/APPM120/WSO2+App+Manager+Documentation>

Support
==================================

WSO2 Inc. offers a variety of development and production support
programs, ranging from Web-based support up through normal business
hours, to premium 24x7 phone support.

For additional support information please refer to <http://wso2.com/support>

For more information on WSO2 App Manager please visit <http://wso2.com/products/app-manager>

Known issues of WSO2 App Manager
========================================

All known issues of WSO2 Application Manager are filed at:

<https://wso2.org/jira/issues/?jql=project%20%3D%20APPM>

Issue Tracker
==================================

Help us make our software better. Please submit any bug reports or feature
requests through the WSO2 JIRA system:
<https://wso2.org/jira/browse/APPM>


Crypto Notice
==================================

   This distribution includes cryptographic software.  The country in
   which you currently reside may have restrictions on the import,
   possession, use, and/or re-export to another country, of
   encryption software.  BEFORE using any encryption software, please
   check your country's laws, regulations and policies concerning the
   import, possession, or use, and re-export of encryption software, to
   see if this is permitted.  See <http://www.wassenaar.org/> for more
   information.

   The U.S. Government Department of Commerce, Bureau of Industry and
   Security (BIS), has classified this software as Export Commodity
   Control Number (ECCN) 5D002.C.1, which includes information security
   software using or performing cryptographic functions with asymmetric
   algorithms.  The form and manner of this Apache Software Foundation
   distribution makes it eligible for export under the License Exception
   ENC Technology Software Unrestricted (TSU) exception (see the BIS
   Export Administration Regulations, Section 740.13) for both object
   code and source code.

   The following provides more details on the included cryptographic
   software:

   * Apache Rampart   : <http://ws.apache.org/rampart/>
   * Apache WSS4J     : <http://ws.apache.org/wss4j/>
   * Apache Santuario : <http://santuario.apache.org/>
   * Bouncycastle     : <http://www.bouncycastle.org/>

--------------------------------------------------------------------------------
(c) Copyright 2016 WSO2 Inc.

