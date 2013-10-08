package org.wso2.carbon.social.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.carbon.social.Activity;

import java.util.HashSet;
import java.util.Set;

public class SocialObject {

    private String targetId;
    private Set<SocialObject> subObjects = new HashSet<SocialObject>();
    private Activity activity;
    private int likes;

    public SocialObject(String targetId) {
        this.targetId = targetId;
    }

    public SocialObject(Activity activity) {
        this(activity.getId());
        this.activity = activity;
    }


    @Override
    public String toString() {
        return toJsonElement().toString();
    }

    private JsonElement toJsonElement() {
        JsonObject jsonObject = new JsonObject();
        toJsonElement(jsonObject, this);
        return jsonObject;
    }

    private static void toJsonElement(JsonObject jsonObject, SocialObject obj) {
        if (obj.subObjects.size() > 0) {
            JsonArray attachmentsArr = new JsonArray();
            jsonObject.add("attachments", attachmentsArr);
            for (SocialObject subObject : obj.subObjects) {
                JsonObject body = subObject.activity.getBody();
                toJsonElement(body, subObject);
                attachmentsArr.add(body);
            }
        }
        jsonObject.addProperty("likes", obj.likes);
    }

//    private static SocialObject createSubObject(SocialObject parent, String id) {
//
//    }

    public void add(SocialObject subObject) {
//        Activity subActivity = subObject.activity;
//        if (!subActivity.getBody().getAsJsonObject("object").get("objectType").getAsString().equals("like")) {
//            subObjects.add(subObject);
//        } else {
//            SocialObject subObject = createSubObject(subActivity.getId());
//            if (subObject != null) {
//                subObject.likes++;
//            } else {
//            }
//        }

    }
}
