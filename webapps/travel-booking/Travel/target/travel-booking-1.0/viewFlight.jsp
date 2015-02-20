<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>Book Your Travel - Booking</title>
<link rel="stylesheet" href="css/style.css" type="text/css"
	media="screen,projection,print" />
<link rel="stylesheet" href="css/prettyPhoto.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="css/theme-turqoise.css" id="template-color" />
<link rel="shortcut icon" href="images/favicon.ico" />
<link rel="stylesheet" href="css/styler.css" type="text/css"
	media="screen,projection,print" />
</head>
<body>
	<%
		int n = (int) (((Math.random()) * 8) + 1);
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
	<%
		String destination3 = request.getParameter("startDestination");
		System.out.println("jsp start " + destination3);
		String destination1 = request.getParameter("endDestination");
	%>
	<div class="main" role="main">
		<div class="wrap clearfix">
			<!--main content-->

			<div class="content clearfix">
				<!--breadcrumbs-->

				<!--//breadcrumbs-->

				<!--three-fourth content-->
				<section class="three-fourth">
				<div class="form" id="form3"></div>
			</div>

			<form id="booking" method="post" action="booking-step1.jsp"
				class="booking">
				<div class="datagrid">
					<table cellspacing="10"> 


						<tbody>
							<%
								for (int i = 0; i < n; i++) {
							%>
							<tr>
								<td ><img src="images/<%=i%>.jpeg" alt="" border="3"
									/></td>
								<td>9:35p</br>CMB</br><%=destination3%></br>
								</td>
								<td>9:35p</br>CMB</br><%=destination1%></br>
								</td>
								<td>11h 30m, 1 stop</td>
								<td>$421 .29</br>roundtrip per person
								</td>
								<td><input type="submit" class="gradient-button"
									value="Book your Flight" id="next-step" /></td>
							</tr>
							<%
								}
							%>
						</tbody>
					</table>
				</div>

			</form>
			</section>
			<!--//three-fourth content-->


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



	</div>
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