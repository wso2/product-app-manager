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
Description: A permission which is given as
        PERMISSION:key  , [right]
        The key is used by other components to reference the rule.
        E.g. PERMSSION:get , read
Filename:PermissionRule.java
Created Date: 30/8/2013
 */
public class PermissionRule {
    private String key;
    private String permission;

    public PermissionRule(String key, String permission) {
        this.key = key;
        this.permission = permission;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
