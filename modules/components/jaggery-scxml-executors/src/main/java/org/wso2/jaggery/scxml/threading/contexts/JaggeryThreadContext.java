package org.wso2.jaggery.scxml.threading.contexts;

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

import org.wso2.carbon.user.api.UserRealm;
import org.wso2.jaggery.scxml.management.DynamicValueInjector;
import org.wso2.jaggery.scxml.management.StateExecutor;

/*
Description: Encapsulates signalling between the Jaggery Executor
             and the filer
Filename: JaggeryThreadContext.java
Created Date: 3/9/2013
 */
public class JaggeryThreadContext {

    private boolean enableJaggeryPutHandler;   //TODO:Remove!

    private String fromState;
    private String toState;
    private DynamicValueInjector dynamicValueInjector;
    private StateExecutor stateExecutor;
    private UserRealm userRealm;
    private String assetPath;

    public JaggeryThreadContext() {
        this.enableJaggeryPutHandler=false;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public UserRealm getUserRealm() {
        return userRealm;
    }

    public void setUserRealm(UserRealm userRealm) {
        this.userRealm = userRealm;
    }

    public DynamicValueInjector getDynamicValueInjector() {
        return dynamicValueInjector;
    }

    public void setDynamicValueInjector(DynamicValueInjector dynamicValueInjector) {
        this.dynamicValueInjector = dynamicValueInjector;
    }

    public StateExecutor getStateExecutor() {
        return stateExecutor;
    }

    public void setStateExecutor(StateExecutor stateExecutor) {
        this.stateExecutor = stateExecutor;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    public boolean isEnableJaggeryPutHandler() {
        return enableJaggeryPutHandler;
    }

    public void setEnableJaggeryPutHandler(boolean enableJaggeryPutHandler) {
        this.enableJaggeryPutHandler = enableJaggeryPutHandler;
    }
}
