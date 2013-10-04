function onMessage(messageEvent) {
    console.log(messageEvent);
}

var windowProxy;
var adjustHeight = function () {
    windowProxy.post({'expanded': $(document).height()});
};

window.onload = function () {
    windowProxy = new Porthole.WindowProxy();
    windowProxy.addEventListener(onMessage);
//    setTimeout(adjustHeight,1000);
    adjustHeight();
};



