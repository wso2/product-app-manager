<!DOCTYPE html>
<!--
~ Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.CharacterEncoder"%>

<fmt:bundle basename="org.wso2.carbon.identity.application.authentication.endpoint.i18n.Resources">

    <html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Login with WSO2 Identity Server</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- Le styles -->
        <link href="assets/css/bootstrap.min.css" rel="stylesheet">
        <link href="css/styles-appmgr.css" rel="stylesheet">
        <!--[if lt IE 8]>
        <link href="css/localstyles-ie7.css" rel="stylesheet">
        <![endif]-->

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="assets/js/html5.js"></script>
        <![endif]-->
        <script src="assets/js/jquery-1.7.1.min.js"></script>
        <script src="js/scripts.js"></script>
	<style>
	div.different-login-container a.truncate {
	  width: 148px;
	  white-space: nowrap;
	  overflow: hidden;
	  text-overflow: ellipsis;
	}
	</style>

    </head>

    <body>
    <!-- Part 1: Wrap all page content here -->
    <div id="wrap">
        <% if (request.getParameter("RelayState") != null && request.getParameter("RelayState").equals("/publisher")) {%>
        <div role="navigation" class="navbar navbar-inverse container-header">
            <div class="container logo-container">
                    <a href="/publisher/" class="navbar-brand"></a>
            </div>
        </div>
        <%
        }else{
        %>
        <div role="navigation" class="navbar navbar-inverse container-header">
            <div class="container logo-container">
                <a href="/store/" class="navbar-brand"></a>
            </div>
        </div>
        <% } %>

        <!-- Begin page content -->

        <div class="container-assets container">


    <div class="overlay" style="display:none"></div>
    <div class="header-strip">&nbsp;</div>
    <div class="header-back">
        <div class="container">
            <div class="row">
                <div class="span12">
                    <a class="logo">&nbsp</a>
                </div>
            </div>
        </div>
    </div>

    <div class="header-text">
        
    </div>
    <div class="container">
	<div class="row">
		<div class="span12">
			<h1>Please Login to continue</h1>
		</div>
	</div>
    </div>
    <!-- container -->
    <%@ page import="java.util.Map" %>
    <%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.CharacterEncoder" %>
    <%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.Constants" %>
    <%@ page import="org.wso2.carbon.identity.application.authentication.endpoint.util.TenantDataManager" %>

    <%

        request.getSession().invalidate();
        String queryString = request.getQueryString();
        Map<String, String> idpAuthenticatorMapping = null;
        if (request.getAttribute(Constants.IDP_AUTHENTICATOR_MAP) != null) {
            idpAuthenticatorMapping = (Map<String, String>)request.getAttribute(Constants.IDP_AUTHENTICATOR_MAP);
        }
        
        String errorMessage = "Authentication Failed! Please Retry";
        String loginFailed = "false";
        
        if (CharacterEncoder.getSafeText(request.getParameter(Constants.AUTH_FAILURE)) != null &&
                "true".equals(CharacterEncoder.getSafeText(request.getParameter(Constants
                .AUTH_FAILURE)))) {
            loginFailed = "true";
            
            if(CharacterEncoder.getSafeText(request.getParameter(Constants.AUTH_FAILURE_MSG)) !=
            null){
                errorMessage = (String) CharacterEncoder.getSafeText(request.getParameter
                (Constants.AUTH_FAILURE_MSG));
                
                if (errorMessage.equalsIgnoreCase("login.fail.message")) {
                    errorMessage = "Authentication Failed! Please Retry";
                }
            }
        }
    %>

    <script type="text/javascript">
        function doLogin() {
            var loginForm = document.getElementById('loginForm');
            loginForm.submit();
        }
    </script>
    
<% 

boolean hasLocalLoginOptions = false; 
List<String> localAuthenticatorNames = new ArrayList<String>();

if (idpAuthenticatorMapping.get(Constants.RESIDENT_IDP_RESERVED_NAME) != null){
	String authList = idpAuthenticatorMapping.get(Constants.RESIDENT_IDP_RESERVED_NAME);
	if (authList!=null){
		localAuthenticatorNames = Arrays.asList(authList.split(","));
	}
}


%>

