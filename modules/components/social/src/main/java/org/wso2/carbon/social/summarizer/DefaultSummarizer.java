package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.social.Activity;
import org.wso2.carbon.social.SortOrder;

import java.util.*;

public class DefaultSummarizer implements Summarizer {

    private static final Log LOG = LogFactory.getLog(DefaultSummarizer.class);
    private Map<String, Activity> activities = new HashMap<String, Activity>();
    private Map<String, List<Activity>> activityByParent = new HashMap<String, List<Activity>>();
    private String rootId;
    private SortOrder order;

    public DefaultSummarizer(String rootId, SortOrder order) {
        this.rootId = rootId;
        this.order = order;
    }


    @Override
    public boolean add(Activity activity) {
        activities.put(activity.getId(), activity);

        String targetId = activity.getTargetId();
        if (!targetId.equals(rootId)) {
            List<Activity> activityList = activityByParent.get(targetId);
            if (activityList == null) {
                activityList = new ArrayList<Activity>();
                activityByParent.put(targetId, activityList);
            }
            activityList.add(activity);
        } else {
            List<Activity> activityList = activityByParent.get(targetId);
            if (activityList == null) {
                activityList = new ArrayList<Activity>();
                activityByParent.put(rootId, activityList);
            }
            activityList.add(activity);
        }

        return true;
    }

    @Override
    public void summarize(JsonObject root, Map<String, Activity> activities) {
        for (Map.Entry<String, List<Activity>> entry : activityByParent.entrySet()) {
            List<Activity> activityList = entry.getValue();
            String parentId = entry.getKey();
            order.sort(activityList);
            for (Activity activity : activityList) {
                if (parentId.equals(rootId)) {
                    attach(activity, root);
                } else {
                    Activity parent = this.activities.get(parentId);
                    if (parent != null) {
                        attach(activity, parent.getBody());
                    } else {
                        LOG.error("activity not summarized (parent '" + parentId + "' is missing) : " + activity);
                    }
                }
            }
        }

    }

    public Map<String, Activity> getActivities() {
        return Collections.unmodifiableMap(activities);
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
