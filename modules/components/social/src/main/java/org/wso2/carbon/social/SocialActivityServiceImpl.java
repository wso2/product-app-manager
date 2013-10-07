package org.wso2.carbon.social;

import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.service.SocialActivityService;

import java.util.ArrayList;
import java.util.List;

public class SocialActivityServiceImpl implements SocialActivityService {


    private ActivityPublisher activityPublisher = new ActivityPublisher();
    private ActivityBrowser activityBrowser = new ActivityBrowser();

    @Override
    public void publish(NativeObject activity) {
        activityPublisher.publish(activity);
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
}
