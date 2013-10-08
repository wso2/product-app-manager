package org.wso2.carbon.social.service;


import org.mozilla.javascript.NativeObject;

import java.util.List;

public interface SocialActivityService {
    void publish(NativeObject activity);

    String[] listActivities(String contextId);

    String getSocialObjectJson(String targetId);
}
