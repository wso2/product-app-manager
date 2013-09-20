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

package org.wso2.jaggery.scxml.parser.handlers;

import org.wso2.jaggery.scxml.domain.PermissionRule;
import org.wso2.jaggery.scxml.domain.StateMapping;
import org.wso2.jaggery.scxml.parser.IStateMappingHandler;

/*
Description:Extracts permission related information
Content of the form key= PERMISSION:name
Filename:PermissionHandler.java
Created Date: 26/8/2013
 */
public class PermissionHandler implements IStateMappingHandler {

    @Override
    public boolean isHandled(String token) {
        if(token.equalsIgnoreCase("PERMISSION")){
            return true;
        }
        return false;
    }

    @Override
    public void execute(StateMapping stateMapping,String[] key,String value) {
        String target=key[1];

        //Create a new permission instance
        PermissionRule rule=new PermissionRule(target,value);

        stateMapping.addPermission(rule);
    }
}
