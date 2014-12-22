if(! ("lazy_load" in document)) {
    document.lazy_load = {};
}
var rows_added = 0;
var last_to = 0;
var $toClone = $('#thumbnail_container_all .asset:first-child');

document.lazy_load.showAll = function(){
    $('#thumbnail_container_all').show();
    document.lazy_load.addItemsToPage();
    $(window).scroll(function(){
        document.lazy_load.addItemsToPage();
    });
    $(window).resize(function () {
        //recalculate "rows_added"
        rows_added = document.lazy_load.addItemsToPage.recalculateRowsAdded();
        document.lazy_load.addItemsToPage();
    });
};
document.lazy_load.addItemsToPage = function(){
    /*
     clean the counted rows from the session
     fist time load the viewable number of rows to the screen rows_per_page
     - Data for this are viewable width and height in the screen ( screen_width, screen_height)
     keep the rows counted in the session   (rows_added)

     calculate the row suppose to be displayed (row_current)
     -  rows_per_page and scroll position (scroll_pos)

     if(row_current > rows_added ) then
     - do a call to get the remaining rows and append them

     */
    var screen_width = $(window).width();
    var screen_height = $(window).height();

    var thumb_width = 200;
    var thumb_height = 250;

    var menu_width = 190;
    var header_height = 220;

    screen_height = screen_height - header_height;

    var items_per_row = 4;//(screen_width-screen_width%thumb_width)/thumb_width;
    var rows_per_page = (screen_height-screen_height%thumb_height)/thumb_height;
    var scroll_pos = $(document).scrollTop();
    var row_current =  (screen_height+scroll_pos-(screen_height+scroll_pos)%thumb_height)/thumb_height;
    row_current++; // We increase the row current by 1 since we need to provide one additional row to scroll down without loading it from backend
    console.info(row_current);
    document.lazy_load.addItemsToPage.recalculateRowsAdded = function(){
        return (last_to - last_to%items_per_row)/items_per_row;
    };

    var from = 0;
    var to = 0;
    if(row_current > rows_added){
        from = rows_added * items_per_row;
        to = row_current*items_per_row;
        last_to = to; //We store this os we can recalculate rows_added when resolution change
        rows_added = row_current;

        document.lazy_load.getItems(from,to).done(function(data) {
            for(var i=0;i<data.length;i++){
                var $newElem = $toClone.clone().show();
                $newElem.attr("data-id" , data[i].id);
                $(".assetsLink" , $newElem).attr("href", "assets/"+data[i].type+"/"+data[i].id);
                $("img" , $newElem).attr("src", data[i].attributes.images_thumbnail);
                $("h4" , $newElem).html(data[i].attributes.overview_name);
                $(".asset-rating div" , $newElem).attr("class", "asset-rating-"+data[i].rating.average+"star");


                $('#thumbnail_container_all').append($newElem);
            }

        });
    }

};

document.lazy_load.getItems = function(from,to){
    var dynamicData = {};
    dynamicData["count"] = to-from;
    dynamicData["from"] = from;
    return $.ajax({
        url: "/store/apis/assets/webapp/lazy",
        type: "get", //Set the async false since it wouldn't do the validation otherwise.
        dataType:"json",
        data: dynamicData
    });
};
$(document).ready(function(){
    document.lazy_load.showAll();
});
