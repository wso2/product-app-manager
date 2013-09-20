package org.wso2.carbon.databridge.datasink.cassandra;

import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.databridge.commons.Credentials;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.core.exception.StreamDefinitionStoreException;
import org.wso2.carbon.databridge.datasink.cassandra.internal.util.ServiceHolder;
import org.wso2.carbon.databridge.persistence.cassandra.datastore.ClusterFactory;
import org.wso2.carbon.event.builder.core.EventBuilderService;
import org.wso2.carbon.event.builder.core.config.EventBuilderConfiguration;
import org.wso2.carbon.event.builder.core.exception.EventBuilderConfigurationException;
import org.wso2.carbon.event.builder.core.internal.config.InputStreamConfiguration;
import org.wso2.carbon.event.input.adaptor.core.config.InputEventAdaptorConfiguration;
import org.wso2.carbon.event.input.adaptor.core.message.config.InputEventAdaptorMessageConfiguration;
import org.wso2.carbon.event.input.adaptor.manager.core.InputEventAdaptorManagerService;
import org.wso2.carbon.event.input.adaptor.manager.core.exception.InputEventAdaptorManagerConfigurationException;
import org.wso2.carbon.event.processor.api.receive.EventReceiver;
import org.wso2.carbon.utils.ConfigurationContextService;

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

public class StreamSubscriber {

    private static Log log = LogFactory.getLog(StreamSubscriber.class);

    public void subscribeStream(StreamDefinition streamDefinition, Credentials credentials) {

        boolean isValidStream = this.validateStream() ;
        EventReceiver eventReceiverService = ServiceHolder.getEventReceiverService();
        if(eventReceiverService != null) {
            if(isValidStream) {
                insertStreamToCassandra(streamDefinition, credentials);
                this.addInputEventAdaptor();
                this.addEventBuilder(streamDefinition);
                this.subscribeDataSink(streamDefinition, credentials);
            }
        } else {
            String errorMsg = "Event Builder Service was not set. ";
            log.error(errorMsg);
        }

    }

    private boolean validateStream() {
        //TODO Check the configuration
        return true;
    }

    private boolean insertStreamToCassandra(StreamDefinition streamDefinition, Credentials credentials) {
        ServiceHolder.getCassandraConnector().definedStream(ClusterFactory.getCluster(credentials), streamDefinition);
        try {
            StreamDefinition existingStreamDefinition = ServiceHolder.getCassandraConnector().getStreamDefinitionFromCassandra(ClusterFactory.getCluster(credentials), streamDefinition.getStreamId());
            return existingStreamDefinition != null;
        } catch (StreamDefinitionStoreException e) {
            String errorMsg = "Error occurred while checking the stream. ";
            log.error(errorMsg);
            return false;
        }
    }

    private void addInputEventAdaptor() {
        InputEventAdaptorManagerService inputEventAdaptorManagerService = ServiceHolder.getInputEventAdaptorManagerService();
        AxisConfiguration axisConfiguration = this.getAxisConfiguration();
        InputEventAdaptorConfiguration inputEventAdaptorConfiguration;

        try {
            inputEventAdaptorConfiguration = inputEventAdaptorManagerService.getActiveInputEventAdaptorConfiguration("CassandraEventReceiver", this.getTenantId());
            if(inputEventAdaptorConfiguration == null) { // If the Input Event Adapter is not yet created
                inputEventAdaptorConfiguration = this.createInputEventAdaptorConfiguration();
                inputEventAdaptorManagerService.deployInputEventAdaptorConfiguration(inputEventAdaptorConfiguration, axisConfiguration);
            }
        } catch (InputEventAdaptorManagerConfigurationException e) {
            log.error("Error while getting Input Event Adaptor Manager Configuration:\n" + e);
        }
    }

