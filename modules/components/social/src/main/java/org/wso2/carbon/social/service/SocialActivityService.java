package org.wso2.carbon.social.service;


import org.mozilla.javascript.NativeObject;

import java.util.List;

public interface SocialActivityService {

    /**
     * Allows an external configuration object to be passed into the Service
     * @param configObject
     */
    void configPublisher(NativeObject configObject);

    String publish(NativeObject activity);

    String[] listActivities(String contextId);

    double getRating(String targetId, String tenant);

    String getSocialObjectJson(String targetId, String sortOrder, String tenant);
}
