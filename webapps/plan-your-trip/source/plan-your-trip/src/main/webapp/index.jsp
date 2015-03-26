<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.sun.jndi.cosnaming.IiopUrl.Address"%>
<html>
  <head>
    <title>Plan your Trip!</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"></meta>
    <script language="javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript" src="invokeStatistcs.js" ></script>
    <script type="text/javascript">
        invokeStatistics();
    </script>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
      <script src="raphael-min.js" type="text/javascript"></script>
      <style type="text/css">
          html, body { height: 100%; }
          body {
              background-image:url("images/sand.png") ;
              background-position: right center;
              background-size:cover;
              background-color:#333;
              display:table;
              table-layout:fixed;
              width:100%;
          }
          div.map_image {
              margin:0 auto;
              background-color: red;
              background-image: url("images/BlankMap-World-1ce.png");
              background-position: left top;
              background-repeat: no-repeat;
              height: 599px;
              width: 1150px;
          }
      </style>
  </head>

<%@ page import="org.wso2.carbon.appmgt.sample.jwt.*" %>
<%@ page import="java.util.*" %>
<%
	String userName = "Subject";
	String telePhone = "http://wso2.org/claims/telephone";
	String streetAddress = "http://wso2.org/claims/streetaddress";
	String ffId ="http://wso2.org/ffid";


    String header = request.getHeader("X-JWT-Assertion");
    System.out.println("Header : "+header);
    System.out.println("REQUEST URL :"+request.getRequestURL());
    System.out.println("Context Path :"+request.getContextPath());
    Map<String,String> headerMap = null;
    Map<String,Integer> routeMap = null;
    int route = 1;
    
    if(header !=null && !header.equals("")){
    
     JWTProcessor obj = new JWTProcessor();
     headerMap= obj.process(header);
    
    if(session.getAttribute("ROUTE_MAP") == null){
        routeMap = new HashMap();
        session.setAttribute("ROUTE_MAP",routeMap);
    }else{
        routeMap = (HashMap) session.getAttribute("ROUTE_MAP");
    }

    if(routeMap.get(headerMap.get(userName)) != null) {
        route = routeMap.get(headerMap.get(userName));
    }else{
        Random random = new Random();
        route = random.nextInt(5) + 1;
        routeMap.put(headerMap.get(userName),route);
    }
    }else if (session.getAttribute("LOGGED_IN_USER") != "admin"){
          response.sendRedirect(request.getContextPath()+"/login.jsp");
    }
    
    if(request.getParameter("action") != null && request.getParameter("action").equals("logout")){
        session.invalidate();
        response.sendRedirect(request.getContextPath()+"/login.jsp");
        return;
    }

%>
    <div class="container">
      <nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Your Flight Itinerary</a>
    </div>
    <% if (headerMap!=null && headerMap.get(userName) != null) {
    %>
		<input type="hidden" id="username" value="<%=headerMap.get(userName).replace("\"","")%>">
	<%} %>
    <input type="hidden" id="route_plan" value="<% out.println(route); %>">

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav navbar-right">
        <li><a href=<% out.println(".?action=logout"); %>><span class="glyphicon glyphicon-log-out"></span>Logout</a></li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>

      <div class="row marketing">
        <div class="col-lg-1">
        </div>
        <div class="col-lg-2">
          <h4>Name</h4>
          <%
            if(session.getAttribute("LOGGED_IN_USER") == "admin"){
                out.println("Administrator");
              }else if (headerMap!=null){
                out.println(headerMap.get(userName).replace("\"",""));
              }
          %>
        </p>
        </div>
        <div class="col-lg-3">
          <h4>Address</h4>
          <p><%
              if(session.getAttribute("LOGGED_IN_USER") == "admin"){
                  out.println("787 Castro St, Mountain View, CA 94041, United States");
              }else if (headerMap!=null){
                  out.println(headerMap.get(streetAddress).replace("\"",""));
              }
          %></p>
        </div>
        <div class="col-lg-3">
          <h4>Frequent Flyer ID</h4>
          <p><%
              if(session.getAttribute("LOGGED_IN_USER")  == "admin"){
                  out.println("FF XXXXXX");
              }else if (headerMap!=null){
                  out.println(headerMap.get(ffId).replace("\"",""));
              }
          %></p>
        </div>
        <div class="col-lg-2">
          <h4>Telephone No.</h4>
          <p><%
              if(session.getAttribute("LOGGED_IN_USER") == "admin"){
                  out.println("(408) 754-7388");
              }else if(headerMap!=null){
                  out.println(headerMap.get(telePhone).replace("\"",""));
              }
          %></p>
        </div>
        <div class="col-lg-1">
        </div>
      </div>

        <div class="map_image">
            <div id="mapCanvas" width="1150" height="599">
            </div>
        <%--<div id="listdiv" style="width:200px; overflow:auto; height:500px; float:right; background-color:#EEEEEE;"></div>--%>
        <%--<div id="mapdiv" style="margin-right:200px; background-color:#EEEEEE; height: 500px;"></div>--%>
        </div>
      <div class="footer">
        <p>&copy; Company 2014</p>
      </div>

    </div>

  <script type="text/javascript">
