package org.wso2.carbon.social.summarizer;

import java.util.Map;

public class SummarizerFactory {
    public static Summarizer create(String summarizerName, String rootId, Map<String, Summarizer> summarizerMap) {
        if (summarizerName.equals("like")) {
            return getLikeSummarizer(summarizerMap);
        } else if (summarizerName.equals("dislike")) {
            return getLikeSummarizer(summarizerMap);
        } else if (summarizerName.equals("unlike")) {
            return getLikeSummarizer(summarizerMap);
        } else if (summarizerName.equals("undislike")) {
            return getLikeSummarizer(summarizerMap);
        } else if (summarizerName.equals("review")) {
            return new ReviewSummarizer();
        } else {
            return null;
        }
    }

    private static Summarizer getLikeSummarizer(Map<String, Summarizer> summarizerMap) {
        Summarizer like = summarizerMap.get("like");
        if (like == null) {
            like = new LikeSummarizer();
        }
        return like;
    }
}
