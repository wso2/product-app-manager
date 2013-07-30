var ssoEncodedRequest, ssoRelayState, ssoSessionId, ssoIdpURL;

(function () {
    var dataConfi = require('/store.js').config(),
        security = require("sso"),
        process = require("process"),
        ssoRelyingParty = new security.SSORelyingParty(dataConfi.ssoConfiguration.issuer),
        sessionId = session.getId(),
        isAuthenticated = ssoRelyingParty.isSessionAuthenticated(sessionId),
        requestURI,
        requestedPage = request.getParameter("requestedPage"),
        log = new Log();

    if (requestedPage != null) {
        requestURI = requestedPage;
    } else {
        requestURI = request.getRequestURI();
        if (request.getQueryString() != null) {
            requestURI = requestURI + '?' + request.getQueryString();
        }
    }

    ssoRelyingParty.setProperty("identityProviderURL", dataConfi.ssoConfiguration.identityProviderURL);
    ssoRelyingParty.setProperty("keyStorePassword", dataConfi.ssoConfiguration.keyStorePassword);
    ssoRelyingParty.setProperty("identityAlias", dataConfi.ssoConfiguration.identityAlias);
    ssoRelyingParty.setProperty("keyStoreName", process.getProperty('carbon.home') + dataConfi.ssoConfiguration.keyStoreName);


    var samlAuthRequest = ssoRelyingParty.getSAMLAuthRequest();

    ssoEncodedRequest = ssoRelyingParty.encode(samlAuthRequest);
    ssoRelayState = ssoRelyingParty.getUUID();
    ssoRelyingParty.setRelayStateProperty(ssoRelayState, requestURI);
    ssoSessionId = session.getId();
    ssoIdpURL = ssoRelyingParty.getProperty("identityProviderURL");

}());