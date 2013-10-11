package org.wso2.carbon.social;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ActivitySummarizer {

    Map<String, JsonObject> objects = new HashMap<String, JsonObject>();
    private static final Log LOG = LogFactory.getLog(ActivityPublisher.class);
    private String rootId;
    private List<Activity> rootActivities = new ArrayList<Activity>();

    public ActivitySummarizer(String rootId) {
        this.rootId = rootId;
    }


    public void add(Activity activity) {
        String parentId = activity.getTargetId();
        if (parentId != null) {
            JsonObject parent = getSubObject(parentId);
            String id = activity.getId();
            JsonObject child = activity.getBody();
            merge(getSubObject(id, child), child);


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
                if (parentId.equals(rootId)) {
                    rootActivities.add(activity);
                } else {
                    JsonArray attachments = addArrIfNot(parent, "attachments");
                    attachments.add(child);
                }
            }
        } else {
            LOG.error("failed to summarize activity (has no target id) : " + activity);
        }
    }

    // needed because http://code.google.com/p/google-gson/issues/detail?id=353
    private void removeElement(JsonObject root, String arrayName, int i) {
        JsonArray oldArray = root.getAsJsonArray(arrayName);
        JsonArray newArray = new JsonArray();
        for (int j = 0; j < oldArray.size(); j++) {
            if (j != i) {
                newArray.add(oldArray.get(j));
            }
        }
        root.add(arrayName, newArray);
    }

    private int likedByMe(JsonArray items, String actorId) {
        for (int i = 0; i < items.size(); i++) {
            JsonElement item = items.get(i);
            if (item.getAsJsonObject().get("id").getAsString().equals(actorId)) {
                return i;
            }
        }
        return -1;
    }


    private JsonArray addArrIfNot(JsonObject root, String arrName) {
        JsonArray attachments = root.getAsJsonArray(arrName);
        if (attachments == null) {
            attachments = new JsonArray();
            root.add(arrName, attachments);
        }
        return attachments;
    }

    public JsonObject summarize(SortOrder order) {
        JsonObject root = new JsonObject();
        JsonArray attachments = addArrIfNot(root, "attachments");

        Collections.sort(rootActivities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getTimestamp() - o2.getTimestamp();
            }
        });
        order.sort(rootActivities);
        for (Activity activity : rootActivities) {
            attachments.add(activity.getBody());
        }
        return root;
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
        return getSubObject(id, null);
    }

    private JsonObject getSubObject(String id, JsonObject obj) {
        JsonObject jsonElement = objects.get(id);
        if (jsonElement == null) {
            jsonElement = obj == null ? new JsonObject() : obj;
            objects.put(id, jsonElement);
        }
        return jsonElement;
    }
}
