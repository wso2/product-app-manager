$(function () {
    drawGraphs();
});

function drawGraphs() {
    var url = window.location.pathname;

    var comps = url.split('/');
    var type = comps[comps.length - 2];
    var operation = comps[comps.length - 3];
    var action = 'getSubscriberCountByAPIs';

    var dateRange = $('#date-range').val();
    var from = dateRange.split('to')[0].trim()+":00";
    var to = dateRange.split('to')[1].trim()+":00";;

    var userParsedResponse;

    $.ajax({
        async : false,
        url : '/publisher/api/assets/' + operation + '/' + type
            + '/getAPIUsageByPage/',
        type : 'POST',
        data : {
            'startDate' : from,
            'endDate' : to
        },
        success : function(response) {
            drawAPIUsageByPage(response);
        },
        error : function(response) {
            alert('Error occured at statistics graph rendering');
        }
    });

    $('.btn-maximize').on('click', function() {
        $(this).parents('.widget').addClass('widget-maximized');
        $('.backdrop').show();
    });

    $('.btn-minimize').on('click', function() {
    	$(this).parents('.widget').removeClass('widget-maximized');
      //  $(this).parents('.widget').toggleClass('widget-maximized');
        $('.backdrop').hide();
    });

    $('.btn-remove').on('click', function() {
        $(this).parents('.graph-maximized').removeClass('graph-maximized');
        $('.backdrop').hide();
    });

}

