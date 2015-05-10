$(document).ready(function() {
    tinyMCE.init({
                     mode : "textareas",
                     theme : "advanced",
                     plugins : "inlinepopups",
                     theme_advanced_buttons1 : "newdocument,|,bold,italic,underline,link,unlink,|,justifyleft,justifycenter,justifyright,fontselect,fontsizeselect,formatselect",
                     theme_advanced_buttons2 : "cut,copy,paste,|,bullist,numlist,|,outdent,indent,|,undo,redo,|,forecolor,backcolor",
                     theme_advanced_buttons3 : "insertdate,inserttime,|,spellchecker,advhr,,removeformat,|,sub,sup,|,charmap,emotions",
                     theme_advanced_toolbar_location : "top",
                     theme_advanced_toolbar_align : "left",
                     theme_advanced_resizing : true,
                     init_instance_callback: loadDefaultTinyMCEContent()

                 });
    
   

});


function loadDefaultTinyMCEContent() {
	
    var provider = $("#provider").val();
    var apiName  = $("#webapp").val();
    var version = $("#version").val();
    var docName = $("#doc").val();

	 $.ajax({
			url : '/publisher/api/doc?action=getInlineContent',
			type:'POST',
			data :{'provider':provider,'apiName':apiName,'version':version,'docName':docName},
			success : function(response) {
				 if(JSON.parse(response).error == false){
					 var doc = JSON.parse(response).doc;
					 var provider = doc.provider;
					 var docName = provider.docName;
	                var apiName = provider.apiName;
	                var docContent = provider.content;

	               $('#apiDeatils').empty().html('<p><h1> ' + docName + '</h1></p>');
	               $('#inlineEditor').val(docContent);
	               //tinyMCE.activeEditor.setContent(docContent);
				 }
				
			},
			error : function(response) {
	          $('#inlineError').show('fast');
	          $('#inlineSpan').html('<strong>'+ i18n.t('errorMsgs.inlineContent')+'</strong><br />'+result.message);
			}
			
		});


}

function saveContent(provider, apiName, apiVersion, docName, mode) {
	var contentDoc = tinyMCE.get('inlineEditor').getContent();

	$.ajax({
		url : '/publisher/api/doc?action=addInlineContent',
		type:'POST',
		//dataType:json,
		data :{'provider':provider,'apiName':apiName,'version':apiVersion,'docName':docName,'content':contentDoc},
		success : function(response) {
			 if(JSON.parse(response).error == false){

				 if (mode == "save"){
                     window.close();
				 }else {
                     $('#docAddMessage').show();
                     setTimeout("hideMsg()", 6000);
                }
			 }
			 else{
	           		console.info('Error occured in adddddinng');

			 }
		},
		error : function(response) {
       		console.info('Error occured in adinggngng');

		}
		
	});
	
	
	
	
	
	
	
	
	
	
	
	
}

var hideMsg=function () {
    $('#docAddMessage').hide();
}
