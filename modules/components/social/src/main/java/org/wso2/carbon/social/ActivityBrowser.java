package org.wso2.carbon.social;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ActivityBrowser {
    private static final Log LOG = LogFactory.getLog(ActivityBrowser.class);
    private static final String STREAM_NAME = ActivityPublisher.STREAM_NAME.replaceAll("\\.", "_");
    private Connection conn;

    public List<String> listActivities(String targetId) {
        List<String> result = null;
        Connection connection = getConnection();
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement("SELECT * FROM " + STREAM_NAME + " WHERE 'payload_target.id'=?");// WHERE target.id=?
                statement.setString(1, targetId);
                resultSet = statement.executeQuery();
                result = new ArrayList<String>();
                while (resultSet.next()) {
                    result.add(resultSet.getString("payload_body"));
                }
            } catch (SQLException e) {
                LOG.error("Can't retrieve activities form cassandra.", e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    //ignore
                }
            }
        }

        return result;
    }

    public void makeIndexes(String column) {
        Connection connection = getConnection();
        if (connection != null) {
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate("CREATE INDEX ON " + STREAM_NAME + " ('payload_" + column + "')");
            } catch (SQLException e) {
                LOG.error("Can't create indexes.", e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    //ignore
                }
            }
        }

    }

    public Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
            } catch (ClassNotFoundException e) {
                LOG.error("unable to load Cassandra driver class", e);
            }
            Properties connectionProps = new Properties();
            //TODO: not to hardcore this
            connectionProps.put("user", "admin");
            connectionProps.put("password", "admin");

            try {
                conn = DriverManager.getConnection("jdbc:cassandra://localhost:9160/EVENT_KS?version=2.0.0", connectionProps);
            } catch (SQLException e) {
                LOG.error("Can't create JDBC connection to Cassandra", e);
            }
        }
        return conn;
    }
}
