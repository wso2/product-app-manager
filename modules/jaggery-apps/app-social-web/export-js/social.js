var guestDomain = 'localhost:9443';

function onMessage1(messageEvent) {
    console.log(messageEvent);
    if (messageEvent.origin == "https://" + guestDomain) {
        var height = messageEvent.data.expanded;
        if (height) {
            $('#socialIfr').height(height);
        }
    }
}


var windowProxy1;
window.onload = function () {
    windowProxy1 = new Porthole.WindowProxy('http://localhost:9763/social/themes/default/js/lib/proxy.html', 'socialIfr');
    windowProxy1.addEventListener(onMessage1);
};