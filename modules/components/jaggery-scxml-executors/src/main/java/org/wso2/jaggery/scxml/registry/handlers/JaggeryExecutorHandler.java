package org.wso2.jaggery.scxml.registry.handlers;

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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.Handler;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.jaggery.scxml.management.DynamicValueInjector;
import org.wso2.jaggery.scxml.management.StateExecutor;
import org.wso2.jaggery.scxml.threading.JaggeryThreadLocalMediator;
import org.wso2.jaggery.scxml.threading.contexts.JaggeryThreadContext;


/*
Description: The handler is run only once in response to been triggered by the JaggeryExecutorFilter.
            It saves the contents of the resource to its path and then changes the permission.
            This handler is triggered by the first put method invocation in the JaggeryTravellingPermssionLifeCycle
            class.
Filename: JaggeryExecutorFilter.java
Created Date: 2/8/2013
 */

public class JaggeryExecutorHandler extends Handler {

    @Override
    public void put(RequestContext requestContext) throws RegistryException {
        super.put(requestContext);


        JaggeryThreadContext context= JaggeryThreadLocalMediator.get();

        if(context!=null){

            StateExecutor stateExecutor=context.getStateExecutor();

            DynamicValueInjector dynamicValueInjector=context.getDynamicValueInjector();

            String toState=context.getToState();

            UserRealm userRealm=context.getUserRealm();

            String path=context.getAssetPath();

            //Update the resource before changing the permissions.
            requestContext.getRepository().put(path,requestContext.getResource());

            //requestContext.getRegistry().put(path,requestContext.getResource());

            stateExecutor.executePermissions(userRealm,dynamicValueInjector,path,toState);

            //Remove the context as we do not want the filter to cause a match again
            JaggeryThreadLocalMediator.unset();
        }

        //Prevent any other handlers from running
        requestContext.setProcessingComplete(true);
    }
}
