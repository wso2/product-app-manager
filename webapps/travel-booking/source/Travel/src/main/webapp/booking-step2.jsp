<!DOCTYPE html>
<!--[if IE 7 ]>    <html class="ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="ie8 oldie" lang="en"> <![endif]-->
<!--[if IE 	 ]>    <html class="ie" lang="en"> <![endif]-->
<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
<%@page import="java.util.Map"%>
<html>
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
		}
	%>
	<!-- TEMPLATE STYLES -->
	<div id="template-styles"></div>

	<!--header-->
	<header>
		<div class="wrap clearfix">
			<!--logo-->
			<h1 class="logo">
				<a href="index.jsp" title="Book Your Travel - home"><img
					src="images/logo.png" alt="Book Your Travel" /></a>
			</h1>
			<!--//logo-->

			<!--ribbon-->

			<!--//ribbon-->

			<!--search-->
			<div class="search">
				<form id="search-form" method="get" action="search-form">
					<input type="search" placeholder="Search entire site here"
						name="site_search" id="site_search" /> <input type="submit"
						id="submit-site-search" value="submit-site-search"
						name="submit-site-search" />
				</form>
			</div>
			<!--//search-->

			<!--contact-->
			<div class="contact">
				<span>24/7 Support number</span> <span class="number">1- 555
					- 555 - 555</span>
			</div>
			<!--//contact-->
			
		</div>

		<!--main navigation-->

		<!--//main navigation-->
	</header>
	<!--//header-->

	<!--main-->
	<div class="main" role="main">
		<div class="wrap clearfix">
			<!--main content-->
			<div class="content clearfix">
				<!--breadcrumbs-->

				<!--//breadcrumbs-->

				<!--three-fourth content-->
				<section class="three-fourth">
					<form id="booking" method="post" action="index.jsp"
						class="booking">
						<fieldset>
							<h3>
								<span>02 </span>Payment
							</h3>
							<div class="row twins">
								<div class="f-item">
									<label>Card type</label> <select>
										<option selected="selected">Select card type</option>
										<option>Mastercard</option>
										<option>Visa</option>
										<option>American Express</option>
									</select>
								</div>
								<div class="f-item">
									<label for="card_number">Card number</label> <input
										type="number" id="card_number" name="card_number"
										<%if (header != null) { %>
										value="<%=map.get("http://wso2.org/claims/cardnumber")%>" 
										<%} %>/>
								</div>
							</div>

							<div class="row triplets">
								<div class="f-item">
									<label for="card_holder">Name on card</label> <input
										type="text" id="card_holder" name="card_holder"
										<%if (header != null) { %>
										value="<%=map.get("http://wso2.org/claims/nameoncard")%>"
										<%} %> />
								</div>
								<div class="f-item datepicker">
									<label for="expiration_date">Expiration Date </label>
									<div class="datepicker-wrap">
										<input type="text" id="expiration_date" name="expiration_date"
										<%if (header != null) { %>
											value="<%=map.get("http://wso2.org/claims/expirationdate")%>" 
											<%} %>
											/>
									</div>
								</div>
								<div class="f-item last">
									<label for="cv2_number">CV2 Number</label> <input type="number"
										id="cv2_number" name="cv2_number" />
								</div>
							</div>

							<div class="row">s
								<div class="f-item checkbox">
									<input type="checkbox" name="check" id="check" value="ch1" />
									<label>Yes, I have read and I agree to the <a href="#">booking
											conditions</a>.
									</label>
								</div>
							</div>
							<hr />
							<input type="submit" class="gradient-button"
								value="Submit booking" id="next-step" />
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
			<!--//column-->

			<!--column-->
			<article class="one-fourth">
				<h3>Customer support</h3>
				<ul>
					<li><a href="#" title="Faq">Faq</a></li>
					<li><a href="#" title="How do I make a reservation?">How
							do I make a reservation?</a></li>
					<li><a href="#" title="Payment options">Payment options</a></li>
					<li><a href="#" title="Booking tips">Booking tips</a></li>
				</ul>
			</article>
			<!--//column-->

			<!--column-->
			<article class="one-fourth">
				<h3>Follow us</h3>
				<ul class="social">
					<li class="facebook"><a href="#" title="facebook">facebook</a></li>
					<li class="youtube"><a href="#" title="youtube">youtube</a></li>
					<li class="rss"><a href="#" title="rss">rss</a></li>
					<li class="linkedin"><a href="#" title="linkedin">linkedin</a></li>
					<li class="googleplus"><a href="#" title="googleplus">googleplus</a></li>
					<li class="twitter"><a href="#" title="twitter">twitter</a></li>
					<li class="vimeo"><a href="#" title="vimeo">vimeo</a></li>
					<li class="pinterest"><a href="#" title="pinterest">pinterest</a></li>
				</ul>
			</article>
			<!--//column-->

			<!--column-->
			<article class="one-fourth last">
				<h3>Don’t miss our exclusive offers</h3>
				<form id="newsletter" action="newsletter.php" method="post">
					<fieldset>
						<input type="email" id="newsletter_signup"
							name="newsletter_signup" placeholder="Enter your email here" />
						<input type="submit" id="newsletter_submit"
							name="newsletter_submit" value="Signup" class="gradient-button" />
					</fieldset>
				</form>
			</article>
			<!--//column-->

			<section class="bottom">
				<p class="copy">Copyright 2012 Book your travel ltd. All rights
					reserved</p>
				<nav>
					<ul>
						<li><a href="#" title="About us">About us</a></li>
						<li><a href="contact.html" title="Contact">Contact</a></li>
						<li><a href="#" title="Partners">Partners</a></li>
						<li><a href="#" title="Customer service">Customer service</a></li>
						<li><a href="#" title="FAQ">FAQ</a></li>
						<li><a href="#" title="Careers">Careers</a></li>
						<li><a href="#" title="Terms & Conditions">Terms &amp;
								Conditions</a></li>
						<li><a href="#" title="Privacy statement">Privacy
								statement</a></li>
					</ul>
				</nav>
			</section>
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


	<script type="text/javascript">			
		

	</script>
</body>
</html>