//      var c = document.getElementById('mapCanvas');
//      var ctx = c.getContext("2d");
//      ctx.fillStyle = "#FF0000";
//      ctx.beginPath();
//      ctx.arc(95,50,40,0,2*Math.PI);
//      ctx.stroke();

    var paper = new Raphael(document.getElementById('mapCanvas'), 1150, 599);
    var route = parseInt(document.getElementById("route_plan").value);

    if(route===1) {
        var qatar = paper.circle(680, 200, 5);
        qatar.attr("fill", "#FF0000");
        var qatar_text = paper.text(680, 215, "Qatar");
        qatar_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        qatar_text.attr("fill", "#63554a");

        var sauPalo = paper.circle(300, 290, 5);
        sauPalo.attr("fill", "#FF0000");
        var sau_palo_text = paper.text(300, 305, "Say Palo");
        sau_palo_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        sau_palo_text.attr("fill", "#63554a");

        var perth = paper.circle(890, 400, 5);
        perth.attr("fill", "#FF0000");
        var perth_text = paper.text(890, 415, "Perth");
        perth_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        perth_text.attr("fill", "#63554a");

        var path1 = paper.path(['M', 300, 290, 'Q', 400, 20, 680, 200]);
        path1.attr ("stroke", "#14A2B1");
        path1.attr ("stroke-width", "2");
        var path2 =paper.path(['M', 680, 200, 'Q', 950, 5, 890, 400]);
        path2.attr ("stroke", "#14A2B1");
        path2.attr ("stroke-width", "2");

    }if (route===2){
        var sf = paper.circle(135, 155, 5);
        sf.attr("fill", "#FF0000");
        var sf_text = paper.text(135, 170, "San Fransisco");
        sf_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        sf_text.attr("fill", "#63554a");

        var jkf = paper.circle(285, 145, 5);
        jkf.attr("fill", "#FF0000");
        var jkf_text = paper.text(285, 160, "New York");
        jkf_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        jkf_text.attr("fill", "#63554a");

        var doha = paper.circle(680, 200, 5);
        doha.attr("fill", "#FF0000");
        var doha_text = paper.text(680, 215, "Qatar");
        doha_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        doha_text.attr("fill", "#63554a");

        var tokyo = paper.circle(955, 165, 5);
        tokyo.attr("fill", "#FF0000");
        var tokyo_text = paper.text(955, 180, "Tokyo");
        tokyo_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        tokyo_text.attr("fill", "#63554a");

        var path1 = paper.path(['M', 135, 155, 'Q', 200, 5, 285, 145]);
        path1.attr ("stroke", "#14A2B1");
        path1.attr ("stroke-width", "2");
        var path2 =paper.path(['M', 285, 145, 'Q', 500, 5, 680, 200]);
        path2.attr ("stroke", "#14A2B1");
        path2.attr ("stroke-width", "2");
        var path3 =paper.path(['M', 680, 200, 'Q', 800, 5, 955, 165]);
        path3.attr ("stroke", "#14A2B1");
        path3.attr ("stroke-width", "2");
    }if (route===3){

        var sl = paper.circle(782, 265, 5);
        sl.attr("fill", "#FF0000");
        var sl_text = paper.text(782, 280, "Colombo");
        sl_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        sl_text.attr("fill", "#63554a");

        var perth = paper.circle(890, 400, 5);
        perth.attr("fill", "#FF0000");
        var perth_text = paper.text(890, 415, "Perth");
        perth_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        perth_text.attr("fill", "#63554a");

        var fiji = paper.circle(1100, 350, 5);
        fiji.attr("fill", "#FF0000");
        var fiji_text = paper.text(1100, 365, "Fiji");
        fiji_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        fiji_text.attr("fill", "#63554a");

        var path1 = paper.path(['M', 782, 265, 'Q', 850, 5, 890, 400]);
        path1.attr ("stroke", "#14A2B1");
        path1.attr ("stroke-width", "2");
        var path2 =paper.path(['M', 890, 400, 'Q', 980, 5, 1100, 350]);
        path2.attr ("stroke", "#14A2B1");
        path2.attr ("stroke-width", "2");
}if (route===4){

        var sl = paper.circle(782, 265, 5);
        sl.attr("fill", "#FF0000");
        var sl_text = paper.text(782, 280, "Colombo");
        sl_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        sl_text.attr("fill", "#63554a");

        var doha = paper.circle(680, 200, 5);
        doha.attr("fill", "#FF0000");
        var doha_text = paper.text(680, 215, "Qatar");
        doha_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        doha_text.attr("fill", "#63554a");

        var london = paper.circle(515, 105, 5);
        london.attr("fill", "#FF0000");
        var london_text = paper.text(515, 120, "Heathrow");
        london_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        london_text.attr("fill", "#63554a");

        var path1 = paper.path(['M', 782, 265, 'Q', 750, 5, 680, 200]);
        path1.attr ("stroke", "#14A2B1");
        path1.attr ("stroke-width", "2");
        var path2 =paper.path(['M', 680, 200, 'Q', 600, 5, 515, 105]);
        path2.attr ("stroke", "#14A2B1");
        path2.attr ("stroke-width", "2");
}if (route===5){

        var sl = paper.circle(782, 265, 5);
        sl.attr("fill", "#FF0000");
        var sl_text = paper.text(782, 280, "Colombo");
        sl_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        sl_text.attr("fill", "#63554a");

        var doha = paper.circle(680, 200, 5);
        doha.attr("fill", "#FF0000");
        var doha_text = paper.text(680, 215, "Qatar");
        doha_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        doha_text.attr("fill", "#63554a");

        var london = paper.circle(515, 145, 5);
        london.attr("fill", "#FF0000");
        var london_text = paper.text(515, 160, "Barcelona");
        london_text.attr({'font-size': 15, 'font-family': 'FranklinGothicFSCondensed-1, FranklinGothicFSCondensed-2'});
        london_text.attr("fill", "#63554a");

        var path1 = paper.path(['M', 782, 265, 'Q', 750, 5, 680, 200]);
        path1.attr ("stroke", "#14A2B1");
        path1.attr ("stroke-width", "2");
        var path2 =paper.path(['M', 680, 200, 'Q', 600, 5, 515, 145]);
        path2.attr ("stroke", "#14A2B1");
        path2.attr ("stroke-width", "2");
}


  </script>
  </body>
</html>
