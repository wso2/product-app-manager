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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.jaggery.scxml.domain.PermissionMapping;
import org.wso2.jaggery.scxml.domain.PermissionOperators;
import org.wso2.jaggery.scxml.domain.StateMapping;
import org.wso2.jaggery.scxml.domain.StateTransitionRule;
import org.wso2.jaggery.scxml.parser.StateMappingParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/*
Description:Changes the permissions of a given asset path.
state to another.
Filename:StateExecutor.java
Created Date: 30/8/2013
 */
public class StateExecutor {

    private StateMapping stateMapping;
    private StateMappingParser parser;

    private static final Log log = LogFactory.getLog(StateExecutor.class);

    public StateExecutor(Map map) {
        this.stateMapping = new StateMapping();
        this.parser = new StateMappingParser();
        initStateMapping(map);
    }

    /*
    The method executes permissions based on the toState on a given path.
    @userRealm: A UserRealm object
    @dynamicValueInjector: A DynamicValueInjector having a list of key values.
    @path: The path of the asset that must be modified
    @toState: The state to which the asset will transition next.
            NOTE: At the point this method is called it has already transitioned to the next state.
     */
    public void executePermissions(UserRealm userRealm, DynamicValueInjector dynamicValueInjector, String path, String toState) {

        PermissionMapping rule;
        String permission;

        //Need to find all state rules that match fromState->toState
        ArrayList<StateTransitionRule> transitionRules = stateMapping.findTransition(toState);

        //Get the current transition
        StateTransitionRule transitionRule;

        //Go through all the transition rules
        for (int transitionIndex = 0; transitionIndex < transitionRules.size(); transitionIndex++) {
            transitionRule = transitionRules.get(transitionIndex);

            //Get the permissions
            ArrayList<PermissionMapping> permissions = transitionRule.getPermissionList();

            //Get the role
            String role = transitionRule.getRole();

            //Replace any dynamic keys for the role string(e.g. private_{asset_author}
            role = dynamicValueInjector.injectValues(role);

            System.out.println("ROLE: " + role);

            //Go through all of the permissions
            for (int index = 0; index < permissions.size(); index++) {

                rule = permissions.get(index);

                //Get the permission
                permission = stateMapping.getPermission(rule.getPermissionKey());

                //Replace any dynamic keys (None implemented atm, although {asset_author} keyword can be
                //used here.
                permission = dynamicValueInjector.injectValues(permission);

                System.out.println("Applying transition: " + rule.getOperator() + " permission: " + permission);

                //Only execute a permission if both a permission and a operator exists.
                if ((rule.getPermissionKey() != null) && (rule.getOperator() != null)) {

                    executePermission(rule.getOperator(), userRealm, role, path, permission);
                }
            }

        }

    }

    /*
    The method determines the type of permission operation to execute based on the permission operator type
    @opType: The operation type
    @user: A UserRealm object
    @role: The role of which the permissions must be modified
    @target: The asset path to which the permission applies
    @rule: The permission to be applied
     */
    private void executePermission(PermissionOperators opType, UserRealm user, String role, String target, String rule) {

        switch (opType) {
            case ADD:
                addPermission(user, role, target, rule);
                break;
            case SUB:
                removePermission(user, role, target, rule);
                break;
            default:
                System.out.println("Ignoring " + opType);
                break;
        }
    }

    /*
    The method adds the given set of permissions (rule) to the target for the role.
    @user: A UserRealm object
    @role: The role of which the permissions must be modified
    @target: The asset path to which the permission applies
     */
    private void addPermission(UserRealm user, String role, String target, String rule) {


        //Do nothing if either the role,target or rule is empty
        if ((role == null) || (target == null) || (rule == null)) {
            return;
        }

        try {
            user.getAuthorizationManager().authorizeRole(role, target, rule);
            System.out.println("Permission " + rule + " ADDED to role: " + role + " for " + target);
        } catch (Exception e) {
            String msg = "Permission " + rule + " could NOT be added to role: " + role + " for " + target;
            System.out.println(msg);

        }

    }

    /*
    The method removes the given set of permissions (rule) to the target for the role
    @user: A UserRealm object
    @role: The role of which the permissions must be modified
    @target: The asset path to which the permission applies
     */
    private void removePermission(UserRealm user, String role, String target, String rule) {

        //Do nothing if either the role,target or rule is empty
        if ((role == null) || (target == null) || (rule == null)) {
            return;

        }
        try {

            user.getAuthorizationManager().denyRole(role, target, rule);
            System.out.println("Permission: " + rule + " REMOVED from role: " + role + " for " + target);
        } catch (Exception e) {
            String msg = "Permission: " + rule + " could NOT be removed from role: " + role + " for " + target;
            System.out.println(msg);
        }

    }

    /*
    The method creates a state mapping object from a Map by first obtaining a list of all keys
    and then iterating through the map.
    @map: The parameter map declared with each executor declaration.
     */
    private void initStateMapping(Map map) {

        Iterator<String> mapKeyIterator = map.keySet().iterator();

        String key;
        String value;

        //Go through all keys in the map object
        while (mapKeyIterator.hasNext()) {

            key = mapKeyIterator.next();
            value = (String) map.get(key);

            //Parse the key value pairs into the state mapping object
            this.parser.parse(key, value, this.stateMapping);
        }
    }


}
