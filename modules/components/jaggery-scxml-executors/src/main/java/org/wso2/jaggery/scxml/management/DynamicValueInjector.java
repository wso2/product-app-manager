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

package org.wso2.jaggery.scxml.management;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
Description:Used to inject values which are specific to an executor context such as the asset author
Filename:DynamicValueInjector.java
Created Date: 30/8/2013
 */
public class DynamicValueInjector {
    private Map dynamicValueMap;


    public static final String ASSET_AUTHOR_KEY="{asset_author}";

    public DynamicValueInjector() {
        dynamicValueMap=new HashMap<String,String>();
    }

    public void setDynamicValue(String key,String value){
        dynamicValueMap.put(key,value);
    }

    /*
    Replaces all occurences of the KEY constants defined in this class occuring in
    as string with its value taken from the current context
    @input: A string input that may contain keys to be replaced by values in the injector
    @return: The passed in input string with replaced values.If it does contain any keys then
            it is returned unaltered.
     */
    public String injectValues(String input){

        //Go through all of the keys and perform a replacement
        Iterator<String>keyIterator=dynamicValueMap.keySet().iterator();
        String key;
        String value;

        //Go through all dynamic values
        while(keyIterator.hasNext()){

            //Replace any occurences of keys
            key=keyIterator.next();
            value=(String) dynamicValueMap.get(key);
            input=input.replace(key,value);
        }

        return input;
    }
}
