package org.wso2.carbon.social;

import com.google.gson.JsonObject;

public class Activity {

    private String id;
    private final JsonObject body;
    private final int timestamp;

    public Activity(String id, JsonObject body, int timestamp) {
        this.id = id;
        this.body = body;
        this.timestamp = timestamp;

        body.addProperty("id", id);
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return body.toString();
    }
}
