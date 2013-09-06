package org.wso2.jaggery.scxml.registry.filters;

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

import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.jdbc.handlers.RequestContext;
import org.wso2.carbon.registry.core.jdbc.handlers.filters.Filter;
import org.wso2.jaggery.scxml.threading.JaggeryThreadLocalMediator;
import org.wso2.jaggery.scxml.threading.contexts.JaggeryThreadContext;


/*
Description: Determines when the JaggeryExecutorHandler should be run.
            This is determine by checking for the presence of the JaggeryThreadContext
            inside the JaggeryThreadLocalMediator.
Filename: JaggeryExecutorFilter.java
Created Date: 2/8/2013
 */
public class JaggeryExecutorFilter extends Filter {

    @Override
    public boolean handleDelete(RequestContext requestContext) throws RegistryException {
        return false;
    }

    @Override
    public boolean handleGet(RequestContext requestContext) throws RegistryException {
        return false;
    }

    @Override
    public boolean handlePut(RequestContext requestContext) throws RegistryException {

      JaggeryThreadContext jaggeryThreadContext=JaggeryThreadLocalMediator.get();

      if(jaggeryThreadContext!=null){
          return true;
      }

      return false;
    }

    @Override
    public boolean handleMove(RequestContext requestContext) throws RegistryException {
        return super.handleMove(requestContext);
    }

    @Override
    public boolean handlePutChild(RequestContext requestContext) throws RegistryException {
        return false;
    }

    @Override
    public boolean handleImportChild(RequestContext requestContext) throws RegistryException {
        return false;
    }

    @Override
    public boolean handleImportResource(RequestContext requestContext) throws RegistryException {
        return false;
    }
}
