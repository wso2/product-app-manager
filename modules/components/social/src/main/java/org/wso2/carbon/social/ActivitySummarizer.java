package org.wso2.carbon.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActivitySummarizer {

    Map<String, JsonObject> objects = new HashMap<String, JsonObject>();
    private static final Log LOG = LogFactory.getLog(ActivityPublisher.class);
    private String rootId;

    public ActivitySummarizer(String rootId) {
        this.rootId = rootId;
    }

    public void add(Activity activity) {
        String parentId = activity.getTargetId();
        if (parentId != null) {
            JsonObject parent = getSubObject(parentId);
            JsonObject child = getSubObject(activity.getId());
            merge(activity.getBody(), child);

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
                if (!likedByMe(items, actorId)) {
                    LOG.debug("ignored activity (duplicate like) : " + activity);

                    JsonObject person = new JsonObject();
                    person.add("id", new JsonPrimitive(actorId));
                    items.add(person);

                    likes.add("totalItems", new JsonPrimitive(totalItemsCount + 1));
                }

            } else {
                JsonArray attachments = addArrIfNot(parent, "attachments");
                attachments.add(child);
            }
        } else {
            LOG.error("failed to summarize activity (has no target id) : " + activity);
        }
    }

    private boolean likedByMe(JsonArray items, String actorId) {
        for (int i = 0; i < items.size(); i++) {
            JsonElement item = items.get(i);
            if (item.getAsJsonObject().get("id").getAsString().equals(actorId)) {
                return true;
            }
        }
        return false;
    }


    private JsonArray addArrIfNot(JsonObject root, String arrName) {
        JsonArray attachments = root.getAsJsonArray(arrName);
        if (attachments == null) {
            attachments = new JsonArray();
            root.add(arrName, attachments);
        }
        return attachments;
    }

    public JsonObject get() {
        return objects.get(rootId);
    }

    private void merge(JsonObject form, JsonObject to) {
        Set<Map.Entry<String, JsonElement>> entries = form.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            String key = entry.getKey();
            JsonElement jsonElement = to.get(key);
            if (jsonElement == null) {
                to.add(key, entry.getValue());
            }
        }
    }

    private JsonObject getSubObject(String id) {
        JsonObject jsonElement = objects.get(id);
        if (jsonElement == null) {
            jsonElement = new JsonObject();
            objects.put(id, jsonElement);
        }
        return jsonElement;
    }
}
