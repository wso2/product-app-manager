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
import org.wso2.jaggery.scxml.parser.IStateMappingHandler;

/*
Description:Does nothing atm
Filename:StateHandler.java
Created Date: 26/8/2013
 */
public class StateHandler implements IStateMappingHandler {
    @Override
    public boolean isHandled(String token) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(StateMapping stateMapping, String[] key, String value) {
    }
}
