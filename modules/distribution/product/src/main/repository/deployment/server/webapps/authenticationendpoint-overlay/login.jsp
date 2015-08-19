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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mobile Device Manager</title>

        <link rel="icon" href="favicon.png" type="image/x-icon" />
        <link href="assets/css/bootstrap.min.css" rel="stylesheet">
        <link href="assets/css/custom-common.css" rel="stylesheet">    
        <link href="assets/css/custom-desktop.css" rel="stylesheet">
        <link href="assets/css/custom-theme.css" rel="stylesheet">

        <!--[if lt IE 9]>
        <script src="js/html5shiv.min.js"></script>
        <script src="js/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>



	

    <!-- Part 1: Wrap all page content here -->
    <div id="wrap">
        <% if (request.getParameter("RelayState") != null && request.getParameter("RelayState").equals("/publisher")) {%>
          <!-- header -->
            <header>
            <div class="row wr-global-header">
                <div class="col-lg-12 app-logo">
                    <a href="/publisher"><img src="assets/img/logo.png" /><h2 class="app-title"><span>App Manager</span></h2></a>
                </div>
            </div>
            </header>
            <!-- /header -->
        <%
        }else{
        %>
          <!-- header -->
            <header>
            <div class="row wr-global-header">
                <div class="col-lg-12 app-logo">
                    <a href="/store"><img src="assets/img/logo.png" /><h2 class="app-title"><span>App Manager</span></h2></a>
                </div>
            </div>
            </header>
            <!-- /header -->
        <% } %>

        <!-- Begin page content -->



        <div class="row">
        <div class="col-md-12">

            <!-- content -->
            <div class="container col-xs-12 col-md-12 col-lg-3 col-centered wr-content wr-login col-centered">

                <div class="wr-header-control">
                    <h1 class="wr-title">Login</h1>
                    App Manager credentials to login.
                </div>

                <hr />

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
                                <div class="row">
                                    <div class="wr-validation-summary col-md-6">
                                        <p><%=errorMessage%></p>
                                    </div>
                                </div>
                            <% } %>

                            <form action="../commonauth" method="post" id="loginForm" class="form-horizontal" >
                                <%
                                    if(localAuthenticatorNames.size()>0) {

                                        if(localAuthenticatorNames.size()>0 && localAuthenticatorNames.contains("OpenIDAuthenticator")){
                                            hasLocalLoginOptions = true;
                                %>

                                <div class="row">
                                    <div class="col-md-6">
                                    
                                            <%@ include file="openid.jsp" %>

                                    </div>
                                </div>

                                <%
                                } else if(localAuthenticatorNames.size()>0 && localAuthenticatorNames.contains("BasicAuthenticator")) {
                                    hasLocalLoginOptions = true;
                                %>


                                    <div class="row">
                                        <div class="col-md-6">

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



            </div>
            <!-- /content -->

        </div>
    </div>  





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
    </div>
    <!-- container -->

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

   <!-- footer -->
    <footer class="footer">
        <p>App Manager v.1.1.0 | &copy; <script>document.write(new Date().getFullYear());</script> <a href="http://wso2.com/" target="_blank"><i class="fw fw-wso2"></i> Inc</a>. All Rights Reserved.</p>
    </footer>
    <!-- /footer -->

    </body>
    </html>

</fmt:bundle>

