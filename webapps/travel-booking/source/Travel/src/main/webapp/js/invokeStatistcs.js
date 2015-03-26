function invokeStatistics(){
       var tracking_code = "AM_9110106947591784810108";
       var request = $.ajax({
        url: "http://localhost:8280/statistics/",
        type: "GET",
        headers: {
            "trackingCode":tracking_code,
        }
     
    });
}
