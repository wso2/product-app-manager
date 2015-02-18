var logout = function () {
    jagg.post("/site/blocks/user/login/ajax/login.jag", {action:"logout"}, function (result) {
        if (!result.error) {
            var current = window.location.pathname;
            if (current.indexOf(".jag") >= 0) {
                location.href = "login.jag";
            } else {
                location.href = 'site/pages/login.jag';
            }
        } else {
            jagg.message({content:result.message,type:"error"});
        }
    }, "json");
};
$(document).ready(function(){
    $('#userInfoMenu').prev().click(function(){
        var $link = $(this);
        if($('#userInfoMenu').is(':visible')){
            $('#userInfoMenu').hide();
            $link.removeClass('open');
        }else{
            $('#userInfoMenu').show();
            $link.addClass('open');
        }
    });
    $('html').click(function() {
        if($('#userInfoMenu').is(':visible')){
            $('#userInfoMenu').hide();
            $('#userInfoMenu').prev().removeClass('open');
        }
    });

    $('#userMenu').click(function(event) {
        event.stopPropagation();
    });
});



