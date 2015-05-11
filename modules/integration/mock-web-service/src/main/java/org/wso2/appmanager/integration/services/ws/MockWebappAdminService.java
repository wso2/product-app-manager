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

package org.wso2.appmanager.integration.services.ws;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.databinding.ADBException;
import org.wso2.carbon.webapp.mgt.stub.types.axis2.GetPagedWebappsSummary;
import org.wso2.carbon.webapp.mgt.stub.types.axis2.GetPagedWebappsSummaryResponse;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.VersionedWebappMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappsWrapper;

import javax.xml.namespace.QName;

/**
 * Mock webapp admin service.
 * Used for integration and UI testing.
 */
public class MockWebappAdminService {

    private static final QName PAGED_WEBAPPS_SUMMARY_QNAME = new QName(
            "http://mgt.webapp.carbon.wso2.org/xsd", "w2c1", "ns4");
    private static final QName PAGED_NUMBER_QNAME = new QName("http://org.apache.axis2/xsd",
            "pageNumber");

    /**
     * WS method for getPagedWebappsSummary.
     *
     * @param request
     * @return
     * @throws ADBException
     */
    public OMElement getPagedWebappsSummary(OMElement request) throws ADBException {
        int DEFAULT_PAGE_SIZE = 16;
        int MAX_PAGES = 3;
        int ENTRIES_IN_LAST_PAGE = 5;
        GetPagedWebappsSummary pagedSummeryRequest = getGetPagedWebappsSummary(request);

        int pageNumber =
                pagedSummeryRequest.getPageNumber() <= 0 ? 1 : pagedSummeryRequest.getPageNumber();
        pagedSummeryRequest.getWebappType();
        WebappsWrapper webappsWrapper = new WebappsWrapper();
        webappsWrapper.setHostName("localhost");
        webappsWrapper.setHttpPort(8080);
        webappsWrapper.setHttpsPort(8443);
        webappsWrapper.setNumberOfCorrectWebapps(4);
        webappsWrapper.setNumberOfPages(MAX_PAGES);

        VersionedWebappMetadata versionedWebappMetadata = new VersionedWebappMetadata();
        VersionedWebappMetadata[] versionedWebappMetadatas = new VersionedWebappMetadata[1];
        versionedWebappMetadatas[0] = versionedWebappMetadata;
        webappsWrapper.setWebapps(versionedWebappMetadatas);

        int entriesCount = (pageNumber - 1) >= MAX_PAGES ? ENTRIES_IN_LAST_PAGE : DEFAULT_PAGE_SIZE;
        WebappMetadata[] webappMetadatas = new WebappMetadata[entriesCount];
        versionedWebappMetadata.setVersionGroups(webappMetadatas);
        for (int i = 0; i < entriesCount; i++) {
            int appIndex = (pageNumber - 1) * DEFAULT_PAGE_SIZE + i;
            WebappMetadata webappMetadata = getWebappMetadata(appIndex);
            webappMetadatas[i] = webappMetadata;
        }

        GetPagedWebappsSummaryResponse pagedWebappsSummaryResponse = new GetPagedWebappsSummaryResponse();
        pagedWebappsSummaryResponse.set_return(webappsWrapper);
        OMElement omResult = pagedWebappsSummaryResponse
                .getOMElement(PAGED_WEBAPPS_SUMMARY_QNAME, OMAbstractFactory.getOMFactory());

        return omResult;
    }

    /**
     * PArses the OM element and returns the request object
     * @param request
     * @return
     */
    private GetPagedWebappsSummary getGetPagedWebappsSummary(OMElement request) {
        GetPagedWebappsSummary pagedSummeryRequest = new GetPagedWebappsSummary();
        if (request.getLocalName().equals("getPagedWebappsSummary")) {
            OMElement pageNumberOm = request.getFirstChildWithName(PAGED_NUMBER_QNAME);
            pagedSummeryRequest.setPageNumber(Integer.parseInt(pageNumberOm.getText()));
        }
        return pagedSummeryRequest;
    }

    /**
     * Returns a new mock webapp metadata given the index number.
     * @param i
     * @return
     */
    private WebappMetadata getWebappMetadata(int i) {
        WebappMetadata webappMetadata = new WebappMetadata();
        webappMetadata.setContext("/test_c_" + i);
        webappMetadata.setAppVersion("1.0");
        webappMetadata.setContextPath("/test_c_" + i);
        webappMetadata.setDisplayName("Test Context " + i);
        webappMetadata.setWebappFile("test_" + i + ".war");
        webappMetadata.setWebappType("webapp");
        return webappMetadata;
    }
}
