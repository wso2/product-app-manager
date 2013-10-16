package org.wso2.carbon.social.summarizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.wso2.carbon.social.Activity;

import java.util.*;

public class LikeSummarizer implements Summarizer {

    Map<String, LikeInfo> map = new HashMap<String, LikeInfo>();

    @Override
    public boolean add(Activity activity) {
        String verb = activity.getVerb();
        if (verb.equals("like")) {
            LikeInfo likeInfo = getLikeInfo(activity);
            likeInfo.addLike(activity.getActorId());
        } else if (verb.equals("unlike")) {
            LikeInfo likeInfo = getLikeInfo(activity);
            likeInfo.removeLike(activity.getActorId());
        } else if (verb.equals("dislike")) {
            LikeInfo likeInfo = getLikeInfo(activity);
            likeInfo.addDislike(activity.getActorId());
        } else if (verb.equals("undislike")) {
            LikeInfo likeInfo = getLikeInfo(activity);
            likeInfo.removeDislike(activity.getActorId());
        }
        return true;
    }

    private LikeInfo getLikeInfo(Activity activity) {
        String id = activity.getTargetId();
        LikeInfo likeInfo = map.get(id);
        if (likeInfo == null) {
            likeInfo = new LikeInfo();
            map.put(id, likeInfo);
        }
        return likeInfo;
    }

    @Override
    public void summarize(JsonObject root, Map<String, Activity> activities) {
        for (Map.Entry<String, LikeInfo> likeInfoEntry : map.entrySet()) {
            String id = likeInfoEntry.getKey();
            Activity activity = activities.get(id);
            if (activity != null) {
                LikeInfo likeInfo = likeInfoEntry.getValue();
                addAsPeopleCollection(likeInfo.likers, "likes", activity);
                addAsPeopleCollection(likeInfo.dislikers, "dislikes", activity);
            } else {
                //LOG ERROR
            }
        }
    }

    private void addAsPeopleCollection(Set<String> peopleIds, String arrayName, Activity to) {
        JsonObject likes = new JsonObject();
        JsonArray likesItems = new JsonArray();
        for (String liker : peopleIds) {
            JsonObject person = new JsonObject();
            person.add("id", new JsonPrimitive(liker));
            person.add("objectType", new JsonPrimitive("person"));
            likesItems.add(person);
        }
        likes.add("items", likesItems);
        likes.add("totalItems",new JsonPrimitive(peopleIds.size()));
        to.getBody().add(arrayName, likes);
    }

    private static class LikeInfo {

        Set<String> likers;
        Set<String> dislikers;

        public void addLike(String actorId) {
            initSets();
            likers.add(actorId);
            dislikers.remove(actorId);
        }

        public void addDislike(String actorId) {
            initSets();
            likers.remove(actorId);
            dislikers.add(actorId);
        }

        public void removeLike(String actorId) {
            initSets();
            likers.remove(actorId);
            dislikers.remove(actorId);
        }

        public void removeDislike(String actorId) {
            initSets();
            likers.remove(actorId);
            dislikers.remove(actorId);
        }

        private void initSets() {
            if (likers == null) {
                likers = new HashSet<String>();
            }
            if (dislikers == null) {
                dislikers = new HashSet<String>();
            }
        }

    }
}
