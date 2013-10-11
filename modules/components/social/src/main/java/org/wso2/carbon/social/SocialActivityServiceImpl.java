package org.wso2.carbon.social;

import com.google.gson.JsonObject;
import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.service.SocialActivityService;

import java.util.List;

public class SocialActivityServiceImpl implements SocialActivityService {


    private ActivityPublisher activityPublisher = new ActivityPublisher();
    private ActivityBrowser activityBrowser = new ActivityBrowser();

    @Override
    public String publish(NativeObject activity) {
        return activityPublisher.publish(activity);
    }

    @Override
    public String[] listActivities(String contextId) {
        List<Activity> activities = activityBrowser.listActivitiesChronologically(contextId);
        String[] serializedActivities = new String[activities.size()];
        for (int i = 0; i < activities.size(); i++) {
            serializedActivities[i] = activities.get(i).toString();
        }
        return serializedActivities;
    }

    @Override
    public double getRating(String targetId) {
        return activityBrowser.getRating(targetId);
    }

    @Override
    public String getSocialObjectJson(String targetId, String sortOrder) {
        SortOrder order;
        try {
            order = SortOrder.valueOf(sortOrder);
        } catch (IllegalArgumentException e) {
            order = SortOrder.NEWEST;
        }
        JsonObject socialObject = activityBrowser.getSocialObject(targetId, order);

        if (socialObject != null) {
            return socialObject.toString();
        } else {
            return "{}";
        }
    }
}
