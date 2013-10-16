package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.wso2.carbon.social.Activity;

import java.util.HashMap;
import java.util.Map;

public class ReviewSummarizer implements Summarizer {

    Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();

    @Override
    public boolean add(Activity activity) {
        String id = activity.getTargetId();
        Map<String, Integer> ratings = map.get(id);
        if (ratings == null) {
            ratings = new HashMap<String, Integer>();
            map.put(id, ratings);
        }
        ratings.put(activity.getActorId(), activity.getRating());
        return false;
    }

    @Override
    public void summarize(JsonObject root, Map<String, Activity> activities) {
        for (Map.Entry<String, Map<String, Integer>> activityRatingEntry : map.entrySet()) {
            Activity activity = activities.get(activityRatingEntry.getKey());
            int totalRatings = 0;
            int numOfRatings = 0;
            for (int rating : activityRatingEntry.getValue().values()) {
                totalRatings += rating;
                numOfRatings++;
            }
            JsonObject object;
            if (activity != null) {
                object = activity.getBody();
            } else {
                //TODO: ideally we should check if (activityRatingEntry.getKey() == rootId)
                object = root;
            }
            object.add("rating", new JsonPrimitive(((double) totalRatings) / numOfRatings));
        }
    }
}
