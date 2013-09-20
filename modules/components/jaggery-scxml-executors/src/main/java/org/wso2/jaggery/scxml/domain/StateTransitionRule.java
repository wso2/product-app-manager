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

package org.wso2.jaggery.scxml.domain;

import java.util.ArrayList;

/*
Description:Records a State transition rule
e.g. STATE_RULE:Production  , publisher: +authorize
Note: DO NOT leave a space between the + operator and the permission.
key= Production ,rule=publisher:+authorize
Filename:StateTransitionRule.java
Created Date: 30/8/2013
 */
public class StateTransitionRule {

    private String key;
    private String rule;
    private ArrayList<PermissionMapping> mappings;

    public StateTransitionRule(String key, String rule) {
        this.key = key;
        this.rule = rule;
    }

    public String getKey() {
        return key;
    }

    public String getRule() {
        return rule;
    }

    public String getRole(){
        String[] keys=rule.split(":");
        //Remove the operator
        return keys[0];
    }

    /*
    Breaks the rule into a list of permission mappings,
    The rule should be of the form [role]:[+/-]permission
    e.g. publisher:+authorize
    The permission should be defined using a PERMISSION key
    @return: An array of permission mappings
     */
    public ArrayList<PermissionMapping> getPermissionList(){

        //Do not pass twice
        if(mappings!=null){
            return mappings;
        }

        //First time passing
        mappings=new ArrayList<PermissionMapping>();

        //Remove the role
        String [] components=this.rule.split(":");
        String [] permissionMappings=components[1].split(",");

        //Create permission mapping instances
        for(int index=0;index<permissionMappings.length;index++){
            mappings.add(new PermissionMapping(permissionMappings[index]));
        }

        //Split the permission list
        return mappings;
    }

    /*
    NOT USED TODO:Remove!
     */
    public PermissionOperators  getOperator(){
        String [] keys=rule.split(":");
        String operator=keys[0].substring(0,1);
        PermissionOperators op=null;

        if(operator=="+"){
            op=PermissionOperators.ADD;
        }
        else if(operator=="-"){
            op=PermissionOperators.SUB;
        }

        return op;
    }
}
