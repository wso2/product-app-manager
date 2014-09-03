/*
 Description: The script is responsible for initializing the Swagger library after obtaining
 the discoveryURL from container data element.
 */
$(function () {

    var CONTAINER = 'swagger-ui-container';
    var CONTAINER_ID = '#' + CONTAINER;
    var DATA_DISCOVERYURL_KEY = 'apimStoreApiDiscoveryurl';
    var SWAGGER_SUPPORTED_SUBMIT_METHODS = ['get', 'post', 'put', 'delete', 'options'];
    var SWAGGER_API_KEY_NAME = 'authorization';
    var SWAGGER_SUPPORT_HEADER_PARAMS = true;
    var SWAGGER_DOC_EXPANSION = 'none';

    //Obtain the discoveryURL from the container data- element (stored as data-apim-store-api-discoveryURL)
    var discoveryUrl = $(CONTAINER_ID).data(DATA_DISCOVERYURL_KEY) || null;

    //Support for IE
    console = (typeof console == undefined) ? {log: function (msg) {
    }} : console;

    //Do not load swagger if there is no discovery URL
    if (!discoveryUrl) {
        console.log('Unable to locate discoveryUrl');
        return;
    }

    //Initialize the Swagger UI library
    window.swaggerUi = new SwaggerUi({
        discoveryUrl: discoveryUrl,
        dom_id: CONTAINER,
        apiKeyName: SWAGGER_API_KEY_NAME,
        supportHeaderParams: SWAGGER_SUPPORT_HEADER_PARAMS,
        supportedSubmitMethods: SWAGGER_SUPPORTED_SUBMIT_METHODS,
        onComplete: function (swaggerApi, swaggerUi) {
            console.log("Loaded SwaggerUI");
            console.log(swaggerApi);
            console.log(swaggerUi);
        },
        onFailure: function (data) {
            console.log("Unable to Load SwaggerUI");
            console.log(data);
        },
        docExpansion: SWAGGER_DOC_EXPANSION
    });

    //Load the Swagger UI
    window.swaggerUi.load();

});
