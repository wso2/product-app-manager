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
import org.wso2.jaggery.scxml.parser.handlers.PermissionHandler;
import org.wso2.jaggery.scxml.parser.handlers.StateTransitionHandler;

import java.util.ArrayList;

/*
Description:The class is used to parse the instructions in the key-value pairs
passed into the executor.
Filename:StateMappingParser.java
Created Date: 29/8/2013
 */
public class StateMappingParser {

    private ArrayList<IStateMappingHandler> mappingHandlers;

    //Used to seperate instructions in the keys
    private static String KEY_TOKEN_SEPERATOR=":";


    public StateMappingParser() {
        this.mappingHandlers=new ArrayList<IStateMappingHandler>();
        initHandlers();
    }

    /*
    Used to register a handler that can pass an instruction in the key
    @handler: A class that implements the IStateMappingHandler
     */
    public void registerHandler(IStateMappingHandler handler){
        mappingHandlers.add(handler);
    }

    /*
    The method is used to pass a provided key value pair
    @key: Of the form [TOKEN]:rule
    @value: A mapping for the rule
    @stateMapping: An object used to store the parsed rules
     */
    public void parse(String key,String value,StateMapping stateMapping){

        //Break the key down into tokens
        String[]keyTokens=getTokenList(key,KEY_TOKEN_SEPERATOR);

        //Run the filters
        useHandlers(keyTokens,keyTokens[0],value,stateMapping);
    }

    /*
    Initializes a set of default filters
     */
    private void initHandlers(){
        registerHandler(new PermissionHandler());
        registerHandler(new StateTransitionHandler());
    }

    /*
    Runs the filters based on the token extracted from the key
     */
    private void useHandlers(String[]tokens,String key,String value,StateMapping stateMapping){

        for(int handlerIndex=0;handlerIndex<mappingHandlers.size();handlerIndex++){

            if(mappingHandlers.get(handlerIndex).isHandled(key)){

                mappingHandlers.get(handlerIndex).execute(stateMapping,tokens,value);

                return;
            }
        }
    }

    /*
    Splits a given input using the provided token seperator
    @return: A string array of tokens split by the token seperator
     */
    private String[] getTokenList(String input,String token){
        return input.split(token);
    }
}
