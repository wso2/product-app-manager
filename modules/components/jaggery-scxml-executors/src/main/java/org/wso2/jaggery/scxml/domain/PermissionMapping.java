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

/*
Description:Used to record permission mappings  of the
    form + get , operator: +  and permissionKey: get

    This equate to enable get permissions.

Filename:PermissionMapping.java
Created Date: 30/8/2013
 */
public class PermissionMapping {

    private PermissionOperators operator;
    private String permissionKey;

    /*
    Initializes the permission mapping with a string input
    @input: A string of the form [ +/ -] [key]
            where key is a permission rule.
     */
    public PermissionMapping(String input) {
        init(input);
    }

    public PermissionOperators getOperator() {
        return operator;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    /*
    The method parses a string containing a permission mapping
    into operator and permissionKey
    @input: A string containing a permission mapping of the form:
            + write , where + indicates enable and write is a permission key
            that should be defined prior to the rule.
     */
    private void init(String input){

        //Assuming the role: is removed

        //The first character is the operator
        char charOp=input.charAt(0);

        this.operator=getOpCodeFromChar(charOp);

        //The rest of the string is the permission key
        this.permissionKey=input.substring(1);
    }

    /*
    The method determines the type of permission operation using a character
    @code: A character of the form + or - .All other characters will be ignored
    @return: The type of operation,else NULL.
     */
    private PermissionOperators getOpCodeFromChar(char code){
        PermissionOperators operatorCode=null;
        switch(code){
            case '+':
                operatorCode=PermissionOperators.ADD;
                break;
            case '-':
                operatorCode=PermissionOperators.SUB;
                break;
            default:
                break;
        }

        return operatorCode;
    }
}
