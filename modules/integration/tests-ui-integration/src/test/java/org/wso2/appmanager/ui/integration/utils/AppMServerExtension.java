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

package org.wso2.appmanager.ui.integration.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.context.ContextXpathConstants;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;
import org.wso2.carbon.automation.extensions.ExtensionConstants;

import javax.xml.xpath.XPathExpressionException;
import java.io.*;


public class AppMServerExtension extends ExecutionListenerExtension {

    private AppMTestServerManager serverManager;
    private static final Log log = LogFactory.getLog(AppMServerExtension.class);
    private String executionEnvironment;

    public void initiate() {
        try {
            if(getParameters().get(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND) == null) {
                getParameters().put(ExtensionConstants.SERVER_STARTUP_PORT_OFFSET_COMMAND, "0");
            }
            serverManager = new AppMTestServerManager(getAutomationContext(), null, getParameters());
            executionEnvironment =
                    getAutomationContext().getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT);
        } catch (XPathExpressionException e) {
            handleException("Error while initiating test environment", e);
        }
    }

    public void onExecutionStart() {

        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                String carbonHome = serverManager.configureTestServer();
                int portOffset = Integer.parseInt(serverManager.getCommands().get("-DportOffset"));


                changePortOffsets(carbonHome + "/repository/conf/app-manager.xml", portOffset);
                changePortOffsets(carbonHome + "/repository/conf/identity/sso-idp-config.xml", portOffset);

                if(log.isDebugEnabled()){
                    log.debug("Changed port values of app-manager.xml and sso-idp-config.xml for offset " + portOffset);
                }

                serverManager.startServer();
                System.setProperty(ExtensionConstants.CARBON_HOME, carbonHome);
            }
        } catch (Exception e) {
            handleException("Fail to start carbon server ", e);
        }
    }

    public void onExecutionFinish() {
        try {
            if (executionEnvironment.equalsIgnoreCase(ExecutionEnvironment.STANDALONE.name())) {
                serverManager.stopServer();
            }
        } catch (Exception e) {
            handleException("Fail to stop carbon server ", e);
        }
    }

    private static void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new RuntimeException(msg, e);
    }

    private void changePortOffsets(String path, int portOffset) throws IOException {
        String xmlDoc = null;
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            xmlDoc = sb.toString();
            xmlDoc = xmlDoc.replaceAll(":9443", ":" + String.valueOf(9443 + portOffset));
            xmlDoc = xmlDoc.replaceAll(":9763", ":" + String.valueOf(9763 + portOffset));
        } finally {
            br.close();
        }

        BufferedWriter bw = new BufferedWriter( new FileWriter(path));
        bw.write(xmlDoc);
        bw.close( );

    }
}