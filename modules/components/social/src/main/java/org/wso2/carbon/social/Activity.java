package org.wso2.carbon.social;

import com.google.gson.JsonObject;

public class Activity {

    private final JsonObject body;
    private final int timestamp;

    public Activity(JsonObject body, int timestamp) {

        this.body = body;
        this.timestamp = timestamp;
    }

    public JsonObject getBody() {
        return body;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
