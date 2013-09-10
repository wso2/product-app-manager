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

import org.wso2.jaggery.scxml.domain.StateMapping;
import org.wso2.jaggery.scxml.domain.StateTransitionRule;
import org.wso2.jaggery.scxml.parser.IStateMappingHandler;

/*
Description:The class extracts state transition rules
The key should be of the form STATE_RULE[INDEX]:state
The state should be a valid state in the life-cycle of an asset
Filename:StateTransitionHandler.java
Created Date: 30/8/2013
 */
public class StateTransitionHandler implements IStateMappingHandler {

    @Override
    public boolean isHandled(String token) {
        //We use contains as the STATE_RULE has an index
        if(token.contains("STATE_RULE"))
        {
            return true;
        }

        return false;
    }

    @Override
    public void execute(StateMapping stateMapping, String[] key, String value) {
        String target=key[1]; //The contents of key: STATE_RULE,state

        StateTransitionRule rule=new StateTransitionRule(target,value);

        stateMapping.addStateTransition(rule);

    }
}
