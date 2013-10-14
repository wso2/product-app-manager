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

            /*
            if (child.get("verb").getAsString().equals("like")) {
                String actorId = activity.getActorId();

                JsonObject likes = parent.getAsJsonObject("likes");
                if (likes == null) {
                    likes = new JsonObject();
                    parent.add("likes", likes);
                }
                JsonElement totalItems = likes.get("totalItems");
                int totalItemsCount = 0;
                if (totalItems != null) {
                    totalItemsCount = totalItems.getAsInt();
                }

                JsonArray items = addArrIfNot(likes, "items");
                if (likedByMe(items, actorId) < 0) {
                    JsonObject person = new JsonObject();
                    person.add("id", new JsonPrimitive(actorId));
                    items.add(person);

                    likes.add("totalItems", new JsonPrimitive(totalItemsCount + 1));

                    LOG.debug("liked " + parentId + " : " + activity);
                } else {
                    LOG.debug("ignored activity (duplicate like) : " + activity);
                }

            } else if (child.get("verb").getAsString().equals("unlike")) {
                String actorId = activity.getActorId();

                JsonObject likes = parent.getAsJsonObject("likes");
                if (likes == null) {
                    likes = new JsonObject();
                    parent.add("likes", likes);
                }
                JsonElement totalItems = likes.get("totalItems");
                int totalItemsCount = 0;
                if (totalItems != null) {
                    totalItemsCount = totalItems.getAsInt();
                }

                JsonArray items = addArrIfNot(likes, "items");
                int i = likedByMe(items, actorId);
                if (i >= 0) {
                    removeElement(likes, "items", i);
                    likes.add("totalItems", new JsonPrimitive(totalItemsCount - 1));

                    LOG.debug("unliked " + parentId + " : " + activity);
                } else {
                    LOG.debug("ignored activity (unlike without a like) : " + activity);
                }

            } else {
                JsonArray attachments = addArrIfNot(parent, "attachments");
                attachments.add(child);
            }
            */
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
