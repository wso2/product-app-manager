<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>Book Your Travel - Booking</title>
<link rel="stylesheet" href="css/style.css" type="text/css"
	media="screen,projection,print" />
<link rel="stylesheet" href="css/theme-turqoise.css" id="template-color" />
<link rel="shortcut icon" href="images/favicon.ico" />
<link rel="stylesheet" href="css/styler.css" type="text/css"
	media="screen,projection,print" />
</head>
<body>

	<%@ page import="org.wso2.carbon.appmgt.sample.jwt.JWTProcessor"%>
	<%
		Map<String, String> map = null;
		String header = request.getHeader("X-JWT-Assertion");
		if (header != null) {
			System.out.println("header " + header);
			JWTProcessor obj = new JWTProcessor();
			System.out.println("obj " + obj.process(header));
			obj.process(header);
			map = obj.process(header);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println("Key : " + entry.getKey() + " Value : "
						+ entry.getValue());
			}
		}
	%>
	<!-- TEMPLATE STYLES -->


	<!--header-->
	<header>
		<div class="wrap clearfix">
			<!--logo-->
			<h1 class="logo">
				<a href="index.jsp" title="Book Your Travel - home"><img
					src="images/logo.png" alt="Book Your Travel" /></a>
			</h1>
	</header>
	<!--//header-->

	<!--main-->
	<div class="main" role="main">
		<div class="wrap clearfix">
			<!--main content-->
			<div class="content clearfix">
				
				<!--three-fourth content-->
				<section class="three-fourth">
					<form id="booking" method="post" action="booking-step2.jsp"
						class="booking">
						<fieldset>
							<h3>
								<span>01 </span>Traveller info
							</h3>
							<div class="row twins">
								<div class="f-item">
									<label for="first_name">First name</label> <input type="text"
										id="first_name" name="first_name" <%if (header != null) {%>
										value="<%=map.get("http://wso2.org/claims/nameoncard")%>"
										<%}%> />
								</div>
								<div class="f-item">
									<label for="last_name">Last name</label> <input type="text"
										id="last_name" name="last_name" <%if (header != null) {%>
										value="<%=map.get("http://wso2.org/claims/lastname")%>" <%}%> />
								</div>
							</div>

							<div class="row twins">
								<div class="f-item">
									<label for="email">Email address</label> <input type="email"
										id="email" name="email" />
								</div>
								<div class="f-item">
									<label for="confirm_email">Confirm email address</label> <input
										type="text" id="confirm_email" name="confirm_email" />
								</div>
								<span class="info">You’ll receive a confirmation email</span>
							</div>

							<div class="row twins">
								<div class="f-item">
									<label for="address">Street Address an Number</label> <input
										type="text" id="address" name="address"
										<%if (header != null) {%>
										value=<%=map.get("http://wso2.org/claims/streetaddress")%>
										<%}%> />
								</div>
								<div class="f-item">
									<label for="city">Town / City</label> <input type="text"
										id="city" name="city" <%if (header != null) {%>
										value="<%=map.get("http://wso2.org/claims/lastname")%>" <%}%> />
								</div>
							</div>

							<div class="row twins">
								<div class="f-item">
									<label for="zip">ZIP Code</label> <input type="text" id="zip"
										name="zip" <%if (header != null) {%>
										value="<%=map.get("http://wso2.org/claims/zipcode")%>" <%}%> />
								</div>
								<div class="f-item">
									<label for="country">Country</label> <input type="text"
										id="country" name="country" <%if (header != null) {%>
										value="<%=map.get("http://wso2.org/claims/country")%>" <%}%> />
								</div>
							</div>

							<div class="row">
								<div class="f-item">
									<label>Special requirements: <span>(Not
											Guaranteed)</span></label>
									<textarea rows="10" cols="10"></textarea>
								</div>
								<span class="info">Please write your requests in English.</span>
							</div>
							<input type="submit" class="gradient-button"
								value="Proceed to next step" id="next-step" />
						</fieldset>
					</form>
				</section>
				<!--//three-fourth content-->

				<!--right sidebar-->

				<!--//right sidebar-->
			</div>
			<!--//main content-->
		</div>
	</div>
	<!--//main-->

	<!--footer-->
	<footer>
		<div class="wrap clearfix">
			<!--column-->
			<article class="one-fourth">
				<h3>Book Your Travel</h3>
				<p>1400 Pennsylvania Ave. Washington, DC</p>
				<p>
					<em>P:</em> 24/7 customer support: 1-555-555-5555
				</p>
				<p>
					<em>E:</em> <a href="#" title="booking@mail.com">booking@mail.com</a>
				</p>
			</article>
			
	</footer>
	<!--//footer-->
	<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
	<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
	<script
		src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/modernizr.js"></script>
	<script type="text/javascript" src="js/jquery.uniform.min.js"></script>
	<script type="text/javascript" src="js/jquery.prettyPhoto.js"></script>
	<script type="text/javascript" src="js/selectnav.js"></script>
	<script type="text/javascript" src="js/scripts.js"></script>

	<script>
		selectnav('nav');
	</script>

</body>
</html>