/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.jaggery.scxml.custom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.impl.APIConstants;
import org.wso2.carbon.appmgt.impl.dto.PublishApplicationWorkflowDTO;
import org.wso2.carbon.appmgt.impl.workflow.*;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.governance.registry.extensions.interfaces.Execution;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.internal.RegistryCoreServiceComponent;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.registry.core.session.CurrentSession;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.jaggery.scxml.RealmContext;
import org.wso2.jaggery.scxml.management.DynamicValueInjector;
import org.wso2.jaggery.scxml.management.StateExecutor;
import org.wso2.jaggery.scxml.threading.JaggeryThreadLocalMediator;
import org.wso2.jaggery.scxml.threading.contexts.JaggeryThreadContext;

import java.util.Map;

/*
Description:The executor parses the parameter map defined in the
            registry.xml transition element and creates a JaggeryThreadContext
            which contains information on the permissions to be changed.
            The actual permission change logic runs in the JaggeryExecutorHandler.
Filename: GenericExecutor.java
Created Date: 26/8/2013
 */
public class PublishEventExecutor implements Execution
{
    private static final Log log=LogFactory.getLog(PublishEventExecutor.class);

    private UserRealm userRealm;
    private int tenantId;
    private StateExecutor stateExecutor;

    @Override
    public void init(Map map) {

        obtainTenantId();
        obtainUserRealm();
        this.stateExecutor=new StateExecutor(map);
    }


    /*
    The method performs some logic when ever a state transition takes place
    @requestContext: Contains context data about the transition
    e.g. Registry and Resource
    s: From state
    s2: To state
    @return: True if the execution took place correctly
     */
    @Override
    public boolean execute(RequestContext requestContext, String s, String s2){
        String resourceID = requestContext.getResource().getUUID();
        String appName = null;
        String appVersion = null;
        String appProvider = null;
        String lcState = null;
        String tenantDomain = null;
        String workflowRef= null;

        try{
            //Get the registry
            Registry registry = RegistryCoreServiceComponent.getRegistryService().getGovernanceUserRegistry(CurrentSession.getUser(), - 1234);
            //Load Gov Artifacts
            GovernanceUtils.loadGovernanceArtifacts((UserRegistry) registry);

            GenericArtifactManager artifactManager = new GenericArtifactManager(registry, APIConstants.API_KEY);

            GenericArtifact webappArtifact = artifactManager.getGenericArtifact(resourceID);

            appName = webappArtifact.getAttribute("overview_name");
            appVersion = webappArtifact.getAttribute("overview_version");
            appProvider = webappArtifact.getAttribute("overview_provider");
            tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();

            workflowRef = concatStrings(appName,appVersion,appProvider,tenantDomain);
            lcState = webappArtifact.getLifecycleState();

        }catch (RegistryException e){
            e.printStackTrace();
        }

        WorkflowExecutor appPublishWFExecutor = WorkflowExecutorFactory.getInstance().
                getWorkflowExecutor(WorkflowConstants.WF_TYPE_AM_APP_PUBLISH);

        PublishApplicationWorkflowDTO workflowDTO = new PublishApplicationWorkflowDTO();
        workflowDTO.setStatus(WorkflowStatus.CREATED);
        workflowDTO.setCreatedTime(System.currentTimeMillis());
//        workflowDTO.setTenantDomain(tenantDomain);
//        workflowDTO.setTenantId(tenantId);
        workflowDTO.setExternalWorkflowReference(appPublishWFExecutor.generateUUID());
        workflowDTO.setWorkflowReference(String.valueOf(workflowRef));
        workflowDTO.setWorkflowType(WorkflowConstants.WF_TYPE_AM_APP_PUBLISH);
        workflowDTO.setCallbackUrl(appPublishWFExecutor.getCallbackURL());
        workflowDTO.setAppName(appName);
        workflowDTO.setLcState(lcState);
        workflowDTO.setAppVersion(appVersion);
        workflowDTO.setAppProvider(appProvider);

        try {
            appPublishWFExecutor.execute(workflowDTO);
        } catch (WorkflowException e) {
            log.error("Could not execute Application Publish Workflow", e);
            //throw new APIManagementException("Could not execute Application Publish Workflow", e);
        }

        JaggeryThreadContext jaggeryThreadContext=new JaggeryThreadContext();

        //The path of the asset
        String path=requestContext.getResource().getPath();

        //Used to inject asset specific information to a permission instruction

        DynamicValueInjector dynamicValueInjector=new DynamicValueInjector();

        //Set the asset author key
        dynamicValueInjector.setDynamicValue(DynamicValueInjector.ASSET_AUTHOR_KEY,requestContext.getResource().getAuthorUserName());

        //Execute all permissions for the current state
        //this.stateExecutor.executePermissions(this.userRealm,dynamicValueInjector,path,s2);

        jaggeryThreadContext.setFromState(s);
        jaggeryThreadContext.setToState(s2);
        jaggeryThreadContext.setAssetPath(path);
        jaggeryThreadContext.setDynamicValueInjector(dynamicValueInjector);
        jaggeryThreadContext.setUserRealm(userRealm);
        jaggeryThreadContext.setStateExecutor(stateExecutor);

        JaggeryThreadLocalMediator.set(jaggeryThreadContext);

        return true;
    }

    private String concatStrings(String appName, String appVersion, String appProvider, String tenantDomain){
        StringBuilder sb = new StringBuilder();
        sb.append(appName.concat(":"));
        sb.append(appVersion.concat(":"));
        sb.append(appProvider.concat(":"));
        sb.append(tenantDomain);
        return sb.toString();
    }


    /*
    The method obtains the tenant id from a string tenant id
     */
    private void obtainTenantId(){

        String stringTenantId=PrivilegedCarbonContext.getCurrentContext().getTenantDomain(false);

        try{
            this.tenantId=RealmContext.getRealmService().getTenantManager().getTenantId(stringTenantId);
        }
        catch(Exception e){
            log.debug("Failed to obtain Tenant id");
        }
    }

    /*
    The method is used to obtain the User Realm from the RealmContext
     */
    private void obtainUserRealm(){
        try{
            this.userRealm=RealmContext.getRealmService().getTenantUserRealm(this.tenantId);
        }
        catch(Exception e){
            log.debug("Failed to load User Realm Manager.");
        }
    }




}
