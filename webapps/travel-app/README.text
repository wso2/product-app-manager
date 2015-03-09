TravelBooking App 06/01/15

General usage notes
===================

-JAVA EE application run in tomcat  servlet container.
-when the server is started,index.jsp page load and 'Proceed To Result' button 
rederct to loging.jsp page or viewFlights.jsp page 

Logging through wso2appmanager
------------------------------
-User logged in any app in wso2AppManager server doesn't requre to loging in the TravelBooking app
-Will be redrect to the viewFlight.jsp after 'Proceed To Result' button is click
-Users should add below claims in their entities to their User Profile.
-booking-step1.jsp required below claims.
	Description-First Name	Claim Uri-http://wso2.org/claims/firstname
	Description-lastname	Claim Uri-http://wso2.org/claims/lastname
	Description-email	Claim Uri-http://wso2.org/claims/email
	Description-streetaddress	Claim Uri-http://wso2.org/claims/streetaddress
	Description-zipcode	Claim Uri-http://wso2.org/claims/zipcode
	Description-country	Claim Uri-http://wso2.org/claims/country

-booking-step2.jsp required below claims.
	Description-Credit card number		Claim Uri-http://wso2.org/claims/card_number
	Description-Credit cArd Holder Name	Claim Uri-http://wso2.org/claims/card_holder
	Description-Credit card expiration date	Claim Uri-http://wso2.org/claims/expiration_date

-User loging to the app through wso2appmaneger doesn't need to fill above fileds in booking steps.

Direct Logging 
--------------
-user must have account to proceed in search results
-when 'Proceed To Result' button click its rederect to the loging.jsp page
-User must fill all required details in booking-step1.jsp and booking-step2.jsp pages in order to book a flight.





