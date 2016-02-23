/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.JWTSecurity.servlets;

import org.wso2.JSON.JSONObject;
import org.wso2.JWTSecurity.Exceptions.JWTSecurityException;
import org.wso2.JWTSecurity.filter.utils.JWTSecurityConstraints;
import org.wso2.JWTSecurity.filter.utils.JWTSecurityConstraintsReader;
import org.wso2.JWTSecurity.filter.utils.SecurityConstraint;
import org.wso2.JWTSecurity.filter.utils.UserAuthenticator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main class in this project. What this class does is it avoid the login pages of secured apps which are
 * running on app manager. To make it application should have J2ee security model for authentication. What this class
 * basically does is decode the JWT token from the request header, read user and user authentication information from
 * it, then it wrap the HttpRequest in a http request wrapper which contains decoded user and user roles, then forward
 * the request.
 */
public class JWTSecurityFilter implements Filter {

    private static final String JWT_TOKEN_SUBJECT = "sub";
    private static final String JWT_TOKEN_NAME = "x-jwt-assertion";
    private static final String JWT_TOKEN_USER_ROLES = "http://wso2.org/claims/role";
    private static final String XML_FILE_PATH = "/WEB-INF/JWTSecurityConstraints.xml";
    private static final Logger log = Logger.getLogger((JWTSecurityFilter.class.getName()));
    private JWTSecurityConstraints JWTSecurityConstraints;
    private UserAuthenticator userAuthenticator;
    protected String trustStorePath = "";
    protected String trustStorePassword = "";
    protected String alias = "";
    private JWTValidator JWTValidator;
    /**
     * @param filterConfig
     * @throws ServletException This is is the methid which runs once at the start of the program so all the
     *                          initializations has been done here.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try (InputStream inStream = filterConfig.getServletContext().getResourceAsStream(
                XML_FILE_PATH)) {
            JWTSecurityConstraintsReader JWTSecurityConstraintsReader = new JWTSecurityConstraintsReader();
            JWTSecurityConstraints = JWTSecurityConstraintsReader.getCustomData(inStream);
            List<SecurityConstraint> securityConstraintList = JWTSecurityConstraints.getSecurityConstraint();
            userAuthenticator = new UserAuthenticator(securityConstraintList);
            JWTValidator = new JWTValidator();
        } catch (IOException e) {
            log.log(Level.OFF, XML_FILE_PATH + " is not found", e);
        } catch (JWTSecurityException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * @param req-  ServletRequest
     * @param resp  - ServletResponse
     * @param chain - FilterChain
     * @throws IOException
     * @throws ServletException Here what this method do is it read the JWT token from the original request. Then it
     *                          decodes the JWT by base64 decoder, Then read the payload part and convert it into a
     *                          jason object. By that it reads the user and role list. Then it wrap the username and
     *                          role list in the HTTPRequestWrapper with original reuest and forward it.
     */

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String jwtToken = request.getHeader(JWT_TOKEN_NAME);
        String requestedUri = request.getServletPath();

        if (userAuthenticator.isURISecured(requestedUri)) {
            if (jwtToken != null && jwtToken != "") {

                if (JWTValidator.isValid(jwtToken, trustStorePath, trustStorePassword, alias)) {
                    String[] jwtArray = JWTValidator.jwtPartitions(jwtToken);

                    if (jwtArray.length != 3) {
                        //Format of the JWT header is invalid so we send un-authorized.
                        response.sendError(403);
                        return;
                    }
                    String payloadString = JWTValidator.getjwtPayloadDecode(jwtArray);
                    JSONObject payLoad = null;

                    try {
                        payLoad = JWTValidator.jsonObjectConverter(payloadString);
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Error while creating JASON object from payloadString", e);
                        response.sendError(422, "Invalid JWT Header");
                    }
                    String userName = (String) payLoad.get(JWT_TOKEN_SUBJECT);
                    String roles = (String) payLoad.get(JWT_TOKEN_USER_ROLES);
                    List<String> rolesList;

                    if (roles != null) {
                        rolesList = new ArrayList<String>(Arrays.asList(roles.split(",")));
                    } else {
                        rolesList = Collections.emptyList();
                    }

                    UserRoleRequestWrapper userRoleRequestWrapper = new UserRoleRequestWrapper(userName, rolesList,
                                                                                               request);
                    if (userAuthenticator.isUserAuthenticated(rolesList, requestedUri)) {
                        chain.doFilter(userRoleRequestWrapper, resp);
                    } else {
                        response.sendError(403);
                        return;
                    }
                }
            } else {
                log.log(Level.WARNING,
                        "JWT Header is not found in the request, considering the request as not authenticated by this" +
                                " " +
                                "filter");
                response.sendError(403);
                return;
            }
        } else {
            chain.doFilter(req, resp);
        }
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    public void destroy() {
    }
}