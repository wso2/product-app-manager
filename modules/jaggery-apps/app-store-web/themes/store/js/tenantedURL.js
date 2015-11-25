var getTenantedLoginURL = function () {
    var context,
        urlDomain = 'carbon.super',
        currentUrl = location.pathname;

    //regex to match super tenant urls '/{context}/{+any}
    var tenantedURLRegex = '([0-9A-Za-z-\\.@:%_\+~#=]+)/t/{1}([0-9A-Za-z-\\.@:%_\+~#=]+)';
    if (currentUrl.match(tenantedURLRegex)) { //if matches to tenanted url pattern
        context = currentUrl.match(tenantedURLRegex)[1];
        urlDomain = currentUrl.match(tenantedURLRegex)[2];
        return '/store' + tenantedPrefix + urlDomain + '/login';
    }
    return '/store/login';
}