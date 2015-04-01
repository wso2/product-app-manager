var to = new Date();
//set from date in date picker month back from the currant date
var from = new Date(to.getTime() - (604800000*4));
var currentDay = new Date(to.getFullYear(), to.getMonth(), to.getDate(),to.getHours(),to.getMinutes());

var today=false;
var month=false;
var hour=false;
var dateRange=false;
var week=false;

//day picker
$('#today-btn').on('click',function(){
    today =true;
    hour,week,month,dateRange = false;
    var to = convertTimeString(currentDay);
    var from = convertTimeString(currentDay-86400000);
    var dateStr= from+" to "+to;
    $("#date-range").html(dateStr);
    $('#date-range').data('dateRangePicker').setDateRange(from,to);
    drawGraphs();

});

//hour picker
$('#hour-btn').on('click',function(){
    hour =true;
    today,week,month,dateRange = false;
    var to = convertTimeString(currentDay);
    var from = convertTimeString(currentDay-3600000);
    var dateStr= from+" to "+to;
    $("#date-range").html(dateStr);
    $('#date-range').data('dateRangePicker').setDateRange(from,to);
    drawGraphs();

})

//week picker
$('#week-btn').on('click',function(){
    week =true;
    today,hour,month,dateRange = false;
    var to = convertTimeString(currentDay);
    var from = convertTimeString(currentDay-604800000);
    var dateStr= from+" to "+to;
    $("#date-range").html(dateStr);
    $('#date-range').data('dateRangePicker').setDateRange(from,to);
    drawGraphs();

})

//month picker
$('#month-btn').on('click',function(){
    month =true;
    today,hour,week,dateRange = false;
    var to = convertTimeString(currentDay);
    var from = convertTimeString(currentDay-(604800000*4));
    var dateStr= from+" to "+to;
    $("#date-range").html(dateStr);
    $('#date-range').data('dateRangePicker').setDateRange(from,to);
    drawGraphs();

})


//date picker
$('#date-range').dateRangePicker(
    {
        startOfWeek: 'monday',
        separator : ' to ',
        format: 'YYYY-MM-DD HH:mm',
        autoClose: false,
        time: {
            enabled: true
        },
        shortcuts:'hide',
        endDate:to
    })
    .bind('datepicker-change',function(event,obj)
    {

    })
    .bind('datepicker-apply',function(event,obj)
    {
        if(obj.date2 == 'Invalid Date'){
            return false;
        }
         var from = convertDate(obj.date1);
         var to = convertDate(obj.date2);
         $('#date-range').html(from + " to "+ to);
         drawGraphs();
         $('.apply-btn').on('click',function(){

         });
    })
    .bind('datepicker-close',function()
    {
    });

    //setting default date
    $('#date-range').data('dateRangePicker').setDateRange(from,to);
    $('#date-range').html($('#date-range').val());



$('#date-range').click(function (event) {
    dateRange =true;
    today,hour,week,month = false;
    event.stopPropagation();
});

function convertDate(date) {
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour=date.getHours();
    var minute=date.getMinutes();
    return date.getFullYear() + '-' + (('' + month).length < 2 ? '0' : '')
        + month + '-' + (('' + day).length < 2 ? '0' : '') + day +" "+ (('' + hour).length < 2 ? '0' : '')
        + hour +":"+(('' + minute).length < 2 ? '0' : '')+ minute;
}

var convertTimeString = function(date){
    var d = new Date(date);
    var formattedDate = d.getFullYear() + "-" + formatTimeChunk((d.getMonth()+1)) + "-" + formatTimeChunk(d.getDate())+" "+formatTimeChunk(d.getHours())+":"+formatTimeChunk(d.getMinutes());
    return formattedDate;
};

var formatTimeChunk = function (t) {
    if (t < 10) {
        t = "0" + t;
    }
    return t;
};

$('body').on('click', '.btn-group button', function (e) {
    $(this).addClass('active');
    $(this).siblings().removeClass('active');
});