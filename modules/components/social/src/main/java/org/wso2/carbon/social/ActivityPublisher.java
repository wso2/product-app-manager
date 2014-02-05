package org.wso2.carbon.social;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;

import java.util.Properties;
import java.util.UUID;

import static org.wso2.carbon.social.Constants.*;
import static org.wso2.carbon.social.JSONUtil.getNullableProperty;
import static org.wso2.carbon.social.JSONUtil.getProperty;

public class ActivityPublisher {

    private static final Log LOG = LogFactory.getLog(ActivityPublisher.class);

    //this variable is init lazily. use getPublisher method to access.
    private DataPublisher publisher;
    //this variable is init lazily. use getStreamId method to access.
    private String streamId;

    private Properties configuration = new Properties();

    public static String DEFAULT_PORT = "7611";
    public static String DEFAULT_HOST = "localhost";
    public static String PROP_PORT = "port";
    public static String PROP_HOST = "host";
    public static String SYS_PROP_PORTOFFSET ="portOffset";
    /**
     * The method is used to initialize the
     *
     * @param props A Properties object with the configuration details
     */
    public void setConfiguration(Properties props) {
        this.configuration = props;
    }

    public String publish(NativeObject activity) {
        DataPublisher publisher = getPublisher();
        try {
            String streamId = getStreamId(publisher);
            String id = UUID.randomUUID().toString();
            activity.put("id", activity, id);
            String json = JSONUtil.SimpleNativeObjectToJson(activity);
            String contextId = getNullableProperty(activity, CONTEXT_JSON_PROP, ID_JSON_PROP);
            if (contextId == null) {
                contextId = getProperty(activity, TARGET_JSON_PROP, ID_JSON_PROP);
            }


            publisher.publish(streamId, null, null, new Object[]{id, contextId, json});
            return id;
        } catch (Exception e) {
            LOG.error("failed to publish social event.", e);
        }
        return null;
    }

    /**
     * lazy init for DataPublisher.
     *
     * @return DataPublisher for publishing activities.
     */
    private DataPublisher getPublisher() {
        int port = getPort();
        String host = getHost();
        String url = "tcp://" + host + ":" + port;
        String username = "admin";
        String password = "admin";

        if (publisher == null) {
            try {
                publisher = new DataPublisher(url, username, password);
            } catch (Exception e) {
                LOG.error("Can't connect to data publisher", e);
            }
        }
        return publisher;
    }

    /**
     * lazy create stream.
     *
     * @return stream id for publishing activities.
     */
    private String getStreamId(DataPublisher publisher) {
        // stream id is cached in field streamId, if it's not there get it form publisher
        if (streamId == null && publisher != null) {
            try {
                streamId = publisher.findStreamId(STREAM_NAME, STREAM_VERSION);
                if (streamId == null) {
                    try {
                        streamId = publisher.defineStream(STREAM_DEF);
                        new ActivityBrowser().makeIndexes("context.id");
                    } catch (Exception e) {
                        LOG.error("Can't create " + STREAM_NAME + ":" +
                                STREAM_VERSION + " for storing social Activities", e);
                    }
                }
            } catch (Exception e) {
                LOG.error("Can't find " + STREAM_NAME + ":" +
                        STREAM_VERSION + " for storing social Activities", e);
            }
        }
        return streamId;
    }

    /**
     * The method is used to obtain the port used by the Data Publisher.
     * The port will be read from the configuration properties, if it is not present then
     * the default port is used.
     * Any port offsets are applied to the port before been sent back
     *
     * @return An integer value which is the port used by the Data Publisher
     */
    private int getPort() {
        int portOffset = 0; //Assume no portoffset
        String portOffsetString;

        //Check if a port property has been provided in the configuration
        String port = configuration.getProperty(PROP_PORT);

        //Check if the port has been specified,if not then use the default port
        if (port == null) {
            port = DEFAULT_PORT;
        }

        //Check if a port offset is present
        portOffsetString = System.getProperty(SYS_PROP_PORTOFFSET);

        //If the portOffset property is present then pickup the offset and
        //convert to an Integer
        if (portOffsetString != null) {
            portOffset = Integer.parseInt(portOffsetString);
        }

        LOG.info("port offset: "+portOffset+" for port: "+port);
        return Integer.parseInt(port) + portOffset;
    }

    /**
     * The method returns the hostname of the DataPublisher.
     *
     * @return A string host name
     */
    private String getHost() {
        String host = configuration.getProperty(PROP_HOST);

        if (host == null) {
            host = DEFAULT_HOST;
        }

        return host;
    }


}