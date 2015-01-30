$(document).ready(function() {

    $("#message-panel").hide();

    function loadMessages(){

        $.ajax({
            url : "api/getMessages.php",
            type: "GET",
            success: function(data, textStatus, jqXHR) {
                data = JSON.parse(data);
                $("#message-table > tbody").html("");

                for( i = 0; i < data.length; i++){
                    if(i === 0){
                        $("#message-txt").html(data[i][0]);
                        $("#message-date").html("- " + data[i][1]);
                        $("#message-sender").html(data[i][2]);
                        $("#message-panel").show();
                    }else{
                        $("#message-table > tbody:last").append("<tr><td>" + data[i][0] +"</td><td>" + data[i][2] +"</td>" +"</td><td>" + data[i][1] +"</td></tr>");
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown){

            }
        });
    }

    loadMessages();
    setInterval(loadMessages, 10000);
});