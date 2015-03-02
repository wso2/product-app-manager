/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
var Agent = org.wso2.carbon.databridge.agent.thrift.Agent;
var AgentConfiguration = org.wso2.carbon.databridge.agent.thrift.conf.AgentConfiguration;
var DataPublisher = org.wso2.carbon.databridge.agent.thrift.DataPublisher;
var Event = org.wso2.carbon.databridge.commons.Event;

var publishEvent = function (dataPublisher, streamId, activity_type, asset, parent_id, parent_type, user, body, replies, rating) {

	var all = {activity_type:activity_type, asset:asset, parent_id:parent_id, parent_type:parent_type, user:user, body:body, replies:replies, rating:rating};
	var activity_type = new java.lang.String(activity_type);
    var asset = new java.lang.String(asset);
	var parent_id = new java.lang.String(parent_id);
	var parent_type = new java.lang.String(parent_type);
    var user = new java.lang.String(user);
    var body = new java.lang.String(body);
	var replies = new java.lang.String(replies);
	var rating = new java.lang.String(rating);
	var full = new java.lang.String(all);
	var clientType = new java.lang.String("ex");


	event = new Event(streamId, Date.now(),[clientType], null,[activity_type,asset,parent_id,parent_type,user,body,replies,rating,full]);
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
                                                  "          {'name':'body','type':'STRING'}," +
						  "          {'name':'replies','type':'STRING'}," +
						  "          {'name':'rating','type':'STRING'}," +
						  "          {'name':'full','type':'STRING'}" +
                                                  "  ]" +
                                                  "}");
        }

	return streamId;
};

var initPublisher = function(activity_type, asset, parent_id, parent_type, user, body, replies, rating){
	
//TODO move following values to the config
	var host = "localhost";
	var url = "tcp://" + host + ":" + "7611";
	var username = "admin";
	var password = "admin";
	var stream = "org.wso2.ues.social.comments.stream";
	var version = "1.0.0";
	
	agentConfiguration = new AgentConfiguration();
	agent = new Agent(agentConfiguration);

	dataPublisher = new DataPublisher(url, username, password, agent);

		if(getStreamId(stream, version)){
		        publishEvent(dataPublisher, streamId, activity_type, asset, parent_id, parent_type, user, body, replies, rating);
			dataPublisher.stop();
		}
};

var getComments = function(stream){
	var db = new Database("jdbc:cassandra://localhost:9160/EVENT_KS?version=2.0.0","admin","admin",{"driverClassName":"org.apache.cassandra.cql.jdbc.CassandraDriver"});
	var result = db.query("SELECT * FROM org_wso2_ues_social_comments_stream");
	return result;
}

