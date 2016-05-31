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

    /**
     * Migrate App Manager database
     * @throws APPMMigrationException
     */
    public void migrateDatabaseTables() throws APPMMigrationException {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            executeSQLScript();
            connection.commit();
            if (log.isTraceEnabled()) {
                log.trace("App Management database tables are created successfully.");
            }
        } catch (SQLException e) {
            ResourceUtil.handleException("Failed to create database tables in App Manager database.", e);
        } catch (Exception e) {
            ResourceUtil.handleException("Error occurred while executing SQL scripts in App Manager database. ", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    ResourceUtil.handleException("Error occurred while closing database connection.", e);
                }
            }
        }
    }

    public void updateAppDefaultVersions(HashMap<String, MigratingWebApp> defaultAppList, String tenantId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String queryToInsertDefaultAppVersions = "INSERT INTO APM_APP_DEFAULT_VERSION ( " +
                "DEFAULT_APP_VERSION, " +
                "APP_NAME , " +
                "APP_PROVIDER,  " +
                "PUBLISHED_DEFAULT_APP_VERSION, " +
                "TENANT_ID ) " +
                "VALUES (?,?,?,?,?)";

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(queryToInsertDefaultAppVersions);
            for (String appName : defaultAppList.keySet()) {
                MigratingWebApp webapp = defaultAppList.get(appName);
                APIIdentifier apiIdentifier = webapp.getId();
                preparedStatement.setString(1, apiIdentifier.getVersion());
                preparedStatement.setString(2, apiIdentifier.getApiName());
                preparedStatement.setString(3, apiIdentifier.getProviderName());
                if (webapp.isPublished()) {
                    preparedStatement.setString(4, webapp.getVersion());
                } else {
                    preparedStatement.setString(4, null);
                }
                preparedStatement.setString(5, tenantId);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            APIMgtDBUtil.closeAllConnections(preparedStatement, connection, null);
        }
    }

    /**
     *
     * @throws Exception
     */
    //org.wso2.carbon.utils.dbcreator.DatabaseCreator.getDatabaseType throws generic Exception
    private void executeSQLScript() throws Exception {
        String databaseType = DatabaseCreator.getDatabaseType(this.connection);
        boolean keepFormat = false;
        if (Constants.DB_TYPE_ORACLE.equals(databaseType)) {
            delimiter = "/";
        }

        String dbscriptName = getDbScriptLocation(databaseType);

        StringBuffer sql = new StringBuffer();
        BufferedReader reader = null;

        try {
            InputStream is = new FileInputStream(dbscriptName);
            reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!keepFormat) {
                    if (line.startsWith("//")) {
                        continue;
                    }
                    if (line.startsWith("--")) {
                        continue;
                    }
                    StringTokenizer st = new StringTokenizer(line);
                    if (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if ("REM".equalsIgnoreCase(token)) {
                            continue;
                        }
                    }
                }
                sql.append(keepFormat ? "\n" : " ").append(line);

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it
                if (!keepFormat && line.indexOf("--") >= 0) {
                    sql.append("\n");
                }
                if ((checkStringBufferEndsWith(sql, delimiter))) {
                    executeSQL(sql.substring(0, sql.length() - delimiter.length()));
                    sql.replace(0, sql.length(), "");
                }
            }
            // Catch any statements not followed by ;
            if (sql.length() > 0) {
                executeSQL(sql.toString());
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    /**
     * Execute given sql query against the database
     * @param sql
     * @throws APPMMigrationException
     */
    private void executeSQL(String sql) throws APPMMigrationException {
        // Check and ignore empty statements
        if ("".equals(sql.trim())) {
            return;
        }

        ResultSet resultSet = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("SQL : " + sql);
            }

            boolean returnedValue;
            int updateCount;
            int updateCountTotal = 0;
            returnedValue = statement.execute(sql);
            updateCount = statement.getUpdateCount();
            resultSet = statement.getResultSet();
            do {
                if (!returnedValue && updateCount != -1) {
                    updateCountTotal += updateCount;
                }
                returnedValue = statement.getMoreResults();
                if (returnedValue) {
                    updateCount = statement.getUpdateCount();
                    resultSet = statement.getResultSet();
                }
            } while (returnedValue);

            if (log.isDebugEnabled()) {
                log.debug(sql + " : " + updateCountTotal + " rows affected");
            }
            SQLWarning warning = connection.getWarnings();
            while (warning != null) {
                log.debug(warning + " sql warning");
                warning = warning.getNextWarning();
            }
            connection.clearWarnings();
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32") || e.getSQLState().equals("42710")) {
                // eliminating the table already exception for the derby and DB2 database types
                if (log.isDebugEnabled()) {
                    log.info("Table Already Exists. Hence, skipping table creation");
                }
            } else {
                ResourceUtil.handleException("Error occurred while executing : " + sql, e);
            }
        } finally {
            if (resultSet != null) {

                try {
                    resultSet.close();
                } catch (SQLException e) {
                    ResourceUtil.handleException("Error occurred while closing result set", e);
                }

            }
        }
    }
}
