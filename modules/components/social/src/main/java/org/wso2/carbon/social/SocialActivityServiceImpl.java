package org.wso2.carbon.social;

import com.google.gson.JsonObject;
import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.service.SocialActivityService;

import java.util.List;
import java.util.Properties;

public class SocialActivityServiceImpl implements SocialActivityService {


    private ActivityPublisher activityPublisher = new ActivityPublisher();
    private ActivityBrowser activityBrowser = new ActivityBrowser();

    @Override
    public void configPublisher(NativeObject configObject) {
        activityPublisher.parseJSONConfig(configObject);
    }

    @Override
    public String publish(NativeObject activity) {
        return activityPublisher.publish(activity);
    }

    @Override
    public String[] listActivities(String contextId) {
        List<Activity> activities = activityBrowser.listActivitiesChronologically(contextId, null);
        String[] serializedActivities = new String[activities.size()];
        for (int i = 0; i < activities.size(); i++) {
            serializedActivities[i] = activities.get(i).toString();
        }
        return serializedActivities;
    }

    @Override
    public double getRating(String targetId, String tenant) {
        return activityBrowser.getRating(targetId, tenant);
    }

    @Override
    public String getSocialObjectJson(String targetId, String tenant, String sortOrder) {
        SortOrder order;
        try {
            order = SortOrder.valueOf(sortOrder);
        } catch (IllegalArgumentException e) {
            order = SortOrder.NEWEST;
        }
        JsonObject socialObject = activityBrowser.getSocialObject(targetId, tenant, order);

        if (socialObject != null) {
            return socialObject.toString();
        } else {
            return "{}";
        }
    }
}
