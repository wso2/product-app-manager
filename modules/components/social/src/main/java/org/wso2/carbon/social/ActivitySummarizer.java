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
    Summarizer defaultSummarizer;

    public ActivitySummarizer(String rootId) {
        this.rootId = rootId;
        defaultSummarizer = new DefaultSummarizer(rootId);
    }


    public void add(Activity activity) {
        String parentId = activity.getTargetId();
        if (parentId != null) {
            String verb = activity.getBody().get("verb").getAsString();
            Summarizer summarizer = summarizerMap.get(verb);
            if (summarizer == null) {
                summarizer = SummarizerFactory.create(verb,rootId);
                if (summarizer == null) {
                    summarizer = defaultSummarizer;
                } else {
                    summarizerMap.put(verb, summarizer);
                }
            }
            summarizer.add(activity);

        } else {
            LOG.error("failed to summarize activity (has no target id) : " + activity);
        }
    }


    public JsonObject summarize() {
        JsonObject root = new JsonObject();
        defaultSummarizer.summarize(root);
        for (Summarizer summarizer : summarizerMap.values()) {
            summarizer.summarize(root);
        }
        return root;
    }
}
