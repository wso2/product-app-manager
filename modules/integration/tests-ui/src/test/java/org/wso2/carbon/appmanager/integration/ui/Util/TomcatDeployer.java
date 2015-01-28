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


package org.wso2.carbon.appmanager.integration.ui.Util;


import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

public class TomcatDeployer {

    public void startTomcat(Tomcat tomcat, String webAppUrl, String webAppPath) throws LifecycleException {
        tomcat.addWebapp(tomcat.getHost(), webAppUrl, webAppPath);
        tomcat.start();
    }

    public Tomcat getTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.getService().setContainer(tomcat.getEngine());
        tomcat.setPort(8080);
        tomcat.setBaseDir(".");

        StandardHost stdHost = (StandardHost) tomcat.getHost();

        stdHost.setAppBase(".");
        stdHost.setAutoDeploy(true);
        stdHost.setDeployOnStartup(true);
        stdHost.setUnpackWARs(true);
        tomcat.setHost(stdHost);

        return tomcat;
    }
}
