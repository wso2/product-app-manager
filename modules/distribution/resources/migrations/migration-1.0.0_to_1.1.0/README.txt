Configuration Migration 1.0.0 to 1.1.0
=============================

1. Shutdown App Manager 1.0.0 if it is running.

2. Backup <APPM_1.0.0>/repository/conf folder.

3. Go to backed up configuration folder, open carbon.xml file and do the following changes.

   - Change the product 'Vesion' configuration from '1.0.0' to '1.1.0'
       <Version>1.1.0<Version>

   - Change the Server Key Configuration from 'AM' to to 'AppM'
       <ServerKey>AppM</ServerKey>

   - Change the Server Role Configuration as follows
       <ServerRoles>
          <Role>AppManager</Role>
       </ServerRoles>

4. Go to backed up configuration folder, open app-manager.xml file and do the following changes

   - Change ApiManagerBamUiActivityStreamVersion value under <APIStreamNameDefinition> configuration as follows
       <ApiManagerBamUiActivityStreamVersion>1.1.0</ApiManagerBamUiActivityStreamVersion>

   - Remove below SignUp Configuration, since this has been moved to '/_system/governance/appmgt/applicationdata/sign-up-config.xml' configuration file in  App Manager 1.1.0 Carbon registry.   

	    <!--
		Use this configuration to control the self-sign-up capability in API store.
	    -->
	    <SelfSignUp>
		<!--
		    Enable or disable the self-sign-up feature.
		-->
		<Enabled>true</Enabled>

		<!--
		    Self signed up users should be associated with a suitable subscriber
		    role for them to be able to access the API store portal. This required
		    parameter specifies which role should be used for that purpose. The role
		    specified here must have the '/permission/admin/manage/api/subscribe'
		    permission.
		-->
		<SubscriberRoleName>internal/subscriber</SubscriberRoleName>

		<!--
		    This parameter specifies whether the subscriber role specified above
		    should be created in the local user store or not. This only makes sense
		    when the API subscribers are authenticated against the local user store.
		    That is the local Carbon server is acting as the AuthManager. If a remote
		    Carbon server is acting as the AuthManager, this parameter should be turned
		    off for the local server.
		-->
		<CreateSubscriberRole>true</CreateSubscriberRole>
	    </SelfSignUp>


5. Replace the <APPM_1.1.0>/repository/conf folder with the above backed up and modified conf folder of APPM 1.0.0.


Data Migration 1.0.0 to 1.1.0
=============================

1. Shutdown App Manager 1.0.0 if it is running.

2. Backup your WSO2 Carbon Database(User Store and Registry), App Manager Database, Jaggery Storage database and Social framework database of your App Manager 1.0.0 instance.

3. Execute relevant sql script provided here against your App Manager Manager Database.

4. Now point same WSO2 Carbon Database(User Store and Registry), App Manager Database, Jaggery Storage and Social databases of your APPM 1.0.0 instance to APPM 1.1.0. (Configure <APPM_1.1.0>/repository/datasource/master-datasources.xml to point same databases configured in APPM 1.0.0). 

   Note : If you are using default 'WSO2CarbonDB' as the JDBCPersistenceManager datasource in <APPM_1.0.0>/repository/conf/security/application-authentication.xml, then you need to migrate the data  from the Carbon datasource of APPM_1.0.0 to Carbon datasource of APPM 1.1.0.
   
6. Copy relevant database drivers to <APPM_1.1.0>/repository/components/lib directory.
 
5. Move your synapse configurations in APPM 1.0.0 to APPM_1.1.0. For that, copy and replace  <APPM_1.0.0>/repository/deployment/server/synapse-config/default directory to <APPM_1.1.0>/repository/deployment/server/synapse-config/default. Do not replace _TokenAPI_.xml, _RevokeAPI_.xml and _AuthorizeAPI_.xml files in the default/api/ subdirectory.

6. Start APPM 1.1.0 and Login.

7. Login to admin console as the admin user and navigate to /_system/config/repository/components/org.wso2.carbon.registry/queries
   using registry browser. Then delete all the queries listed under "queries" collection (allTags,latest-apis,resource-by-tag,tag-summary and tagsByMediaTypeAndLifecycle).

8. Restart the server.


Tenant Migration (Only needs to be done if you are migrating a multi-tenanted setup)
====================================================================================

1. Move your tenant synapse configurations to APPM 1.1.0. For that, copy and replace specific folders for tenants(shown as 1,2,...) from <APPM_1.0.0>/repository/tenants/ to <APPM_1.1.0>/repository/tenants. Do not replace _TokenAPI_.xml, _RevokeAPI_.xml and _AuthorizeAPI_.xml files in the default/api subdirectory.

2. Start App Manager 1.1.0.

3. Login to each tenant space as tenant admin.

4. Navigate to /_system/config/repository/components/org.wso2.carbon.registry/queries
   using registry browser. Then delete all the queries listed under "queries" collection (allTags,latest-apis,resource-by-tag,tag-summary and tagsByMediaTypeAndLifecycle).

5. If you need to enable Self Sigh-Up in the tenant store, login to admin console as the tenant admin user, then navigate to /_system/governance/appmgt/applicationdata/sign-up-config.xml and edit the file (Enter Tenant admin information, signup roles etc).

6. Restart the server. 


