<%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.CharacterEncoder"%>
<div id="loginTable1" class="identity-box">
    <%
        loginFailed = CharacterEncoder.getSafeText(request.getParameter("loginFailed"));
        if (loginFailed != null) {

    %>
            <div class="wr-validation-summary">
               <p> <fmt:message key='<%=CharacterEncoder.getSafeText(request.getParameter
                ("errorMessage"))%>'/></p>
            </div>

    <% } %>

    <% if (CharacterEncoder.getSafeText(request.getParameter("username")) == null || "".equals
    (CharacterEncoder.getSafeText(request.getParameter("username")).trim())) { %>



            <label class="wr-input-label"><fmt:message key='username'/>:</label>
                <div class="wr-input-control">
                    <input class="wr-input-control" type="text" id='username' name="username"/>
                </div>
        
      

    <%} else { %>

        <input type="hidden" id='username' name='username' value='<%=CharacterEncoder.getSafeText
        (request.getParameter("username"))%>'/>

    <% } %>

     <label class="wr-input-label"><fmt:message key='password'/>:</label>
                <div class="wr-input-control">
                   <input type="password" id='password' name="password"  class="wr-input-control"/>
                </div>

    <!--Password-->
    <div class="control-group">
        <label class="control-label" for="password"></label>

        <div class="controls">
            <input type="hidden" name="sessionDataKey" value='<%=CharacterEncoder.getSafeText(request.getParameter("sessionDataKey"))%>'/>
            <label class="checkbox" style="margin-top:10px"><input type="checkbox" id="chkRemember" name="chkRemember"><fmt:message key='remember.me'/></label>
        </div>
    </div>

    <div class="form-actions">
        <input type="submit" value='<fmt:message key='login'/>' class="btn btn-primary">
        <% if(request.getParameter("RelayState") != null && !request.getParameter("RelayState").equals("/publisher") ){ %>
        <input type="button" value="Back to Store" class="btn btn-default btn-primary" onclick="location.href='<%=request.getParameter("RelayState")%>';">
        <% } %>
    </div>

</div>

