/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.appmgt.migration.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.util.Constants;
import org.wso2.carbon.appmgt.migration.util.MigratingWebApp;
import org.wso2.carbon.appmgt.migration.util.ResourceUtil;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.ndatasource.core.CarbonDataSource;
import org.wso2.carbon.ndatasource.core.DataSourceManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.dbcreator.DatabaseCreator;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MigrationDBCreator extends DatabaseCreator {

    private static final Log log = LogFactory.getLog(MigrationDBCreator.class);
    private DataSource dataSource;
    private String delimiter = ";";
    private Connection connection = null;
    Statement statement;

    public MigrationDBCreator(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    /**
     * Get the location of database migration scripts
     * @param databaseType
     * @return
     */
    @Override
    protected String getDbScriptLocation(String databaseType) {
        String scriptName = databaseType + ".sql";
        String resourcePath = CarbonUtils.getCarbonHome() + Constants.MIGRATION_SCRIPTS_LOCATION;
        if (log.isDebugEnabled()) {
            log.debug("Loading database script :" + scriptName);
        }
        return resourcePath + scriptName;
    }
}
