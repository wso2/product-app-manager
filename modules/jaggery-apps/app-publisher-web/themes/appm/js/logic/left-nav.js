
var id = $('#page').val();

$( ".menu-item" ).each(function( index ) {

    if(("menu" + id) == $(this).attr('id')){

        $(this).addClass( "highlighted" );
    }

});