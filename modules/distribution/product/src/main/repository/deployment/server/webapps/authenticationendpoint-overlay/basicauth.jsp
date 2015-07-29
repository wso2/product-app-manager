<%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.CharacterEncoder"%>
<div id="loginTable1" class="identity-box">
    <%
        loginFailed = CharacterEncoder.getSafeText(request.getParameter("loginFailed"));
        if (loginFailed != null) {

    %>
            <div class="alert alert-error">
                <fmt:message key='<%=CharacterEncoder.getSafeText(request.getParameter
                ("errorMessage"))%>'/>
            </div>
    <% } %>

    <% if (CharacterEncoder.getSafeText(request.getParameter("username")) == null || "".equals
    (CharacterEncoder.getSafeText(request.getParameter("username")).trim())) { %>

        <!-- Username -->
        <div class="control-group">
            <label class="control-label" for="username"><fmt:message key='username'/>:</label>

            <div class="controls">
                <input class="input-xlarge" type="text" id='username' name="username" size='30'/>
            </div>
        </div>

    <%} else { %>

        <input type="hidden" id='username' name='username' value='<%=CharacterEncoder.getSafeText
        (request.getParameter("username"))%>'/>

    <% } %>

    <!--Password-->
    <div class="control-group">
        <label class="control-label" for="password"><fmt:message key='password'/>:</label>

        <div class="controls">
            <input type="password" id='password' name="password"  class="input-xlarge" size='30'/>
            <input type="hidden" name="sessionDataKey" value='<%=CharacterEncoder.getSafeText(request.getParameter("sessionDataKey"))%>'/>
            <label class="checkbox" style="margin-top:10px"><input type="checkbox" id="chkRemember" name="chkRemember"><fmt:message key='remember.me'/></label>
        </div>
    </div>

    <div class="form-actions">
        <input type="submit" value='<fmt:message key='login'/>' class="btn btn-primary">
        <% if(request.getParameter("RelayState") != null && !request.getParameter("RelayState").equals("/publisher") ){ %>
        <input type="button" value="Back to Store" class="btn btn-default" onclick="location.href='<%=request.getParameter("RelayState")%>';">
        <% } %>
    </div>

</div>