var drawAPIUsageByPage = function(response) {

    $('#checkboxContainer').empty();
	var parsedResponse = JSON.parse(response);
	var data = parsedResponse.totalPageCount;
	var ticks = parsedResponse.webapp;

	var $dataTable =$('<table class="display" width="100%" cellspacing="0" id="apiSelectTable"></table>');
    	    $dataTable.append($('<thead class="tableHead"><tr>'+
    	                            '<th width="10%"></th>'+
    	                            '<th>API</th>'+
    	                        '</tr></thead>'));

	var filterValues=[];
    var filterData=[];
    var defaultFilterValues=[];
    var defaultChartData=[];

    $('#checkboxContainer').append($dataTable);
    $('#checkboxContainer').show();

    var state_array =[];

    for(var n=0;n<ticks.length;n++){

        if(n<20){
            $dataTable.append($('<tr><td >'
                                        +'<input name="item_checkbox"  checked   id='+n+'  type="checkbox"  data-item='+ticks[n][1] +' class="inputCheckbox" />'
                                        +'</td>'
                                        +'<td style="text-align:left;"><label for='+n+'>'+ticks[n][1] +'</label></td></tr>'));
            filterValues.push(ticks[n][1]);
            filterData.push(data[n][1]);
            state_array.push(true);
            defaultFilterValues.push([n,ticks[n][1]]);
            defaultChartData.push([n,data[n][1]]);
        }else{

            $dataTable.append($('<tr><td >'
                                        +'<input name="item_checkbox" id='+n+'  type="checkbox"  data-item='+ticks[n][1] +' class="inputCheckbox" />'
                                        +'</td>'
                                        +'<td style="text-align:left;"><label for='+n+'>'+ticks[n][1] +'</label></td></tr>'));
            filterValues.push(ticks[n][1]);
            filterData.push(data[n][1]);
            state_array.push(false);

        }
    }
    $('#checkboxContainer').append($dataTable);
                    $('#checkboxContainer').show();
                    $('#apiSelectTable').DataTable({
                        retrieve: true,
                        "order": [[ 1, "asc" ]],
                        "aoColumns": [
                        { "bSortable": false },
                        null
                        ],
                    });



    var defaultOptions = {
                series: {
                    bars: {
                        show: true
                    }
                },
                bars: {
                    align: "center",
                    barWidth: 0.3
                },
                xaxis: {

                    axisLabelUseCanvas :false,
                    axisLabel : "<b>Web Apps</b>",
                    tickLength : 0,
                    axisLabelPadding: 10,
                    ticks: defaultFilterValues
                },
                yaxis: {
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelPadding: 3,
                    axisLabel: "Total Request",

                },
                legend: {
                    noColumns: 0,
                    labelBoxBorderColor: "#000000",
                    position: "nw"
                },
                grid: {
                    hoverable: true,
                    borderWidth: 0.5,
                     borderColor: {left: "#bdbdbd", left: "#bdbdbd"},
                    clickable : true,
                    backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
                }
            };
            defaultDataset = [{ data: defaultChartData, color: "#5482FF" }];
           if(parsedResponse.usage.length == 0){
                   $("#placeholder51").html('<h1 class="no-data-heading">No data available</h1>')
               }else{
                   $("#placeholder51").html();
                   $.plot($("#placeholder51"), defaultDataset, defaultOptions);
               }
            $("#placeholder51").UseTooltip();


	$("#placeholder51").bind("plotclick", function(event, pos, item) {

		if (item!=null){
        		                var numbers =[]
                                var option
                                $("#placeholder51").parents('.widget').addClass('graph-maximized');
                                $('.backdrop').show();
                                $('#checkboxContainer').hide();

                                                $('.btn-remove').on('click',function(){
                                                 $('#checkboxContainer').show();
                                                });

                                var x = item.datapoint[0];

                                 label = item.series.xaxis.ticks[x].label;

                                for ( var i = 0; i < parsedResponse.webapp_.length; i++) {
                                    var count = 0;
                                    var app ='';

                                    if(label == (parsedResponse.webapp_[i][0]).replace(/\s+/g, '')){

                                        for ( var j = 0; j < parsedResponse.webapp_[i][1].length; j++) {

                                            numbers.push(parsedResponse.webapp_[i][1][j][0])

                                        }

                                    }

                                }
                                var option ;

                                for (i=0;i<numbers.length;i++){
                                option += '<option value="'+ numbers[i] + '">' + numbers[i] + '</option>';
                                }
                                $('#items').html(option);
                                 var e = document.getElementById("items");
                                var strUser = e.options[e.selectedIndex].value;
                    drawPopupChart(parsedResponse,label,strUser);

                }
	});

	    $('#items').change(function(){

                var e = document.getElementById("items");
            var strUser = e.options[e.selectedIndex].value;
            drawPopupChart(parsedResponse,label,strUser);

            });

	$('#apiSelectTable').on( 'change', 'input.inputCheckbox', function () {
          var id =  $(this).attr('id');
          var check=$(this).is(':checked');
          var tickValue= $(this).attr('data-item');
          var draw_y_axis = []
          var draw_x_axis = []

            if(check){
                state_array[id] =true;
            }else{
                state_array[id] =false;
            }

            var y_iter = 0
            $.each( filterData ,function(index, value){
                if(state_array[index]){
                    draw_y_axis.push([y_iter,value]);
                    y_iter++
                }
            });

            var x_iter = 0
            $.each( filterValues ,function(index, value){
                if(state_array[index]){
                    draw_x_axis.push([x_iter,value]);
                    x_iter++
                }
            });

            var options = {
                series: {

                    bars: {
                        show: true,
                        align: "center",
                        barWidth: 0.3
                    }
                },

                xaxis: {

                    axisLabelUseCanvas :false,
                    axisLabel : "<b>Web Apps</b>",
                    tickLength : 0,
                    axisLabelPadding: 10,

                    ticks: draw_x_axis
                },
                yaxis: {
                axisLabelUseCanvas :true,
                    axisLabel: "Total Request",

                },
                legend: {
                    noColumns: 0,
                    labelBoxBorderColor: "#000000",
                    position: "nw"
                },
                grid: {
                    hoverable: true,
                    borderWidth: 0.5,
                    borderColor: {left: "#bdbdbd", left: "#bdbdbd"},
                    clickable : true,

                    backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
                }
            };
                    dataset = [{ data: draw_y_axis, color: "#5482FF" }];

            $.plot($("#placeholder51"), dataset, options);
            $("#placeholder51").UseTooltip();
            });


}

var previousPoint = null, previousLabel = null;

