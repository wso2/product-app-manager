function invokeStatistics(){
        var tracking_code = "AM_47799122892320156";
        var request = $.ajax({
        url: "http://localhost:8280/statistics/",
        type: "GET",
        headers: {
            "trackingCode":tracking_code,
        }
     
    });
}
