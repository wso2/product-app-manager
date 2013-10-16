package org.wso2.carbon.social;

import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.social.summarizer.DefaultSummarizer;
import org.wso2.carbon.social.summarizer.Summarizer;
import org.wso2.carbon.social.summarizer.SummarizerFactory;

import java.util.HashMap;
import java.util.Map;

public class ActivitySummarizer {

    private static final Log LOG = LogFactory.getLog(ActivitySummarizer.class);
    private String rootId;

    Map<String, Summarizer> summarizerMap = new HashMap<String, Summarizer>();
    DefaultSummarizer defaultSummarizer;

    public ActivitySummarizer(String rootId, SortOrder order) {
        this.rootId = rootId;
        defaultSummarizer = new DefaultSummarizer(rootId, order);
    }


    public void add(Activity activity) {
        String parentId = activity.getTargetId();
        if (parentId != null) {
            boolean added = false;
            String objectType = activity.getObjectType();
            String key = objectType == null ? activity.getVerb() : objectType;
            Summarizer summarizer = summarizerMap.get(key);
            if (summarizer == null) {
                summarizer = SummarizerFactory.create(key, rootId, summarizerMap);
                if (summarizer != null) {
                    summarizerMap.put(key, summarizer);
                }
            }

            if (summarizer != null) {
                added = summarizer.add(activity);
            }

            if (!added) {
                defaultSummarizer.add(activity);
            }

        } else {
            LOG.error("failed to summarize activity (has no target id) : " + activity);
        }
    }


    public JsonObject summarize() {
        JsonObject root = new JsonObject();
        Map<String, Activity> activities = defaultSummarizer.getActivities();
        for (Summarizer summarizer : summarizerMap.values()) {
            summarizer.summarize(root, activities);
        }
        defaultSummarizer.summarize(root, activities);
        return root;
    }
}
