How to Deploy & User Creation Workflow Sample
=============================================

This sample contains sample BPS workflow source and back end service. This sample workflow accept web service call from
Application Manager(When user creation process strats) and call to sample echo service hosted in stratos Live(https://stratoslive.wso2.com).
This is just example demostrate how we can plug external workflow into user creation process of WSO2 Application Manager. Follow given
instructions to run sample.


Steps:
------

1. Extract wso2appm-xxx.zip (eg: wso2appmanager-1.4.1.zip)

2. Build workflow sample project (Available at samples/BPSWorkFlow/UserCreation/UserCreationProcess) using maven. Goto
   that project location and run "mvn clean install" command. If you want to change edit this service open this project
   with WSO2 Developer Studio.

3. Extract wso2bps-xxx.zip (eg: wso2bps-3.0.0.zip). Start WSO2BPS by executing wso2bps-3.0.0/bin/wso2server.sh

4. Upload business process into WSO2 BPS using management console of WSO2 BPS. Now you will see UserCreationProcess 
   deployed as a service. To verify this step you can invoke web service using embeded tryit user interface.  

5. To enable user add listner we have to change some additional configurations. For that we have to edit app-manager.xml
   file. See following section of that file. Set Business Process Server URL into <ServerURL> section.
   <WorkFlowExtensions>
      <SelfSignIn>
	<!--This propertey used to enable/disable user add listner-->
         <ProcessEnabled>true</ProcessEnabled>
	<!--This propertey used to specify business process server URL-->
         <ServerURL>https://localhost:9444/services/</ServerURL>
        <!--This propertey used provide username/ password if we invoke service in a secured way-->
         <UserName>admin</UserName>
         <Password>admin</Password>
      </SelfSignIn>
    </WorkFlowExtensions>     
      
6. Start WSO2AM by executing wso2appm-1.0.0/bin/wso2server.sh.

7. Add new user and check instances created inside Business Process Server. You will see created instances and web service 
   invocation happens when we add new user into system.

