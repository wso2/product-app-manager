package org.wso2.carbon.social;

import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.service.SocialActivityService;

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
        List<String> activities = activityBrowser.listActivities(contextId);
        return activities.toArray(new String[activities.size()]);
    }
}
