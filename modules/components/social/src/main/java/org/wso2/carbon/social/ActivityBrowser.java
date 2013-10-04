package org.wso2.carbon.social;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

import static org.wso2.carbon.social.Constants.*;

public class ActivityBrowser {

    private static final Log LOG = LogFactory.getLog(ActivityBrowser.class);
    public static final String SELECT_CQL = "SELECT * FROM " + STREAM_NAME_IN_CASSANDRA + " WHERE '" + TARGET_ID_COLUMN + "'=?";

    private JsonParser parser = new JsonParser();
    private Connection conn;

    public List<String> listActivities(String targetId) {
        List<Activity> activities = null;
        Connection connection = getConnection();
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SELECT_CQL);
                statement.setString(1, targetId);
                resultSet = statement.executeQuery();
                activities = new ArrayList<Activity>();
                while (resultSet.next()) {
                    JsonElement body = parser.parse(resultSet.getString(BODY_COLUMN));
                    Activity activity = new Activity(body.getAsJsonObject(), resultSet.getInt(TIMESTAMP_COLUMN));
                    activities.add(activity);
                }
            } catch (SQLException e) {
                String message = e.getMessage();
                // we'll ignore the "Keyspace EVENT_KS does not exist" error,
                // this happens when there are 0 activities in bam
                if (!(message.startsWith("Keyspace ") && message.endsWith(" does not exist"))) {
                    LOG.error("Can't retrieve activities form cassandra.", e);
                }
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
        if (activities != null) {
            sortChronologically(activities);
            return serializeEach(activities);
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> serializeEach(List<Activity> activities) {
        List<String> stringList = new ArrayList<String>(activities.size());
        for (Activity activity : activities) {
            stringList.add(activity.getBody().toString());
        }
        return stringList;
    }

    public void sortChronologically(List<Activity> activities) {
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity a1, Activity a2) {
                return a2.getTimestamp() - a1.getTimestamp();
            }
        });
    }

    public void makeIndexes(String column) {
        Connection connection = getConnection();
        if (connection != null) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate("CREATE INDEX ON " + STREAM_NAME_IN_CASSANDRA + " ('payload_" + column + "')");
            } catch (SQLException e) {
                LOG.error("Can't create indexes.", e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
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
