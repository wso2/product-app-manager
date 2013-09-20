package org.wso2.carbon.databridge.datasink.cassandra.subscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.core.AgentCallback;
import org.wso2.carbon.databridge.datasink.cassandra.StreamSubscriber;
import org.wso2.carbon.databridge.datasink.cassandra.internal.util.ServiceHolder;
import org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory;
import org.wso2.carbon.event.builder.core.EventBuilderService;

import java.util.List;

/**
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BAMEventSubscriber implements AgentCallback {

    private static Log log = LogFactory.getLog(BAMEventSubscriber.class);
    private StreamSubscriber streamSubscriber;

    @Override
    public void definedStream(StreamDefinition streamDefinition, Credentials credentials) {

        //streamSubscriber = new StreamSubscriber();
        //streamSubscriber.subscribeStream(streamDefinition, credentials);


        ServiceHolder.getCassandraConnector().definedStream(ClusterFactory.getCluster(credentials),
                                                            streamDefinition);
    }

    @Override
    public void removeStream(StreamDefinition streamDefinition, Credentials credentials) {
        ServiceHolder.getCassandraConnector().removeStream(credentials,
                                                           ClusterFactory.getCluster(credentials),
                                                           streamDefinition);
    }

    @Override
    public void receive(List<Event> eventList, Credentials credentials) {
        if (System.getProperty("disable.bam.event.storage") == null) {
            try {
                ServiceHolder.getCassandraConnector().insertEventList(
                        credentials, ClusterFactory.getCluster(credentials), eventList);
            } catch (Exception e) {
                String errorMsg = "Error processing event. ";
                log.error(errorMsg, e);
            }
        }

//        for (Event event : eventList) {
//            try {
//                ServiceHolder.getCassandraConnector().insertEvent(ClusterFactory.getCluster(credentials), event);
//            } catch (Exception e) {                           u
//                String errorMsg = "Error processing event. " + event.toString();
//                log.error(errorMsg, e);
//            }
//
//        }
    }
}
