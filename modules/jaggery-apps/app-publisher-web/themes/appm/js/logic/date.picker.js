var to = new Date();
var from = new Date(to.getTime() - 1000 * 60 * 60 * 24 * 104);
var currentDay = new Date(to.getFullYear(), to.getMonth(), to.getDate(),to.getHours(),to.getMinutes());


//date picker
$('#today-btn').on('click',function(){
    var from = convertTimeString(currentDay);
    var to = convertTimeString(currentDay-86400000);
    var str= to+" to "+from;
    $("#date-range").val(str);
    drawGraphs();

});

$('#hour-btn').on('click',function(){
    var from = convertTimeString(currentDay);
    var to = convertTimeString(currentDay-3600000);
    var str= to+" to "+from;
    $("#date-range").val(str);
    drawGraphs();
})

$('#week-btn').on('click',function(){
console.log(currentDay)
    var from = convertTimeString(currentDay);
    var to = convertTimeString(currentDay-604800000);
    var str= to+" to "+from;
    $("#date-range").val(str);
    //alert("dateRange Value"+$('#date-range').val())
    drawGraphs();
})

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
        startDate:from,
        endDate:to
    })
    .bind('datepicker-change',function(event,obj)
    {

    })
    .bind('datepicker-apply',function(event,obj)
    {
         var from = convertDate(obj.date1);
         var to = convertDate(obj.date2);
         $('#date-range').val(from + " to "+ to);
         //alert("dateRange Value"+$('#date-range').val())
         drawGraphs();
    })
    .bind('datepicker-close',function()
    {
    });

    $('#date-range').data('dateRangePicker').setDateRange(from,to);




//$('#datepicker-calendar').DatePicker({
//    inline: true,
//    date: [from, to],
//    calendars: 2,
//    mode: 'range',
//    current: new Date(to.getFullYear(), to.getMonth() - 1, 1),
//    onChange: function (dates, el) {
//        // update the range display
//        $('#date-range-field span').text(
//                convertDate(dates[0]) + ' to ' + convertDate(dates[1])
//        );
//        onDateSelected();
//    }
//});
//
//$('#date-range-field span').text(
//    convertDate(from) +' to ' + convertDate(to));
//
//$('#date-range-field').bind('click', function () {
//    $('#datepicker-calendar').toggle();
//    if ($('#date-range-field a').text().charCodeAt(0) == 9660) {
//        // switch to up-arrow
//        $('#date-range-field a').html('&#9650;');
//        $('#date-range-field').css({borderBottomLeftRadius: 0, borderBottomRightRadius: 0});
//        $('#date-range-field a').css({borderBottomRightRadius: 0});
//    } else {
//        // switch to down-arrow
//        $('#date-range-field a').html('&#9660;');
//        $('#date-range-field').css({borderBottomLeftRadius: 5, borderBottomRightRadius: 5});
//        $('#date-range-field a').css({borderBottomRightRadius: 5});
//    }
//    return false;
//});
//
//$('html').click(function () {
//    if ($('#datepicker-calendar').is(":visible")) {
//        $('#datepicker-calendar').hide();
//        $('#date-range-field a').html('&#9660;');
//        $('#date-range-field').css({borderBottomLeftRadius: 5, borderBottomRightRadius: 5});
//        $('#date-range-field a').css({borderBottomRightRadius: 5});
//    }
//});
//
$('#date-range').click(function (event) {
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