<%if(localAuthenticatorNames.contains("BasicAuthenticator")){ %>
    <div id="local_auth_div" class="container main-login-container" style="margin-top:10px;">
<%} %>

		<% if ("true".equals(loginFailed)) { %>
            <div class="alert alert-error">
                <%=errorMessage%>
            </div>
	    <% } %>

        <form action="../commonauth" method="post" id="loginForm" class="form-horizontal" >
            <%
                if(localAuthenticatorNames.size()>0) {

                    if(localAuthenticatorNames.size()>0 && localAuthenticatorNames.contains("OpenIDAuthenticator")){
                    	hasLocalLoginOptions = true;
            %>

            <div class="row">
                <div class="span6">
                
                        <%@ include file="openid.jsp" %>

                </div>
            </div>

            <%
            } else if(localAuthenticatorNames.size()>0 && localAuthenticatorNames.contains("BasicAuthenticator")) {
            	hasLocalLoginOptions = true;
            %>


                <div class="row">
                    <div class="span6">

                        <%@ include file="basicauth.jsp" %>

                    </div>
                </div>
            <%
            }
            } 
            %>

    <%if(idpAuthenticatorMapping.get(Constants.RESIDENT_IDP_RESERVED_NAME) != null){ %>
	</div>
	<%} %>
	<%
        if ((hasLocalLoginOptions && localAuthenticatorNames.size() > 1) || (!hasLocalLoginOptions)
        		|| (hasLocalLoginOptions && idpAuthenticatorMapping.size() > 1)) {
    	%>
  <div class="container">
	<div class="row">
		<div class="span12">
		    <% if(hasLocalLoginOptions) { %>
			<h2>Other login options:</h2>
			<%} else { %>
			<script type="text/javascript">
			    document.getElementById('local_auth_div').style.display = 'block';
			</script>
			<%} %>
		</div>
	</div>
    </div>
    	
	<div class="container different-login-container">
            <div class="row">
                                               
                    <%
                        for (Map.Entry<String, String> idpEntry : idpAuthenticatorMapping.entrySet())  {
                            if(!idpEntry.getKey().equals(Constants.RESIDENT_IDP_RESERVED_NAME)) {
                            	String idpName = idpEntry.getKey();
                            	boolean isHubIdp = false;
                            	if (idpName.endsWith(".hub")){
                            		isHubIdp = true;
                            		idpName = idpName.substring(0, idpName.length()-4);
                            	}
                    %>
                              <div class="span3">
                                    <% if (isHubIdp) { %>
                                    <a href="#"  class="main-link"><%=idpName%></a>
                                    <div class="slidePopper" style="display:none">
				                        <input type="text" id="domainName" name="domainName"/>
				                        <input type="button" class="btn btn-primary go-btn" onClick="javascript: myFunction('<%=idpName%>','<%=idpEntry.getValue()%>','domainName')" value="Go" />
			                        </div>
			                        <%}else{ %>
			                              <a onclick="javascript: handleNoDomain('<%=idpName%>','<%=idpEntry.getValue()%>')"  class="main-link truncate" style="cursor:pointer" title="<%=idpName%>"><%=idpName%></a>			                        
			                        <%} %>
		                      </div>
                            <%}else if(localAuthenticatorNames.size()>0 && localAuthenticatorNames.contains("IWAAuthenticator")) {
                            %>
                            	<div class="span3">
                                <a onclick="javascript: handleNoDomain('<%=idpEntry.getKey()%>','IWAAuthenticator')"  class="main-link" style="cursor:pointer">IWA</a>
	                            </div>
	                       <% 
                            }

                         }%>
                        
                    
               
            </div>
	    <% } %>
        </form>
    </div>

    <script>
 	$(document).ready(function(){
		$('.main-link').click(function(){
			$('.main-link').next().hide();
			$(this).next().toggle('fast');
			var w = $(document).width();
			var h = $(document).height();
			$('.overlay').css("width",w+"px").css("height",h+"px").show();
		});
		$('.overlay').click(function(){$(this).hide();$('.main-link').next().hide();});
	
	});
        function myFunction(key, value, name)
        {
	    var object = document.getElementById(name);	
	    var domain = object.value;


            if (domain != "")
            {
                document.location = "../commonauth?idp=" + key + "&authenticator=" + value + "&sessionDataKey=<%=CharacterEncoder.getSafeText(request.getParameter("sessionDataKey"))%>&domain=" + domain;
            } else {
                document.location = "../commonauth?idp=" + key + "&authenticator=" + value + "&sessionDataKey=<%=CharacterEncoder.getSafeText(request.getParameter("sessionDataKey"))%>";
            }
        }
        
        function handleNoDomain(key, value)
        {


          document.location = "../commonauth?idp=" + key + "&authenticator=" + value + "&sessionDataKey=<%=CharacterEncoder.getSafeText(request.getParameter("sessionDataKey"))%>";
            
        }
        
    </script>
        </div>

        <div id="push"></div>
    </div>

    <div id="footer">
        <div class="container">
            <div class="row row-footer">
                <div class="col-md-6">
                    <span> &copy; 2015 WSO2 Inc. All Rights Reserved.</span>
                </div>
                <div class="col-md-5 pull-right txt-right">
                    <ul>

                        <li>

                            <a target="_blank"  href="http://wso2.com/products/app-manager/" class="powered-by-logo"></a>
                        </li>
                    </ul>

                </div>
            </div>

        </div>
    </div>
    </body>
    </html>

</fmt:bundle>

