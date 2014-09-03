var metastore;

$(function(){

    metastore = (function () {
        console.log('searching for metadata');

        //Check if metadata is present in the page
        if (metadata) {
            console.log('metadata present in the page');
        }

        var getMetaData = function (key) {
            if(metadata.hasOwnProperty(key)){
                return metadata[key];
            }

            return {};
        };

        var putMetaData = function (key, data) {
            metadata[key] = data;
        }

        return{
            get: getMetaData,
            put: putMetaData
        };

    })();

})();
