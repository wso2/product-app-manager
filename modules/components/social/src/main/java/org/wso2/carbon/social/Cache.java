package org.wso2.carbon.social;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.ndatasource.core.CarbonDataSource;
import org.wso2.carbon.ndatasource.core.DataSourceManager;

import javax.sql.DataSource;
import java.sql.*;

public class Cache {

    private static final String TABLE_NAME = "SOCIAL_CACHE";

    private static final Log LOG = LogFactory.getLog(Cache.class);
    private Connection connection = null;

    public void put(String id, String tenant, JsonObject obj) {
        if (connection == null) {
            init();
        }

        String insertTableSQL = "MERGE INTO " + TABLE_NAME + "(id,tenant ,type , body , rating) VALUES(?,?,?,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(insertTableSQL);
            statement.setString(1, id);
            statement.setString(2, tenant);
            statement.setString(3, id.substring(0, id.indexOf(':')));
            statement.setString(4, obj.toString());
            JsonElement ratingJSON = obj.get("rating");
            Number rating = ratingJSON == null ? 0 : ratingJSON.getAsNumber();
            statement.setDouble(5, rating.doubleValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("could not cache WSO2 Social object", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private void executeUpdate(String sql) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("could not cache WSO2 Social object", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public boolean tableExists() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + TABLE_NAME + "' AND TABLE_SCHEMA='PUBLIC'; ";
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    }

    public void init() {
        try {
            CarbonDataSource carbonDataSource = DataSourceManager.getInstance().getDataSourceRepository().getDataSource("SOCIAL_CACHE");
            DataSource dataSource = (DataSource) carbonDataSource.getDSObject();
            connection = dataSource.getConnection();
            if (!tableExists()) {
                executeUpdate("CREATE TABLE " + TABLE_NAME + " (id VARCHAR(255) NOT NULL, tenant VARCHAR(255),type VARCHAR(255), body VARCHAR(5000), rating DOUBLE,  PRIMARY KEY ( id ))");
                LOG.info("table '" + TABLE_NAME + "' created for storing WSO2 Social object cache");
            }
        } catch (DataSourceException e) {
            LOG.error("could not initialize database for WSO2 Social object caching", e);
            connection = null;
        } catch (SQLException e) {
            LOG.error("could not open a DB connection for for WSO2 Social object caching", e);
            connection = null;
        }
    }
}
