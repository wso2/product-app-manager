/*
 Description: The script is used to delete an asset
 Filename: delete.asset.js
 Created Date: 27/5/2014
 */
$(function() {

	//The container used to display messages to the user
	var MSG_CONTAINER = '#msg-container-recent-activity';
	var ERROR_CSS = 'alert alert-error';
	var SUCCESS_CSS = 'alert alert-info';
	var CHARS_REM = 'chars-rem';
	var DESC_MAX_CHARS = 995;

	$('#overview_description').after('<span class="span8 ' + CHARS_REM + '"></span>');
		
	$(document).ready(function() {		
		
		createMessage(MSG_CONTAINER, ERROR_CSS , "The Application is already subcribed. Cannot be removed.");				

	});

	/*
	 The function creates a message and displays it in the provided container element.
	 @containerElement: The html element within which the message will be displayed
	 @cssClass: The type of message to be displayed
	 @msg: The message to be displayed
	 */
	function createMessage(containerElement, cssClass, msg) {
		var date = new Date();
		var time = date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
		var infoMessage = '<div class="' + cssClass + '">' + '<a data-dismiss="alert" ></a>' + time + ' ' + msg + '</div';

		//Place the message
		$(containerElement).html(infoMessage);
	}


	//$('.selectpicker').selectpicker();

});




