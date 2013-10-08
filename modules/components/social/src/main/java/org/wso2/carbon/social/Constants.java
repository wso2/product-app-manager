package org.wso2.carbon.social;

public class Constants {
    public static final String BODY_COLUMN = "payload_body";
    public static final String CONTEXT_ID_COLUMN = "payload_context.id";
    public static final String TIMESTAMP_COLUMN = "Timestamp";

    public static final String VERB_JSON_PROP = "verb";
    public static final String TYPE_JSON_PROP = "objectType";
    public static final String OBJECT_JSON_PROP = "object";
    public static final String TARGET_JSON_PROP = "target";
    public static final String CONTEXT_JSON_PROP = "context";
    public static final String ID_JSON_PROP = "id";

    public static final String STREAM_VERSION = "1.0.0";
    public static final String STREAM_NAME = "org.wso2.social.activity";
    public static final String STREAM_NAME_IN_CASSANDRA = STREAM_NAME.replaceAll("\\.", "_");
    public static final String STREAM_DEF = "{ 'name':'" + STREAM_NAME + "'," +
            " 'version':'" + STREAM_VERSION + "'," +
            " 'nickName': 'Activity stream for WSO2 Social'," +
            " 'description': 'store json object and mete-data describing each activity'," +
            " 'tags':['social', 'activity']," +
            " 'metaData':[" +
            " ]," +
            " 'correlationData':[" +
            " ]," +
            " 'payloadData':[" +
            "       {'name':'verb','type':'STRING'}," +
            "       {'name':'object.objectType','type':'STRING'}," +
            "       {'name':'context.id','type':'STRING'}," +
            "       {'name':'body','type':'STRING'}" +
            " ]" +
            "}";
}
