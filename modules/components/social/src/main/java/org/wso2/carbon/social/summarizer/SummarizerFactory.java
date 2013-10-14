package org.wso2.carbon.social.summarizer;

public class SummarizerFactory {
    public static Summarizer create(String summarizerName, String rootId) {
        if (summarizerName.equals("like")) {
            return new LikeSummarizer();
        } else {
            return null;
        }
    }
}
