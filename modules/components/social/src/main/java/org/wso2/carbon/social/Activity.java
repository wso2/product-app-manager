package org.wso2.carbon.social;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Activity {

    private final JsonObject body;
    private final int timestamp;

    public Activity(JsonObject body, int timestamp) {
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getId() {
        return body.get("id").getAsString();
    }

    public JsonObject getBody() {
        return body;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return body.toString();
    }

    public String getActorId() {
        return body.getAsJsonObject("actor").get("id").getAsString();
    }

    public String getTargetId() {
        JsonObject target = body.getAsJsonObject("target");
        if(target!=null){
            JsonElement targetId = target.get("id");
            if(targetId!=null){
                return targetId.getAsString();
            }
        }
        return null;
    }

    public void getLikeCount() {
        JsonObject target = body.getAsJsonObject("like");
        if(target!=null){
            JsonElement targetId = target.get("id");
            if(targetId!=null){
//                return targetId.getAsString();
            }
        }

    }
}
