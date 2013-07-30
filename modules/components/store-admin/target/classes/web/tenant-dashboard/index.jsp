<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil"%>
<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
        boolean hasModMgtPermission = CarbonUIUtil.isUserAuthorized(request,
		"/permission/admin/manage/add/module");
        boolean hasServiceMgtPermission = CarbonUIUtil.isUserAuthorized(request, "/permission/admin/manage/add/service");
        boolean hasWebAppMgtPermission = CarbonUIUtil.isUserAuthorized(request,"/permission/admin/manage/manage/webapp");
        boolean loggedIn = false;
        if (param != null) {
            loggedIn = (Boolean) param;             
        } 
%>
  
<div id="passwordExpire">
         <%
         if (loggedIn && passwordExpires != null) {
         %>
              <div class="info-box"><p>Your password expires at <%=passwordExpires%>. Please change by visiting <a href="../user/change-passwd.jsp?isUserChange=true&returnPath=../admin/index.jsp">here</a></p></div>
         <%
             }
         %>
</div>
<div id="middle">
<div id="workArea">
<style type="text/css">
    .tip-table td.service-hosting {
        background-image: url(../../carbon/tenant-dashboard/images/service-hosting.png);
    }

    .tip-table td.web-applications {
        background-image: url(../../carbon/tenant-dashboard/images/web-applications.png);
    }
    .tip-table td.service-testing {
        background-image: url(../../carbon/tenant-dashboard/images/service-testing.png);
    }
    .tip-table td.message-tracing {
        background-image: url(../../carbon/tenant-dashboard/images/message-tracing.png);
    }


    .tip-table td.wsdl2java {
        background-image: url(../../carbon/tenant-dashboard/images/wsdl2java.png);
    }
    .tip-table td.java2wsdl {
        background-image: url(../../carbon/tenant-dashboard/images/java2wsdl.png);
    }
    .tip-table td.wsdl-validator {
        background-image: url(../../carbon/tenant-dashboard/images/wsdl-validator.png);
    }
    .tip-table td.modules {
        background-image: url(../../carbon/tenant-dashboard/images/modules.png);
    }
</style>
 <h2 class="dashboard-title">WSO2 Application Server quick start dashboard</h2>
        <table class="tip-table">
            <tr>
                <td class="tip-top service-hosting"></td>
                <td class="tip-empty"></td>
                <td class="tip-top web-applications"></td>
                <td class="tip-empty "></td>
                <td class="tip-top service-testing"></td>
                <td class="tip-empty "></td>
                <td class="tip-top message-tracing"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                          <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title" href="../service-mgt/index.jsp?region=region1&item=services_list_menu">Service Hosting</a> <br/>
						<%
							} else {
						%>
					    <h3>Service Hosting</h3> <br/>
					    <%
							}
						%>
                        <p>Different types of Web Services such as Axis2 Services, JAXWS Services, Jar Services or Spring Services can be deployed in Application Server. All configurations such as QoS can be easily configured here.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                          <%
							if (hasWebAppMgtPermission) {
						%>
                        <a class="tip-title" href="../webapp-list/index.jsp?region=region1&item=webapps_list_menu">Web Applications</a> <br />
						<%
							} else {
						%>
				      <h3>Web Applications</h3> <br />
				        <%
							}
						%>
                        <p>Web Application hosting features in AppServer supports deployment of Tomcat compliant Webapps. Deployed Webapps can be easily managed using the Webapp management facilities available in the management console.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                         <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title" href="../tryit/index.jsp?region=region5&item=tryit">Service Testing</a> <br/>
						<%
							} else {
						%>
                       <h3>Service Testing</h3> <br/>
                        <%
							}
						%>
                        <p>Tryit tool can be used as a simple Web Service client which can be used to try your services within AppServer itself.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                          <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title"  href="../tracer/index.jsp?region=region4&item=tracer_menu">Message Tracing</a> <br/>
						<%
							} else {
						%>
						<h3>Message Tracing</h3> <br/>
						<%
							}
						%>
                        <p>Trace the request and responses to your service. Message Tracing is a vital debugging tool when you have clients from heterogeneous platforms.</p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>
        <div class="tip-table-div"></div>
        <table class="tip-table">
            <tr>
                <td class="tip-top wsdl2java"></td>
                <td class="tip-empty"></td>
                <td class="tip-top java2wsdl"></td>
                <td class="tip-empty "></td>
                <td class="tip-top wsdl-validator"></td>
                <td class="tip-empty "></td>
                <td class="tip-top modules"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                          <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title" href="../wsdl2code/index.jsp?region=region5&item=wsdl2java_menu">WSDL2Java Tool</a> <br/>
						<%
							} else {
						%>
						<h3>WSDL2Java Tool</h3> <br/>
						<%
							}
						%>
                        <p>Use WSDL2Java tool in Web Application Server to convert Web Service WSDL to a set of Java objects.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                          <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title" href="../java2wsdl/index.jsp?region=region5&item=java2wsdl_menu">Java2WSDL Tool</a><br />
						<%
							} else {
						%>
						 <h3>Java2WSDL Tool</h3><br />
						<%
							}
						%>
                        <p>Use Java2WSDL tool in Web Application Server  make it easy to develop a new web service.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                      <%
							if (hasServiceMgtPermission) {
						%>
                        <a class="tip-title" href="../wsdl_validator/index.jsp?region=region5&item=wsdl_validator_menu">WSDL Validator</a> <br/>
						<%
							} else {
						%>
						<h3>WSDL Validator</h3> <br/>
						<%
							}
						%>
                        <p>Use WSDL Validator tool in Web Application Server to Validate WSDL by directly uploading them or providing a URL.</p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                      <%
							if (hasModMgtPermission) {
						%>
                        <a class="tip-title" href="../modulemgt/index.jsp?region=region1&item=modules_list_menu">Modules</a> <br/>
					    <%
							} else {
						%>
					    <h3>Modules</h3><br/>
					    <%
							}
						%>
                        <p>The WSO2 SOA platform has the capabilities of Axis2 to add modules to extend its capabilities. The global modules will affect all the services deployed within the server. </p>

                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>
        

<p>
    <br/>
</p> </div>
</div>
