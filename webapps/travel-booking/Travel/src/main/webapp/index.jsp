<!DOCTYPE html>
<!--[if IE 7 ]>    <html class="ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="ie8 oldie" lang="en"> <![endif]-->
<!--[if IE 	 ]>    <html class="ie" lang="en"> <![endif]-->
<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>Book Your Travel</title>
<link rel="stylesheet" href="css/style.css" type="text/css"
	media="screen,projection,print" />
<link rel="stylesheet" href="css/prettyPhoto.css" type="text/css"
	media="screen" />
<link rel="stylesheet" href="css/theme-turqoise.css" id="template-color" />
<link rel="shortcut icon" href="images/favicon.ico" />


<link rel="stylesheet" href="styler.css" type="text/css"
	media="screen,projection,print" />
 <style type="text/css">
            #l {
                font: normal 15px courier !important;
            }
        </style>


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


	<!--header-->
	<header>
		<div class="wrap clearfix">
			<!--logo-->
			<h1 class="logo">
				<a href="index.jsp" title="Book Your Travel - home"><img
					src="images/logo.png" alt="Book Your Travel" /></a>
			</h1>
			
			<div class="contact">
			
				
				
			</div>
		</div>
		<div class="wrap clearfix">
			
			<!--ribbon-->
			<div class="ribbon">
				<nav>
					<ul class="profile-nav">
						<li class="active"><a href="#" title="My Account">My
								Account</a></li>
						<li><a href="logout" title="Logout">Logout</a></li>
						<li><a href="my_account.html" title="Settings">Settings</a></li>
					</ul>

				</nav>
			</div>
			

		</div>

	</header>
	<!--//header-->

	<!--slider-->
	<section class="slider clearfix">
		<div id="sequence">
			<ul class="sequence-canvas">
				<li>
					<div class="info animate-in">
						<h2>Last minute Winter escapes</h2>
						<br />
						<p>January 2013 holidays 40% off! An unique opportunity to
							realize your dreams</p>
					</div> <img class="main-image animate-in" src="images/img3.jpg" alt="" />
				</li>
				<li>
					<div class="info animate-in">
						<h2>Check out our top weekly deals</h2>
						<br />
						<p>Save Now. Book Later.</p>
					</div> <img class="main-image animate-in" src="images/img4.jpg" alt="" />
				</li>
				<li>
					<div class="info animate-in">
						<h2>Check out last minute flight, hotel &amp; vacation
							offers!</h2>
						<br />
						<p>Save up to 50%!</p>
					</div> <img class="main-image animate-in" src="images/img1.jpg" alt="" />
				</li>
			</ul>
		</div>
	</section>
	<!--//slider-->

	<!--search-->
	<div class="main-search">
		<form id="main-search" method="post"
			action="authenticate<%if (header != null) {
				out.print("?header=" + header);
			}%>">
			

			<div class="forms">


				<!--form flight-->
				<div class="form" id="form3">
					<!--column-->
					<div class="column">
						<h4>
							<span>02</span> Where?
						</h4>
						<div class="f-item">
							<label for="destination3">Leaving from</label> <select
								name="destination3">
								<option>I Don't Mind</option>
								<option>Africa</option>
								<option>Alaska</option>
								<option>Alaska - Gulf Northbound</option>
								<option>Alaska - Gulf Southbound</option>
								<option>Antarctica</option>
								<option>Arctic</option>
								<option>Australia</option>
								<option>Bahamas</option>
								<option>Bermuda</option>
								<option>Brazil</option>
								<option>Canada/New England</option>
								<option>Canada</option>
								<option>Caribbean</option>
								<option>Caribbean - Eastern</option>
								<option>Caribbean - Southern</option>
								<option>Caribbean - Western</option>
								<option>Coastal</option>
								<option>Costa Rica</option>
								<option>Cruise To Nowhere</option>
								<option>Europe</option>
								<option>Northern Europe</option>
								<option>Europe - Western</option>
								<option>Europe (Greenland)</option>
								<option>Europe (Norway)</option>
								<option>Far East</option>
								<option>Greece</option>
								<option>Hawaii</option>
								<option>India</option>
								<option>Mediterranean</option>
								<option>Mediterranean - Eastern</option>
								<option>Mexico</option>
								<option>Middle East</option>
								<option>New England</option>
								<option>New Zealand</option>
								<option>Pacific Coastal</option>
								<option>Pacific Northwest</option>
								<option>Panama Canal</option>
								<option>Russia</option>
								<option>South America</option>
								<option>South Pacific</option>
								<option>Southeast Asia</option>
								<option>Tahiti</option>
								<option>Transatlantic</option>
								<option>Transcanal</option>
								<option>Transpacific</option>

							</select>

						</div>
						<div class="f-item">
							<label for="destination4">Going to</label> <select
								name="destination1">
								<option>I Don't Mind</option>
								<option>Africa</option>
								<option>Alaska</option>
								<option>Alaska - Gulf Northbound</option>
								<option>Alaska - Gulf Southbound</option>
								<option>Antarctica</option>
								<option>Arctic</option>
								<option>Australia</option>
								<option>Bahamas</option>
								<option>Bermuda</option>
								<option>Brazil</option>
								<option>Canada/New England</option>
								<option>Canada</option>
								<option>Caribbean</option>
								<option>Caribbean - Eastern</option>
								<option>Caribbean - Southern</option>
								<option>Caribbean - Western</option>
								<option>Coastal</option>
								<option>Costa Rica</option>
								<option>Cruise To Nowhere</option>
								<option>Europe</option>
								<option>Northern Europe</option>
								<option>Europe - Western</option>
								<option>Europe (Greenland)</option>
								<option>Europe (Norway)</option>
								<option>Far East</option>
								<option>Greece</option>
								<option>Hawaii</option>
								<option>India</option>
								<option>Mediterranean</option>
								<option>Mediterranean - Eastern</option>
								<option>Mexico</option>
								<option>Middle East</option>
								<option>New England</option>
								<option>New Zealand</option>
								<option>Pacific Coastal</option>
								<option>Pacific Northwest</option>
								<option>Panama Canal</option>
								<option>Russia</option>
								<option>South America</option>
								<option>South Pacific</option>
								<option>Southeast Asia</option>
								<option>Tahiti</option>
								<option>Transatlantic</option>
								<option>Transcanal</option>
								<option>Transpacific</option>

							</select>

						</div>
					</div>
					<!--//column-->

					<!--column-->
					<div class="column two-childs">
						<h4>
							<span>03</span> When?
						</h4>
						<div class="f-item datepicker">
							<label for="datepicker6">Departing on</label>
							<div class="datepicker-wrap">
								<input type="text" placeholder="click to show datepicker"
									id="example1" name="example1"><img
									class="ui-datepicker-trigger" src="images/calendar.png"
									alt="..." title="..."></img>
							</div>

						</div>
						<div class="f-item datepicker">
							<label for="datepicker7">Arriving on</label>
							<div class="datepicker-wrap">
								<input type="text" placeholder="click to show datepicker"
									id="example2" name="example2"> <img
									class="ui-datepicker-trigger" src="images/calendar.png"
									alt="..." title="..."></img>
							</div>

						</div>
					</div>
					<!--//column-->

					<!--column-->
					<div class="column triplets">
						<h4>
							<span>04</span> Who?
						</h4>
						<div class="f-item spinner">
							<label for="spinner6">Adults</label> <input type="text"
								placeholder="" id="spinner6" name="spinner6" />
						</div>
						<div class="f-item spinner">
							<label for="spinner7">Children</label> <input type="text"
								placeholder="" id="spinner7" name="spinner7" />
						</div>
						<div class="f-item spinner">
							<label for="spinner8">Infants</label> <input type="text"
								placeholder="" id="spinner8" name="spinner8" />
						</div>
					</div>
					<!--//column-->
				</div>
				<!--//form flight-->

			</div>
			<input type="submit" value="Proceed to results" class="search-submit"
				id="search-submit" />

		</form>
	</div>
	<!--//search-->

	<!-- Load jQuery and bootstrap datepicker scripts -->
	<script src="js/jquery-min.js"></script>
	<script src="js/bootstrap-datepicker.js"></script>
	<script type="text/javascript">
		// When the document is ready
		$(document).ready(function() {

			$('#example1').datepicker({
				format : "dd/mm/yyyy"
			});

		});
		$(document).ready(function() {

			$('#example2').datepicker({
				format : "dd/mm/yyyy"
			});

		});
	</script>

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

	<script src="js/jquery-1.11.0.min.js"></script>
	<script src="js/jquery-migrate-1.2.1.min.js"></script>
	<script src="js/jquery-ui.min.js"></script>
	<script type="text/javascript" src="js/sequence.jquery-min.js"></script>
	<script type="text/javascript" src="js/sequence.js"></script>
	<script type="text/javascript" src="js/selectnav.js"></script>
	<script type="text/javascript" src="js/jquery.uniform.min.js"></script>
	<script type="text/javascript" src="js/jquery.prettyPhoto.js"></script>
	<script type="text/javascript" src="js/modernizr.js"></script>
	<script type="text/javascript" src="js/scripts.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$(".form").hide();
			$(".form:first").show();
			$(".f-item:first").addClass("active");
			$(".f-item:first span").addClass("checked");
		});
	</script>
	<script>selectnav('nav'); </script>








	</script>

</body>
</html>