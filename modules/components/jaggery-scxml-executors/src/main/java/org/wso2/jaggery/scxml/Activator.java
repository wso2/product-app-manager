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

package org.wso2.jaggery.scxml;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.jaggery.scxml.internal.RealmServiceComponent;

/*
Description:The Activator class used by the OSGI framework to start and stop the bundle
Filename:RealmContext.java
Created Date: 26/8/2013
 */
public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        System.out.println("Activating org.wso2.jaggery.scxml with RealmService enabled");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Stopping org.wso2.jaggery.scxml with RealmService disabled");
    }
}
