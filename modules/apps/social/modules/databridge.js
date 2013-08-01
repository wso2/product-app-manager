var Agent = org.wso2.carbon.databridge.agent.thrift.Agent;
var AgentConfiguration = org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
var DataPublisher = org.wso2.carbon.databridge.agent.thrift.DataPublisher;
var Event = org.wso2.carbon.databridge.commons.Event;

var publishEvents = function (dataPublisher, streamId) {
//TODO remove following values
	var ext_str = new java.lang.String("external");
       	var nokia_str = new java.lang.String("Nokia");
       	var total_int = new java.lang.Integer(123);
      	var quantity_int = new java.lang.Integer(12300);
       	var user_str = new java.lang.String("UdaraR");
	event = new Event(streamId, Date.now(),[ext_str], null,[nokia_str,total_int, quantity_int, user_str]);
	dataPublisher.publish(event);
};

var getStreamId	= function(stream,version){
	try {
            streamId = dataPublisher.findStream(stream, version);

        } catch (e) {
            streamId = dataPublisher.defineStream("{" +
                                                  "  'name':'" + stream + "'," +
                                                  "  'version':'" + version + "'," +
                                                  "  'nickName': 'Phone_Retail_Shop'," +
                                                  "  'description': 'Phone Sales'," +
                                                  "  'metaData':[" +
                                                  "          {'name':'clientType','type':'STRING'}" +
                                                  "  ]," +
                                                  "  'payloadData':[" +
                                                  "          {'name':'brand','type':'STRING'}," +
                                                  "          {'name':'quantity','type':'INT'}," +
                                                  "          {'name':'total','type':'INT'}," +
                                                  "          {'name':'user','type':'STRING'}" +
                                                  "  ]" +
                                                  "}");
        }

	return streamId;
};

var initPublisher = function(){
//TODO remove following values
	var host = "localhost";
	var url = "tcp://" + host + ":" + "7611";
	var username = "admin";
	var password = "admin";
	var stream = "org.wso2.bam.phone.retail.store.kpi";
	var version = "1.0.0";

	agentConfiguration = new AgentConfiguration();
	agent = new Agent(agentConfiguration);

	dataPublisher = new DataPublisher(url, username, password, agent);

		if(getStreamId(stream, version)){
			for (var i = 0; i < 100; i++) {
		        publishEvents(dataPublisher, streamId);
		        print("Events published : " + (i + 1)+"<br/>");
		   	}
			dataPublisher.stop();
		}
};

