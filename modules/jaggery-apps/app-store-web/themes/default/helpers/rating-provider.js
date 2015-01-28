/*
Description: The helper module examines an asset array provided in the context and calculates the pixel
size of the displayed star ratings
Created Date: 17/12/2013
Filename;ratingHelper.js
 */
var ratingProvider;

(function(){

    var STAR_WIDTH = 84;
    var MAX_RATING = 5;

    function Helper(){

    }

    Helper.prototype.formatRating=function(assets){

        var assets=assets||[];

        this.assignRating(assets);

        return assets;
    };

    Helper.prototype.assignRating=function(assets){
        var averageRating;
        assets.forEach(function(element){
             averageRating=element.rating.average;
             element['ratingPx']=(averageRating/MAX_RATING)*STAR_WIDTH;
        });
    };

    ratingProvider=new Helper();

})();
