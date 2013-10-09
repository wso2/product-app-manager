package org.wso2.carbon.social;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;

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

        //TODO: don't hardcore following.
        String host = "localhost";
        String url = "tcp://" + host + ":" + "7611";
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
}