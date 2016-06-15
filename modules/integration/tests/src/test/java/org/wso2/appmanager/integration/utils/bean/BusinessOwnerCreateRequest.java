/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appmanager.integration.utils.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This class is used to generate the payload for business owner creation.
 */

public class BusinessOwnerCreateRequest  extends AbstractRequest {
    private static final Log log = LogFactory.getLog(BusinessOwnerCreateRequest.class);
    private BusinessOwner businessOwner;

    public BusinessOwnerCreateRequest(BusinessOwner businessOwner) {
        this.businessOwner = businessOwner;
    }

    @Override
    public void setAction() {

    }

    @Override
    public void init() {
        if(businessOwner.getBusinessOwnerId() != 0) {
            addParameter("businessOwnerId", String.valueOf(businessOwner.getBusinessOwnerId()));
        }
        addParameter("businessOwnerName", checkValue(businessOwner.getBusinessOwnerName()));
        addParameter("businessOwnerEmail", checkValue(businessOwner.getBusinessOwnerEmail()));
        addParameter("businessOwnerDescription", checkValue(businessOwner.getBusinessOwnerDescription()));
        addParameter("businessOwnerSite", checkValue(businessOwner.getBusinessOwnerSite()));

        List<BusinessOwnerProperty> businessOwnerPropertiesList = businessOwner.getBusinessOwnerPropertiesList();
        JSONObject businessOwnerCustomProperties = new JSONObject();
        if (businessOwnerPropertiesList != null) {
            for(int i = 0; i < businessOwnerPropertiesList.size(); i++) {
                JSONArray propertiesArray = new JSONArray();
                try {
                    propertiesArray.put(0, businessOwnerPropertiesList.get(i).getPropertyValue());
                    propertiesArray.put(1, businessOwnerPropertiesList.get(i).isShowingInStore());
                    businessOwnerCustomProperties.put(businessOwnerPropertiesList.get(i).getPropertyId(),
                                                      businessOwnerCustomProperties);
                } catch (JSONException e) {
                    log.error("Error occurred while adding custom fields to JSON Array.");
                }
            }
        }
        addParameter("businessOwnerProperties", businessOwnerCustomProperties.toString());
    }

    private String checkValue(String input) {
        return input != null ? input : "";

    }
}