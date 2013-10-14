package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonObject;
import org.wso2.carbon.social.Activity;

public interface Summarizer {

    void add(Activity activity);

    void summarize(JsonObject root);
}
