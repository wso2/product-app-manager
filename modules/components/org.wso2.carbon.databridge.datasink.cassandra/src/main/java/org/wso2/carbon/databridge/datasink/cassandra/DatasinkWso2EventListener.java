package org.wso2.carbon.databridge.datasink.cassandra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.datasink.cassandra.internal.util.ServiceHolder;
import org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory;
import org.wso2.carbon.event.processor.api.receive.BasicEventListener;
import org.wso2.carbon.event.processor.api.receive.Wso2EventListener;

import java.util.ArrayList;
import java.util.Arrays;
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

public class DatasinkWso2EventListener implements Wso2EventListener {

    private static Log log = LogFactory.getLog(DatasinkWso2EventListener.class);
    private Credentials credentials;

    public DatasinkWso2EventListener(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public void onRemoveDefinition(StreamDefinition streamDefinition) {

    }

    @Override
    public void onEvent(Event event) {
        if (System.getProperty("disable.bam.event.storage") == null) {
            try {
                Event[] events= {event};
                ServiceHolder.getCassandraConnector().insertEventList(
                        credentials, ClusterFactory.getCluster(credentials), Arrays.asList(events));
            } catch (Exception e) {
                String errorMsg = "Error processing event. ";
                log.error(errorMsg, e);
            }
        }
    }

    @Override
    public void onAddDefinition(StreamDefinition streamDefinition) {
        System.out.println("Stream :" + streamDefinition.getStreamId() + " created.");
    }

}
