var log=new Log();

var resources= function (page,meta){

    return{
        js : ['asset-helpers.js', 'jquery.validate.js', 'search.js','logic/login/login.js','logic/assets/lazy-load.js'],
        css : ['navigation.css']
    } ;
};

var currentPage = function(navigation, type, search) {
    var asset;

    for (asset in navigation.assets) {
        if (asset == type) {
            navigation.assets[asset].selected = true;
            break;
        }
    }
    navigation.search = search;
    return navigation;
}