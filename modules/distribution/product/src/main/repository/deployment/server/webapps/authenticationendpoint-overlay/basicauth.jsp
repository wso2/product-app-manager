<%--
  ~ Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
  ~
  ~ WSO2 Inc. licenses this file to you under the Apache License,
  ~ Version 2.0 (the "License"); you may not use this file except
  ~ in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<%@ page import="org.owasp.encoder.Encode" %>

<%
    String type = request.getParameter("type");
    if ("samlsso".equals(type)) {

%>
<form action="/samlsso" method="post" id="loginForm">
    <input id="tocommonauth" name="tocommonauth" type="hidden" value="true">
<%
    } else if ("oauth2".equals(type)){
%>
    <form action="/oauth2/authorize" method="post" id="loginForm">
        <input id="tocommonauth" name="tocommonauth" type="hidden" value="true">

<%
    } else {
%>

<form action="../commonauth" method="post" id="loginForm">

    <%
        }
    %>

    <% if (Boolean.parseBoolean(loginFailed)) { %>
    <div class="alert alert-danger" id="error-msg"><%= Encode.forHtml(errorMessage) %></div>
    <%}else if((Boolean.TRUE.toString()).equals(request.getParameter("authz_failure"))){%>
    <div class="alert alert-danger" id="error-msg">You are not authorized to login
    </div>
    <%}%>

    <!-- Username -->
        <div class="control-group">
            <label class="control-label" for="username"><fmt:message key='username'/>:</label>

        <div class="controls">
                <input class="input-xlarge" type="text" id='username' name="username" size='30'/>
            </div>
        </div>


    <!--Password-->
    <div class="control-group">
        <label class="control-label" for="password"><fmt:message key='password'/>:</label>

        <div class="controls">
            <input type="password" id='password' name="password"  class="input-xlarge" size='30'/>
            <input type="hidden" name="sessionDataKey" value='<%=Encode.forHtmlAttribute
            (request.getParameter("sessionDataKey"))%>'/>
            <label class="checkbox" style="margin-top:10px"><input type="checkbox" id="chkRemember" name="chkRemember"><fmt:message key='remember.me'/></label>
        </div>
    </div>


    <div class="form-actions">
		<input type="submit" value='<fmt:message key='login'/>' class="btn btn-primary">
		<%  if(request.getParameter("RelayState") != null && !request.getParameter("RelayState").equals("/publisher") ){ %>
		<input type="button" value="Back to Store" class="btn btn-default" onclick="location.href='<%=request.getParameter("RelayState")%>';">
		<% } %>
    </div>
    


    <div class="clearfix"></div>
</form>


