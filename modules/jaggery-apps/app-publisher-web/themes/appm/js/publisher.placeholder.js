var placeHolders= {
    "overview_name": "HelloWorld",
    "overview_displayName": "Hello World Web Application",
    "overview_context": "Web Application Context. E.g: /hello",
    "overview_version": "E.g: 1.0.0, v1, or v1.0.0",
    "overview_webAppUrl": "Enter the URL of existing web application  E.g: http://mywebapp.com/appurl",
    "overview_description": "Enter description for your webapp",
    "overview_logoutUrl": "Enter Logout URL for your webapp"
}

$(document).ready(function(){
    $.each(placeHolders, function(key,val) {
       $('#'+key).attr("placeholder", val);
    });
});