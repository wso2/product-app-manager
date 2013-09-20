var caramel = require('caramel');

caramel.configs({
    context: '/social',
    cache: true,
    negotiation: true,
    themer: function () {
        return 'default';
    }
});

var carbon = require('carbon');
var social = carbon.server.osgiService('org.wso2.carbon.social.service.SocialActivityService');
setTimeout(function () {
    social.publish({"verb": "post",
        "object": {"type": "comment", "content": "test"},
        "target": {"id": "test_"}
    });
}, 10000)
