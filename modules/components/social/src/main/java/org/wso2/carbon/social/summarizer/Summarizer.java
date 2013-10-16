package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonObject;
import org.wso2.carbon.social.Activity;

import java.util.Map;

public interface Summarizer {

    boolean add(Activity activity);

    void summarize(JsonObject root, Map<String, Activity> activities);
}