$.fn.UseTooltip = function () {
    $(this).bind("plothover", function (event, pos, item) {
        if (item) {
            if ((previousLabel != item.series.label) || (previousPoint != item.dataIndex)) {
                previousPoint = item.dataIndex;
                previousLabel = item.series.label;
                $("#tooltip").remove();
                var x = item.datapoint[0];
                var y = item.datapoint[1];
                var color = item.series.color;
                showTooltip(item.pageX,
                item.pageY,
                color,
                "<strong>" + item.series.xaxis.ticks[x].label+ " : " + y + "</strong>");
            }
        } else {
            $("#tooltip").remove();
            previousPoint = null;
        }
    });
};

function showTooltip(x, y, color, contents) {
    $('<div id="tooltip">' + contents + '</div>').css({
        position: 'absolute',
        display: 'none',
        top: y - 20,
        left: x - 120,
        border: '2px solid ' + color,
        padding: '3px',
        'font-size': '9px',
        'border-radius': '5px',
        'background-color': '#fff',
        'font-family': 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
        opacity: 0.9
    }).appendTo("body").fadeIn(200);
}


var onDateSelected = function() {
    clearTables();
    drawGraphs();
}


function clearTables() {
    $('#webAppTable').remove();
    $('#webAppTable2').remove();
    $('#webAppTable3').remove();
    $('#webAppTable4').remove();
    $('#webAppTable5').remove();

}
function drawPopupChart(parsedResponse,label,strUser){

            var webappPage = [];
            var webappPageCount = [];
            for ( var i = 0; i < parsedResponse.webapp_.length; i++) {

                var app ='';

                if(label == (parsedResponse.webapp_[i][0]).replace(/\s+/g, '')){

                var arr=[];


                    for ( var j = 0; j < parsedResponse.webapp_[i][1].length; j++) {

                        if(strUser == (parsedResponse.webapp_[i][1][j][0]).replace(/\s+/g, '')){

                            var newArr = [],found, x, y;

                            var maximumUsers = parsedResponse.webapp_[i][1][j][1].length;

                            var origLen = parsedResponse.webapp_[i][1][j][1].length,

                            maxrowspan = parsedResponse.webapp_[i][1][j][1].length;


                            for ( var k = 0; k < maximumUsers; k++) {

                                found = undefined;

                                for (y = 0; y < newArr.length; y++) {

                                    if (parsedResponse.webapp_[i][1][j][1][k][0] === newArr[y]) {
                                        found = true;

                                        break;
                                    }
                                }
                                if (!found) {
                                    newArr.push(parsedResponse.webapp_[i][1][j][1][k][0]);
                                }

                            }
                            for(var l = 0; l < newArr.length; l++){
                            var allcount=0;
                                for ( var k = 0; k < maximumUsers; k++) {
                                    if(newArr[l] == parsedResponse.webapp_[i][1][j][1][k][0]){


                                            allcount = Number(allcount)+Number(parsedResponse.webapp_[i][1][j][1][k][1]);
                                        }

                                }
                                webappPageCount.push([allcount,l]);
                            }



                        }

                    }


                }

            }

            for(p = 0; p < newArr.length; p++){
                webappPage.push([p,newArr[p]])
            }



            var max = 0;
            for (t = 0; t < webappPageCount.length; t++) {
                if (max < webappPageCount[t][0]) {
                    max = webappPageCount[t][0];
                }
            }

            max = max + 10;


            var data = webappPageCount;

            var dataset = [ {
                data : data,
                color : "#5482FF"
            } ];

            var ticks = webappPage;

            var options = {
                series : {
                    bars : {
                    show : true,
                    horizontal : true,

                    }
                },
                bars : {
                    align : "center",
                    barWidth : 0.5
                },
                xaxis : {
                    axisLabelUseCanvas :false,
                    axisLabel : "<b>Total Request Count</b>",
                    reserveSpace : true,
                    labelWidth : 150,
                    min : 0,
                    max : max
                },
                yaxis : {
                    ticks : ticks,

                    tickLength : 0,
                    axisLabel : "Accessed Page",

                }
            };
            $.plot($("#placeholder52"), dataset, options);

}

