$(function () {
    windowProxy = new Porthole.WindowProxy();
    windowProxy.addEventListener(onMessage);
    setTimeout(adjustHeight,1000);
});

var onMessage = function (messageEvent) {
    console.log(messageEvent);
};

var adjustHeight = function () {
	var docHeight = $(document).height();
    windowProxy.post({'expanded':docHeight});
};