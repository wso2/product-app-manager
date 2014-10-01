/*
 Description: The following script is used to provide a generic API through which messages are rendered
 in the store and publisher.
 It uses the Noty messaging framework to render the messages.
 If a message notification framework is not available, it will default to showing messages
 in an alert window.
 Filename: notifications.js
 Created Date: 30/1/2013
 */
var notify;

/*
 License details for Noty v2.2.2

 Copyright (c) 2012 Nedim Arabacı

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

$(function () {

    /*
     * The function checks and initializes available renderers.It first checks whether
     * Noty is present, if not it uses an Alert renderer.
     */
    var resolveAvailableRenderer = function () {
        var renderer = new AlertWrapper();
        if (typeof noty == 'undefined') {
            renderer = new AlertWrapper();
        } else {
            renderer = new NotyWrapper(noty);
        }
        return renderer;
    };

    /*
     * The function intializes the notification rendering
     */
    var init = function () {
        var renderer = resolveAvailableRenderer();
        var notify = new Notify();
        notify.setRenderer(renderer);
        return notify;
    };

    /*
     * The function is used to perform any extra processing on the message object before
     * it is passed to the renderer
     */
    var resolveMessageType = function (obj) {
        return obj;
    };


    function Notify() {
        this.renderer = null;
        this.msgResolver=resolveMessageType;
    }

    /*
     * The renderer used to render the messages on the page
     */
    Notify.prototype.setRenderer = function (renderer) {
        this.renderer = renderer;
    };

    /*
     * The way messages are rendered on a per page basic can be changed
     * by calling setting a callback which will modify the structure of the message
     * passed to the renderer.
     * The provided callback will be invoked before the message is passed to the
     * renderer
     */
    Notify.prototype.setMsgResolver=function(msgResolveHandler){
        this.msgResolver=msgResolveHandler;
    };

    /*
     * The function displays a message
     * @obj.text: The message to be displayed
     * @obj.type: The type of message to be rendered (info and error)
     * @obj.buttons: A button array with associated callbacks
     */
    Notify.prototype.msg = function (obj) {
        this.renderer.render(obj);
    };

    /*
     * Default renderers
     */
    var DEFAULT_MESSAGE_TYPE = 'info';
    var DEFAULT_MESSAGE = 'No message specified';

    /*
     * The following class wraps the functionality of the Noty framework
     */
    function NotyWrapper(noty) {
        this.noty = noty;
    }

    /*
     * The function is used to render a message in the Noty framework
     */
    NotyWrapper.prototype.render = function (obj) {
        var msgType = obj.hasOwnProperty('type') ? obj.type : DEFAULT_MESSAGE_TYPE;
        this.noty(obj);
    };


    /*
     * The following class is used to render messages in an alert box if no notification
     * framework is available.It will only display messages and will not render any
     * buttons
     */
    function AlertWrapper() {

    }

    /*
     * The function displays the provided message text in an alert box
     */
    AlertWrapper.prototype.render = function (obj) {
        var msg = obj.hasOwnProperty('text') ? obj.text : DEFAULT_MESSAGE;
        alert(msg);
    };

    notify = init();
}());