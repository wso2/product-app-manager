/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/


package org.wso2.appmanager.test.common.util;

import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.extensions.servers.carbonserver.TestServerManager;

import java.io.IOException;
import java.util.Map;


public class AppMTestServerManager extends TestServerManager {

    public AppMTestServerManager(AutomationContext context, String carbonZip) {
        super(context, carbonZip);
    }

    public AppMTestServerManager(AutomationContext context, int portOffset) {
        super(context, portOffset);
    }

    public AppMTestServerManager(AutomationContext context, String carbonZip, Map<String, String> commandMap) {
        super(context, carbonZip, commandMap);
    }

    public String configureTestServer() throws IOException, AutomationFrameworkException {
        if(this.carbonHome == null) {
            if(this.carbonZip == null) {
                this.carbonZip = System.getProperty("carbon.zip");
            }

            if(this.carbonZip == null) {
                throw new IllegalArgumentException("carbon zip file cannot find in the given location");
            }

            this.carbonHome = this.carbonServer.setUpCarbonHome(this.carbonZip);
            this.configureServer();
        }

        return carbonHome;
    }
}
