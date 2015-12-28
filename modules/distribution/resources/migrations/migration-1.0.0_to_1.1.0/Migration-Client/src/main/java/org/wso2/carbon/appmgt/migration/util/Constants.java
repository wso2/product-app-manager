/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.appmgt.migration.util;

public class Constants {

    public static final String VERSION_1_1_0 = "1.1.0";
    public static final String API = "api";

    public static final String CURRENT_TENANT   = "current_tenant";
    public static final String ROLE_RESTRICTIONS = "role_restrictions";

    // Migration client argument property names
    public static final String ARG_MIGRATE_TO_VERSION = "migrateToVersion";
    public static final String ARG_MIGRATE_TENANTS = "tenants";
    public static final String ARG_MIGRATE_ALL = "migrate";

    public static final String DATA_SOURCE_NAME = "DataSourceName";
    public static final String ARG_MIGRATE_DB = "migrateDB";
    public static final String ARG_MIGRATE_REG = "migrateReg";
    public static final String MIGRATION_SCRIPTS_LOCATION = "/migration-1.0.0_to_1.1.0/dbscripts/";

    //database types
    public static final String DB_TYPE_ORACLE = "oracle";
}
