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

package org.wso2.jaggery.scxml.parser;

import org.wso2.jaggery.scxml.domain.StateMapping;

/*
Description:The interfaces defines a state mapping handler
Filename:IStateMappingHandler.java
Created Date: 30/8/2013
 */
public interface IStateMappingHandler {

    /*
    Checks whether a given token is handled by the handler
    @token: The token to be checked
    @return: True if the token is handled,else false
     */
    boolean isHandled(String token);

    /*
    Performs processing on the key-value pair provided based on its association with the token
    @stateMapping: Stores the results of the processing
    @key: An array of Strings containing the key broken down into tokens
    @value:The value of the rule
     */
    void execute(StateMapping stateMapping,String[] key,String value);
}
