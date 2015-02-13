/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.Util.Bean;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.ContentType;

/**
 * This class is used to generate multipart/form-data request for Adding document
 * if action = "addDocumentation" and mode="" -> add documentation
 * if action = "addDocumentation" and mode="Update" -> update documentation
 * if docType="other" need to set value for newType
 * if source type ="file" then need to set docLocation
 */
public class DocumentRequest extends AbstractMultiPartRequest {

    private String mode = "";
    private String docUrl = "http://";
    private String sourceType = "inline";
    private String summary = "summary of inline doc";
    private String docType = "how to";
    private String docName = "doc";
    private String version;
    private String apiName;
    private String action = "addDocumentation";
    private String provider = "admin";
    private String optionsRadios = "how to";
    private String optionsRadios1 = "inline";
    private String newType;
    private String docLocation;

    public DocumentRequest(String apiName, String apiVersion) {
        this.apiName = apiName;
        this.version = apiVersion;

    }

    @Override
    public void init() {
        addParameter("mode", new StringBody(mode, ContentType.TEXT_PLAIN));
        addParameter("docUrl", new StringBody(docUrl, ContentType.TEXT_PLAIN));
        addParameter("sourceType", new StringBody(sourceType, ContentType.TEXT_PLAIN));
        addParameter("summary", new StringBody(summary, ContentType.TEXT_PLAIN));
        addParameter("docType", new StringBody(docType, ContentType.TEXT_PLAIN));
        addParameter("docName", new StringBody(docName, ContentType.TEXT_PLAIN));
        addParameter("version", new StringBody(version, ContentType.TEXT_PLAIN));
        addParameter("apiName", new StringBody(apiName, ContentType.TEXT_PLAIN));
        addParameter("action", new StringBody(action, ContentType.TEXT_PLAIN));
        addParameter("provider", new StringBody(provider, ContentType.TEXT_PLAIN));
        addParameter("optionsRadios", new StringBody(optionsRadios, ContentType.TEXT_PLAIN));
        addParameter("optionsRadios1", new StringBody(optionsRadios1, ContentType.TEXT_PLAIN));
        if ("other".equalsIgnoreCase(docType)) {
            addParameter("newType", new StringBody(newType, ContentType.TEXT_PLAIN));
        }
        if ("file".equalsIgnoreCase(sourceType)) {
            FileBody bin = new FileBody(new File(docLocation));
            addParameter("docLocation", bin);
        }
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }


    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getOptionsRadios() {
        return optionsRadios;
    }

    public void setOptionsRadios(String optionsRadios) {
        this.optionsRadios = optionsRadios;
    }

    public String getOptionsRadios1() {
        return optionsRadios1;
    }

    public void setOptionsRadios1(String optionsRadios1) {
        this.optionsRadios1 = optionsRadios1;
    }

    public String getDocLocation() {
        return docLocation;
    }

    public void setDocLocation(String docLocation) {
        this.docLocation = docLocation;
    }


}