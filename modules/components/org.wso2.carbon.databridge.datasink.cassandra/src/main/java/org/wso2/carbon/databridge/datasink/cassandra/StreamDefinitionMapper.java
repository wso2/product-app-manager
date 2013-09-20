package org.wso2.carbon.databridge.datasink.cassandra;

import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.event.builder.core.internal.config.InputMappingAttribute;
import org.wso2.carbon.event.builder.core.internal.type.wso2event.Wso2EventInputMapping;
import org.wso2.carbon.event.builder.core.internal.util.EventBuilderConstants;

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

public class StreamDefinitionMapper {

    private Wso2EventInputMapping wso2EventInputMapping;
    private List<Attribute> correlationDataFields;
    private List<Attribute> metaDataFields;
    private List<Attribute> payloadDataFields;

    public Wso2EventInputMapping generateEventMapping(StreamDefinition streamDefinition) {

        wso2EventInputMapping = new Wso2EventInputMapping();
        correlationDataFields = streamDefinition.getCorrelationData();
        metaDataFields = streamDefinition.getMetaData();
        payloadDataFields = streamDefinition.getPayloadData();

        this.generateCorrelationDataMapping();
        this.generateMetaDataMapping();
        this.generatePayloadDataMapping();

        return wso2EventInputMapping;
    }

    private void generateCorrelationDataMapping() {
        InputMappingAttribute inputMappingAttribute;
        if(correlationDataFields != null) {
            for(Attribute attribute : correlationDataFields) {
                inputMappingAttribute = new InputMappingAttribute(attribute.getName(), EventBuilderConstants.CORRELATION_DATA_PREFIX+ attribute.getName(),
                        attribute.getType(), EventBuilderConstants.CORRELATION_DATA_VAL);
                wso2EventInputMapping.addInputMappingAttribute(inputMappingAttribute);
            }
        }
    }

    private void generateMetaDataMapping() {
        InputMappingAttribute inputMappingAttribute;
        if(metaDataFields != null) {
            for(Attribute attribute : metaDataFields) {
                inputMappingAttribute = new InputMappingAttribute(attribute.getName(), EventBuilderConstants.META_DATA_PREFIX + attribute.getName(),
                        attribute.getType(), EventBuilderConstants.META_DATA_VAL);
                wso2EventInputMapping.addInputMappingAttribute(inputMappingAttribute);
            }
        }

    }

    private void generatePayloadDataMapping() {
        InputMappingAttribute inputMappingAttribute;
        if(payloadDataFields != null) {
            for(Attribute attribute : payloadDataFields) {
                inputMappingAttribute = new InputMappingAttribute(attribute.getName(), attribute.getName(),
                        attribute.getType(), EventBuilderConstants.PAYLOAD_DATA_VAL);
                wso2EventInputMapping.addInputMappingAttribute(inputMappingAttribute);
            }
        }
    }

}