    private InputEventAdaptorConfiguration createInputEventAdaptorConfiguration() {
        InputEventAdaptorConfiguration inputEventAdaptorConfiguration = new InputEventAdaptorConfiguration();
        inputEventAdaptorConfiguration.setName("CassandraEventReceiver");
        inputEventAdaptorConfiguration.setType("wso2event");
        return inputEventAdaptorConfiguration;
    }

    private boolean addEventBuilder(StreamDefinition streamDefinition) {
        EventBuilderService eventBuilderService = ServiceHolder.getEventBuilderService();
        EventBuilderConfiguration eventBuilderConfiguration = eventBuilderService.getActiveEventBuilderConfiguration(this.createEventBuilderName(streamDefinition), this.getTenantId());
        
        if(eventBuilderConfiguration == null || eventBuilderConfiguration.getEventBuilderName() == null || eventBuilderConfiguration.getEventBuilderName().equals("")) {
            InputEventAdaptorManagerService inputEventAdaptorManagerService = ServiceHolder.getInputEventAdaptorManagerService();

            try {
                InputEventAdaptorConfiguration inputEventAdaptorConfiguration = inputEventAdaptorManagerService.getActiveInputEventAdaptorConfiguration("CassandraEventReceiver", this.getTenantId());

                InputEventAdaptorMessageConfiguration inputEventAdaptorMessageConfiguration = new InputEventAdaptorMessageConfiguration();
                inputEventAdaptorMessageConfiguration.addInputMessageProperty("stream", streamDefinition.getName());
                inputEventAdaptorMessageConfiguration.addInputMessageProperty("version", streamDefinition.getVersion());

                eventBuilderConfiguration = new EventBuilderConfiguration();
                StreamDefinitionMapper streamDefinitionMapper = new StreamDefinitionMapper();
                InputStreamConfiguration inputStreamConfiguration = new InputStreamConfiguration();
                inputStreamConfiguration.setInputEventAdaptorMessageConfiguration(inputEventAdaptorMessageConfiguration);
                inputStreamConfiguration.setInputEventAdaptorName(inputEventAdaptorConfiguration.getName());
                inputStreamConfiguration.setInputEventAdaptorType(inputEventAdaptorConfiguration.getType());
                eventBuilderConfiguration.setInputStreamConfiguration(inputStreamConfiguration);
                eventBuilderConfiguration.setToStreamName(streamDefinition.getName());
                eventBuilderConfiguration.setToStreamVersion(streamDefinition.getVersion());
                eventBuilderConfiguration.setInputMapping(streamDefinitionMapper.generateEventMapping(streamDefinition));
                eventBuilderConfiguration.setEventBuilderName(this.createEventBuilderName(streamDefinition));

                eventBuilderService.deployEventBuilderConfiguration(eventBuilderConfiguration, this.getAxisConfiguration());
                return true;

            } catch (InputEventAdaptorManagerConfigurationException e) {
                log.error("Error while getting Input Event Adaptor Manager Configuration:\n" + e);
                return false;
            } catch (EventBuilderConfigurationException e) {
                log.error("Error when subscribing to event builder:\n" + e);
                return false;
            }
        } else {
            return false;
        }

    }
    
    private String createEventBuilderName(StreamDefinition streamDefinition) {
        return "cassandra-stream-" + streamDefinition.getStreamId();
    }

    private void subscribeDataSink(StreamDefinition streamDefinition, Credentials credentials) {
        EventReceiver eventReceiver = ServiceHolder.getEventReceiverService();
        DatasinkWso2EventListener datasinkWso2EventListener = new DatasinkWso2EventListener(credentials);
        eventReceiver.subscribe(streamDefinition.getStreamId(), datasinkWso2EventListener, this.getTenantId());
    }

    private int getTenantId() {
        return PrivilegedCarbonContext.getCurrentContext(this.getAxisConfiguration()).getTenantId();
    }

    private AxisConfiguration getAxisConfiguration() {
        ConfigurationContextService configurationContextService = ServiceHolder.getConfigurationContextService();
        return configurationContextService.getServerConfigContext().getAxisConfiguration();
    }

}
