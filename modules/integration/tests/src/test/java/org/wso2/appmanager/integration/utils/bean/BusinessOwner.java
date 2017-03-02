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

import java.util.List;
/**
 * Business owner model class.
 */
public class BusinessOwner {
    private int businessOwnerId;
    private String businessOwnerName;
    private String businessOwnerEmail;
    private String businessOwnerDescription;
    private String businessOwnerSite;
    private List<BusinessOwnerProperty> businessOwnerPropertiesList;

    public void setBusinessOwnerId(int businessOwnerId) {
        this.businessOwnerId = businessOwnerId;
    }

    public void setBusinessOwnerName(String businessOwnerName) {
        this.businessOwnerName = businessOwnerName;
    }

    public void setBusinessOwnerEmail(String businessOwnerEmail) {
        this.businessOwnerEmail = businessOwnerEmail;
    }

    public void setBusinessOwnerSite(String businessOwnerSite) {
        this.businessOwnerSite = businessOwnerSite;
    }

    public void setBusinessOwnerDescription(String businessOwnerDescription) {
        this.businessOwnerDescription = businessOwnerDescription;
    }

    public int getBusinessOwnerId() {
        return businessOwnerId;
    }

    public String getBusinessOwnerName() {
        return businessOwnerName;
    }

    public String getBusinessOwnerEmail() {
        return businessOwnerEmail;
    }

    public String getBusinessOwnerDescription() {
        return businessOwnerDescription;
    }

    public String getBusinessOwnerSite() {
        return businessOwnerSite;
    }

    public List<BusinessOwnerProperty> getBusinessOwnerPropertiesList() {
        return businessOwnerPropertiesList;
    }

    public void setBusinessOwnerPropertiesList(List<BusinessOwnerProperty> businessOwnerPropertiesList) {
        this.businessOwnerPropertiesList = businessOwnerPropertiesList;
    }

}