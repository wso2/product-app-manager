$(document).ready(function() {


    $( "#btn-send" ).click(function() {

        var message = $("#txt-message").val();


        if(message === ""){
            alert("Please enter a valid message");
            return;
        }

        $.ajax({
            url : "api/sendMessage.php",
            type: "POST",
            data : {message: message},
            success: function(data, textStatus, jqXHR) {
                loadMessages();
            },
            error: function (jqXHR, textStatus, errorThrown){

            }
        });
    });



    function loadMessages(){

        $.ajax({
            url : "api/getMessages.php",
            type: "GET",
            success: function(data, textStatus, jqXHR) {
                data = JSON.parse(data);
                $("#message-table > tbody").html("");
                for( i = 0; i < data.length; i++){
                    $("#message-table > tbody:last").append("<tr><td>" + data[i][0] +"</td><td>" + data[i][1] +"</td></tr>");
                }
            },
            error: function (jqXHR, textStatus, errorThrown){

            }
        });

    }

    loadMessages();



});