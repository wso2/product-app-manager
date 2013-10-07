var windowProxy;

var onMessage = function (messageEvent) {
    console.log(messageEvent);
};

var adjustHeight = function () {
    windowProxy.post({'expanded': $(document).height()});
};

window.onload = function () {
    windowProxy = new Porthole.WindowProxy();
    windowProxy.addEventListener(onMessage);
    adjustHeight();
};


