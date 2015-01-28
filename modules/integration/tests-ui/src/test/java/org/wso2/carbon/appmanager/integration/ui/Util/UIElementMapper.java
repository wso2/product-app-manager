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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;


public class UIElementMapper {

    private Properties appProp;

    public UIElementMapper() {
        try {
            appProp = new Properties();
            String propFileName = "mapper.properties";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            appProp.load(inputStream);

            if (inputStream == null) {
                throw new FileNotFoundException("Property file '" + propFileName + "' is not found in the classpath");
            }
        }
        catch(Exception e){
                e.printStackTrace();
        }

    }

    public String getElement(String key) {
        if (appProp != null) {
            return appProp.getProperty(key);
        }
        return null;
    }
}