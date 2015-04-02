
    $(".btn-deploySample").click(function() {
        alert("button clicked");    
        jQuery.ajax({
             url: '/publisher/api/asset/mobileapp/deploySample',
             type: "GET",
        });
        (document).ajaxComplete(function () {
             location.reload();
        });    
    });