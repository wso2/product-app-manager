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

import org.wso2.jaggery.scxml.domain.RoleRule;
import org.wso2.jaggery.scxml.domain.StateRule;
import org.wso2.jaggery.scxml.domain.StateTransitionRule;

import java.util.ArrayList;

/*
Description: Stores the permissions and transition rules.
Filename:StateMapping.java
Created Date: 30/8/2013
 */
public class StateMapping {

    private ArrayList<StateRule> stateRules;
    private ArrayList<StateTransitionRule> stateTransitionRules;
    private ArrayList<RoleRule> roleRules;
    private ArrayList<PermissionRule>permissionRules;

    public StateMapping() {
        this.stateRules=new ArrayList<StateRule>();
        this.stateTransitionRules=new ArrayList<StateTransitionRule>();
        this.roleRules=new ArrayList<RoleRule>();
        this.permissionRules=new ArrayList<PermissionRule>();
    }

    public void addPermission(PermissionRule rule){
        this.permissionRules.add(rule);
    }

    public void addRole(RoleRule rule){
        this.roleRules.add(rule);
    }

    public void addStateTransition(StateTransitionRule rule){
        this.stateTransitionRules.add(rule);
    }

    public void addState(StateRule rule){
        this.stateRules.add(rule);
    }

    /*
    The method locates a transition (fromState- > toState) using the
    toState as a search key.
    @toState: A string indicating the next state of the system.
    @return: An array list of transition rules for the toState.
     */
    public ArrayList<StateTransitionRule> findTransition(String toState){

        String searchKey=toState;
        ArrayList<StateTransitionRule>rules=new ArrayList<StateTransitionRule>();

        StateTransitionRule currentTransition;

        //Traverse every single transition
        for(int index=0;index<stateTransitionRules.size();index++){

            currentTransition=stateTransitionRules.get(index);

            if(currentTransition.getKey().equalsIgnoreCase(searchKey)){
                rules.add(currentTransition);
            }
        }

        return rules;
    }

    /*
    The method is used to obtain a permission based on a key
    @key: A key referencing a permission rule.
    @return: The permission if a match is found,else NULL.
     */
    public String getPermission(String key){
        PermissionRule rule;

        for(int index=0;index<permissionRules.size();index++){
              rule=permissionRules.get(index);
              if(rule.getKey().equalsIgnoreCase(key)){
                  return rule.getPermission();
              }
        }

        return null;
    }


}
