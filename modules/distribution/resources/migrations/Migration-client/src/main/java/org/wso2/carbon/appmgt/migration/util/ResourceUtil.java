/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.appmgt.migration.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.appmgt.migration.APPMMigrationException;
import org.wso2.carbon.appmgt.migration.client.dto.SynapseDTO;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.utils.CarbonUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ResourceUtil {

    private static final Log log = LogFactory.getLog(ResourceUtil.class);

    /**
     * Returns the content configuration from a RXT file
     *
     * @param payload
     * @return content element
     * @throws org.wso2.carbon.registry.core.exceptions.RegistryException
     */
    public static String getArtifactUIContentFromConfig(String payload) throws RegistryException {
        try {
            OMElement element = AXIOMUtil.stringToOM(payload);
            element.build();
            OMElement content = element.getFirstChildWithName(new QName("content"));
            if (content != null) {
                return content.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            String message = "Unable to parse the XML configuration. Please validate the XML configuration";
            log.error(message, e);
            throw new RegistryException(message, e);
        }

    }

    public static void transformXMLDocument(Document document, File file) {
        document.getDocumentElement().normalize();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, Charset.defaultCharset().toString());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(file));
        } catch (TransformerConfigurationException e) {
            log.error("Transformer configuration error encountered while transforming file " + file.getName(), e);
        } catch (TransformerException e) {
            log.error("Transformer error encountered while transforming file " + file.getName(), e);
        }
    }


    public static String getApiPath(int tenantID, String tenantDomain) {
        log.debug("Get api synapse files for tenant " + tenantID + '(' + tenantDomain + ')');
        String apiFilePath;
        if (tenantID != MultitenantConstants.SUPER_TENANT_ID) {
            apiFilePath = CarbonUtils.getCarbonTenantsDirPath() + File.separatorChar + tenantID +
                    File.separatorChar + "synapse-configs" + File.separatorChar + "default" + File.separatorChar + "api";
        } else {
            apiFilePath = CarbonUtils.getCarbonRepository() + "synapse-configs" + File.separatorChar +
                    "default" + File.separatorChar + "api";
        }
        log.debug("Path of api folder " + apiFilePath);

        return apiFilePath;
    }

    public static List<SynapseDTO> getVersionedAPIs(String apiFilePath) {
        File apiFiles = new File(apiFilePath);
        File[] files = apiFiles.listFiles();

        ArrayList<SynapseDTO> versionedAPIs = new ArrayList<SynapseDTO>();

        if (files != null) {
            for (File file : files) {
                try {
                    if (!file.getName().endsWith(".xml")) { // Ignore non xml files
                        continue;
                    }

                    Document doc = buildDocument(file, file.getName());
                    Element rootElement = doc.getDocumentElement();

                    // Ensure that we skip internal apis such as '_TokenAPI_.xml' and apis
                    // that represent default versions
                    if (Constants.SYNAPSE_API_ROOT_ELEMENT.equals(rootElement.getNodeName()) &&
                            rootElement.hasAttribute(Constants.SYNAPSE_API_ATTRIBUTE_VERSION)) {
                        log.debug("API file name : " + file.getName());
                        SynapseDTO synapseConfig = new SynapseDTO(doc, file);
                        versionedAPIs.add(synapseConfig);
                    }
                } catch (APPMMigrationException e) {
                    log.error("Error when passing file " + file.getName(), e);
                }
            }
        }

        return versionedAPIs;
    }

    public static Document buildDocument(File file, String fileName) throws APPMMigrationException {
        Document doc = null;
        try {
            DocumentBuilder docBuilder = getDocumentBuilder(fileName);
            doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
        } catch (SAXException e) {
            handleException("Error occurred while parsing the " + fileName + " xml document", e);
        } catch (IOException e) {
            ResourceUtil.handleException("Error occurred while reading the " + fileName + " xml document", e);
        }

        return doc;
    }

    public static String getResourceContent(Object content) {
        return new String((byte[]) content, Charset.defaultCharset());
    }

    private static DocumentBuilder getDocumentBuilder(String fileName) throws APPMMigrationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = null;
        try {
            docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            ResourceUtil.handleException("Error occurred while trying to build the " + fileName + " xml document", e);
        }

        return docBuilder;
    }

    /**
     * To handle exceptions
     *
     * @param msg error message
     * @throws org.wso2.carbon.appmgt.migration.APPMMigrationException
     */
    public static void handleException(String msg, Throwable e) throws APPMMigrationException {
        log.error(msg, e);
        throw new APPMMigrationException(msg, e);
    }

}
