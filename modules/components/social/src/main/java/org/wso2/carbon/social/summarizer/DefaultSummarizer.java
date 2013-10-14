package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.social.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSummarizer implements Summarizer {

    private static final Log LOG = LogFactory.getLog(DefaultSummarizer.class);
    private Map<String, Activity> activities = new HashMap<String, Activity>();
    private String rootId;

    public DefaultSummarizer(String rootId) {
        this.rootId = rootId;
    }


    @Override
    public void add(Activity activity) {
        activities.put(activity.getId(), activity);
    }

    @Override
    public void summarize(JsonObject root) {
        for (Activity activity : activities.values()) {
            String targetId = activity.getTargetId();
            if (!targetId.equals(rootId)) {
                Activity parent = activities.get(targetId);
                if (parent != null) {
                    attach(activity, parent.getBody());
                } else {
                    LOG.error("activity not summarized (parent '" + targetId + "' is missing) : " + activity);
                }
            } else {
                attach(activity, root);
            }
        }
    }

    private void attach(Activity activity, JsonObject to) {
        JsonElement attachmentsElement = to.get("attachments");
        JsonArray attachments;
        if (attachmentsElement == null) {
            attachments = new JsonArray();
            to.add("attachments", attachments);
        } else {
            attachments = attachmentsElement.getAsJsonArray();
        }
        attachments.add(activity.getBody());
    }
}
