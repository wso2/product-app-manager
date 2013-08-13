var caramel = require('caramel');

caramel.configs({
    context: '/social',
    cache: true,
    negotiation: true,
    themer: function () {
        return 'default';
    }
});
