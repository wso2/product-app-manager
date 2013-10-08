package org.wso2.carbon.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActivitySummarizer {

    Map<String, JsonObject> objects = new HashMap<String, JsonObject>();
    private String rootId;

    public ActivitySummarizer(String rootId) {
        this.rootId = rootId;
    }

    public void add(Activity activity) {
        String parentId = activity.getBody().getAsJsonObject("target").get("id").getAsString();
        JsonObject parent = getSubObject(parentId);
        JsonObject child = getSubObject(activity.getId());
        merge(activity.getBody(), child);

        if (child.get("verb").getAsString().equals("like")) {

            JsonObject likes = parent.getAsJsonObject("likes");
            if (likes == null) {
                likes = new JsonObject();
                parent.add("likes", likes);
            }
            JsonElement totalItems = likes.get("totalItems");
            if (totalItems == null) {
                likes.add("totalItems", new JsonPrimitive(1));
            } else {
                likes.add("totalItems", new JsonPrimitive(totalItems.getAsInt() + 1));
            }

            JsonArray items = addArrIfNot(likes, "items");

            JsonObject person = new JsonObject();
            person.add("id",new JsonPrimitive("somewirdname"));
            items.add(person);
        } else {
            JsonArray attachments = addArrIfNot(parent, "attachments");
            attachments.add(child);
        }
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
