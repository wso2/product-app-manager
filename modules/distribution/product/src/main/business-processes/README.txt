Work Flow integration for Application manager
=============================================

We can engage business process execution for App management releated operations.
With Application manager 1.0.0 we have added 3 workflow plug points for below operations.
01. User creation process.
02. Application creation process.
03. Subscription process.

If we performed any of above operations with properly configured Application Manager to integrate
web service based workflow executor[which is default ship with APPM 1.0.0],then APP manager will
execute business process workflow for each of above operations. Each business process has 4 main operations.
01. Accept workflow request coming from App manager to BPS.
02. Create human tasks instance in BPS.
03. User interaction to approve workflow. User can log in to "Workflow-Admin" jaggery app which is default ship with APP manager 1.0.0 and approve process.The url for "workflow-admin" jaggery application is "http[s]://ip:port/workflow-admin"
04. Call back to Application manager after workflow execution.


Configure workflow execution
============================
In order to run this we will assume users running Application manager with port offset 0 and Business Process server with port offset 02.

Business Process Server Configurations 
======================================
Set port offset as 2 in carbon.xml file of Business Process Server 3.1.0. Edit following element of carbon.xml file.
       <Offset>2</Offset>

Copy /epr folder in to repository/conf folder of Business Process Server.
Then copy human tasks (archived) files to repository/deployment/server/humantasks folder of Business Process Server.
Then copy business process (archived) files to repository/deployment/server/bpel folder of Business Process Server.
Then start server.
If you wish to change port offset to some other value or set hostname to a different value than localhost [when running BPS and AM in different machines] , then you will need to edit all .epr files available inside repository/conf/epr folder.
In addition to that we need to edit human tasks wsdl files as well. We need this because once human task finished it will call back to workflow. So it 
should aware about exact service location of workflow.
 


Application Manager Configurations
==========================
Edit Application manager configuration file to enable web service based workflow execution. For this we need to edit app-manager.xml located inside repository/conf of Application Manager product.
All work flow related configurations are located inside <WorkFlowExtensions> section. Edit WorkFlowExtensions as follows. Please note that all workflow process services
are running on port 9765 of Business Process Server(as it is running with port offset2). 

	  <ApplicationCreation executor="org.wso2.carbon.apimgt.impl.workflow.ApplicationCreationWSWorkflowExecutor">
           <Property name="serviceEndpoint">http://localhost:9765/services/ApplicationApprovalWorkFlowProcess</Property>
           <Property name="username">admin</Property>
           <Property name="password">admin</Property>
           <Property name="callbackURL">https://localhost:8243/services/WorkflowCallbackService</Property>
      </ApplicationCreation>
    
      <SubscriptionCreation executor="org.wso2.carbon.apimgt.impl.workflow.SubscriptionCreationWSWorkflowExecutor">
           <Property name="serviceEndpoint">http://localhost:9765/services/SubscriptionApprovalWorkFlowProcess</Property>
           <Property name="username">admin</Property>
           <Property name="password">admin</Property>
           <Property name="callbackURL">https://localhost:8243/services/WorkflowCallbackService</Property>
      </SubscriptionCreation>
      
      <UserSignUp executor="org.wso2.carbon.apimgt.impl.workflow.UserSignUpWSWorkflowExecutor">
           <Property name="serviceEndpoint">http://localhost:9765/services/UserSignupProcess</Property>
           <Property name="username">admin</Property>
           <Property name="password">admin</Property>
           <Property name="callbackURL">https://localhost:8243/services/WorkflowCallbackService</Property>
      </UserSignUp>
    </WorkFlowExtensions>

