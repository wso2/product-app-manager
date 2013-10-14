package org.wso2.carbon.social;

import com.google.gson.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

import static org.wso2.carbon.social.Constants.*;

public class ActivityBrowser {

    private static final Log LOG = LogFactory.getLog(ActivityBrowser.class);
    public static final String SELECT_CQL = "SELECT * FROM " + STREAM_NAME_IN_CASSANDRA + " WHERE '" +
            CONTEXT_ID_COLUMN + "'=?";

    private JsonParser parser = new JsonParser();
    private Connection conn;
    private Cache cache = new Cache();

    public double getRating(String targetId) {
        int totalRatings = 0;
        int numRatings = 0;

        JsonObject socialObject = getSocialObject(targetId, null);
        JsonArray attachments = socialObject.get("attachments").getAsJsonArray();

        for (JsonElement r : attachments) {
            JsonElement ratingElm = r.getAsJsonObject().getAsJsonObject("object").get("rating");
            if (ratingElm != null) {
                numRatings++;
                totalRatings += ratingElm.getAsInt();
            }
        }
        if (numRatings == 0) {
            return 0;
        } else {
            return ((double) totalRatings) / numRatings;
        }
    }

    public JsonObject getSocialObject(String targetId, SortOrder order) {
        List<Activity> activities = listActivitiesChronologically(targetId);
        ActivitySummarizer summarizer = new ActivitySummarizer(targetId);
        for (Activity activity : activities) {
            summarizer.add(activity);
        }
        JsonObject summarize = summarizer.summarize(order);
        cache.put(targetId, summarize);
        return summarize;
    }

    public List<Activity> listActivities(String contextId) {
        List<Activity> activities = null;
        Connection connection = getConnection();
        if (connection != null) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SELECT_CQL);
                statement.setString(1, contextId);
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
                // this happens when there are 0 activities in Cassandra.
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
            return activities;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Activity> listActivitiesChronologically(String contextId) {
        List<Activity> activities = listActivities(contextId);
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity a1, Activity a2) {
                return a1.getTimestamp() - a2.getTimestamp();
            }
        });
        return activities;
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
