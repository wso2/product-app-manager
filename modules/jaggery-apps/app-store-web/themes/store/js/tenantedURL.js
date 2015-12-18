//regex to match super tenant urls '/{context}/{+any}
var tenantedURLRegex = '([0-9A-Za-z-\\.@:%_\+~#=]+)/t/{1}([0-9A-Za-z-\\.@:%_\+~#=]+)';

/**
 * Return tenanted url
 * @param requestedURL
 * @returns {string}
 */
var getTenantedURL = function (requestedURL){
    var context = caramel.context,
        urlDomain = 'carbon.super',
        tenantedPrefix = '/t/',
        currentUrl = location.pathname;

    if (currentUrl.match(tenantedURLRegex)) { //if matches to tenanted url pattern
        urlDomain = currentUrl.match(tenantedURLRegex)[2];
        return context+ tenantedPrefix + urlDomain + requestedURL;
    }
    return context + requestedURL;
}

var getURLTenantDomain = function(){
    var urlDomain = 'carbon.super',
        currentUrl = location.pathname;

    if (currentUrl.match(tenantedURLRegex)) { //if matches to tenanted url pattern
        urlDomain = currentUrl.match(tenantedURLRegex)[2];
    }
    return urlDomain;
}