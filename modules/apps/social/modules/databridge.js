var Agent = org.wso2.carbon.databridge.agent.thrift.Agent;
var AgentConfiguration = org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
var DataPublisher = org.wso2.carbon.databridge.agent.thrift.DataPublisher;
var Event = org.wso2.carbon.databridge.commons.Event;

var publishEvent = function (dataPublisher, streamId, assetID, userID, comment) {
       	var assetID = new java.lang.String(assetID);
       	var userID = new java.lang.String(userID);
      	var comment = new java.lang.String(comment);
	var clientType = new java.lang.String("ex");
	event = new Event(streamId, Date.now(),[clientType], null,[assetID,userID,comment]);
	dataPublisher.publish(event);
};

var getStreamId	= function(stream,version){
	try {
            streamId = dataPublisher.findStream(stream, version);

        } catch (e) {
            streamId = dataPublisher.defineStream("{" +
                                                  "  'name':'" + stream + "'," +
                                                  "  'version':'" + version + "'," +
                                                  "  'nickName': 'social_comments'," +
                                                  "  'description': 'Social comments feature'," +
                                                  "  'metaData':[" +
                                                  "          {'name':'clientType','type':'STRING'}" +
                                                  "  ]," +
                                                  "  'payloadData':[" +
						  "          {'name':'activity_type','type':'STRING'}," +	
                                                  "          {'name':'asset','type':'STRING'}," +
						  "          {'name':'parent_id','type':'STRING'}," +
						  "          {'name':'parent_type','type':'STRING'}," +
                                                  "          {'name':'user','type':'STRING'}," +
                                                  "          {'name':'comment','type':'STRING'}" +
						  "          {'name':'replies','type':'STRING'}" +
						  "          {'name':'rating','type':'STRING'}" +
						  "          {'name':'full','type':'STRING'}" +
                                                  "  ]" +
                                                  "}");
        }

	return streamId;
};

var initPublisher = function(assetID,comment,parent){
//TODO move following values to config
	var host = "localhost";
	var url = "tcp://" + host + ":" + "7611";
	var username = "admin";
	var password = "admin";
	var stream = "org.wso2.ues.social.comments";
	var version = "1.0.0";
	var userID = "udarakr";

	agentConfiguration = new AgentConfiguration();
	agent = new Agent(agentConfiguration);

	dataPublisher = new DataPublisher(url, username, password, agent);

		if(getStreamId(stream, version)){
		        publishEvent(dataPublisher, streamId, assetID, userID, comment);
			dataPublisher.stop();
		}
};

