(function (caramel) {

    /**
     * Resolves absolute paths by adding app context prefix.
     * @param path
     * @return {*}
     */
    caramel.url = function (path) {
        return this.context + (path.charAt(0) !== '/' ? '/' : '') + path;
    };

    caramel.tenantedUrl = function (path) {
        var uri = window.location.href;//current page path
        var tenantedRegex = '([0-9A-Za-z-\\.@:%_\+~#=]+)/t/{1}([0-9A-Za-z-\\.@:%_\+~#=]+)';

        if (uri.match(tenantedRegex)) {
            var domain = uri.match(tenantedRegex)[2];
            return this.context + '/t/' + domain + path;//this.context;;
        } else if (path.length > 0) {
            return this.context + (path.charAt(0) !== '/' ? '/' : '') + path;
        }
        return this.context;
    };

    caramel.get = function(path) {
        var args = Array.prototype.slice.call(arguments);
        args[0] = caramel.url(args[0]);
        return $.get.apply(this, args)
    };

    caramel.post = function(path) {
        var args = Array.prototype.slice.call(arguments);
        args[0] = caramel.url(args[0]);
        return $.post.apply(this, args)
    };

    caramel.ajax = function(options) {
        options.url = caramel.url(options.url);
        return $.ajax.call(this, options);
    };

})(caramel);