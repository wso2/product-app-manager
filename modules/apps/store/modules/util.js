var getRXTAttributes = function(type){
    var config = require('/config/publisher.json');
    var modelManager = application.get(config.app.MODEL_MANAGER);
    var model= modelManager.getModel(type);
    var output = model.export('formo');
    return output;
};