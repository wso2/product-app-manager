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
            + '/getAPIResponseTime/',
        type : 'POST',
        data : {
            'startDate' : from,
            'endDate' : to
        },
        success : function(response) {
            drawAPIResponseTime(response);
        },
        error : function(response) {
            alert('Error occured at statistics graph rendering');
        }
    });

}

var drawAPIResponseTime = function(response) {

    var parsedResponse = JSON.parse(response);
    var length=parsedResponse.webapps.length;

    $('#placeholder41').empty();
    if(length==0){
            $('#placeholder41').html($('<h1 class="no-data-heading">No data available</h1>'));
    }else{

        var $dataTable =$('<table class="display" width="100%" cellspacing="0" id="apiSelectTable"></table>');
        	    $dataTable.append($('<thead class="tableHead"><tr>'+
        	                            '<th width="10%"></th>'+
        	                            '<th>API</th>'+
        	                            '<th>Response Time(ms)</th>'+

        	                        '</tr></thead>'));

    	var filterValues=[];
        var filterData=[];
        var defaultFilterValues=[];
        var defaultChartData=[];

        $('#checkboxContainer').append($dataTable);
        $('#checkboxContainer').show();

        var state_array =[];


    responsetimeCount =0;
    var timedatastructure=[];
    var webappdatasructure=[];

    for ( var i = 0; i < parsedResponse.webapps.length; i++) {
        var count = 0;
        for ( var j = 0; j < parsedResponse.webapps[i][1].length; j++) {
            responsetimeCount =Number(responsetimeCount)+ Number(parsedResponse.webapps[i][1][j][1]);
        }
        timedatastructure.push([responsetimeCount,i]);
        webappdatasructure.push([i,parsedResponse.webapps[i][0]]);
        responsetimeCount =0;
    }


    for(var n=0;n<webappdatasructure.length;n++){

            if(n<20){
                $dataTable.append($('<tr><td >'
                                            +'<input name="item_checkbox"  checked   id='+n+'  type="checkbox"  data-item='+webappdatasructure[n][1] +' class="inputCheckbox" />'
                                            +'</td>'
                                            +'<td style="text-align:left;"><label for='+n+'>'+webappdatasructure[n][1] +'</label></td>'
                                            +'<td style="text-align:left;"><label for='+n+'>'+timedatastructure[n][0] +'</label></td></tr>'));

                filterValues.push(webappdatasructure[n][1]);
                filterData.push(timedatastructure[n][0]);
                state_array.push(true);
                defaultFilterValues.push([n,webappdatasructure[n][1]]);
                defaultChartData.push([timedatastructure[n][0],n]);

            }else{

                $dataTable.append($('<tr><td >'
                                            +'<input name="item_checkbox" id='+n+'  type="checkbox"  data-item='+webappdatasructure[n][1] +' class="inputCheckbox" />'
                                            +'</td>'
                                            +'<td style="text-align:left;"><label for='+n+'>'+webappdatasructure[n][1] +'</label></td>'
                                            +'<td style="text-align:left;"><label for='+n+'>'+timedatastructure[n][0] +'</label></td></tr>'));

                filterValues.push(webappdatasructure[n][1]);
                filterData.push(timedatastructure[n][0]);
                state_array.push(false);
            }
        }
    $('#checkboxContainer').append($dataTable);
        $('#checkboxContainer').show();
        $('#apiSelectTable').DataTable({

            "order": [[ 2, "desc" ]],
            "aoColumns": [
            { "bSortable": false },
            null,
            null
            ],
        });

    // BAR CHART
    var dataset = [{
        data: defaultChartData,
        color: "#5482FF"
    }];

    $.plot($("#placeholder41"), dataset, {
        series: {
            bars: {
                show: true,
                clickable: true
            }
        },
        bars: {
            align: "center",
            barWidth : 0.5,
            horizontal : true,
        },
        xaxis: {
            axisLabel: "<b>Response Time (ms)</b>",
            axisLabelUseCanvas: false,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 20
        },
        yaxis: {
            axisLabel: "Web App",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelPadding: 3,
            ticks: defaultFilterValues
        },
        grid: {
            clickable: true,
            hoverable: true,
            borderWidth: 2,
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
        }
    });

   // tooltip
    var previousPoint = null,
    previousLabel = null;

    function showTooltip(x, y, contents) {
        $('<div id="tooltip">' + contents + '</div>').css({
            position: 'absolute',
            display: 'none',
            top: y - 40,
            left: x - 120,
            border: '2px solid ' ,
            color: 'black',
            font: '12px sans-serif',
            padding: 5,
            opacity: 0.9
        }).appendTo("body").fadeIn(200);
    }

    $("body #placeholder41").bind("plotclick", function (event, pos, item) {
        $("#tooltip").remove();
        if (item!=null){

            var tableStatement='';
            tableStatement ='<table class="table graphTable"><thead><tr><th>page</th><th>response time(ms)</th></tr></thead><tbody id="tbody"></tbody></table>';
            var x = item.datapoint[0];
            var label = item.series.yaxis.ticks[item.dataIndex].label;
            var webappPage = [];
            var webappPageCount = [];

            for ( var i = 0; i < parsedResponse.webapps.length; i++) {
                arr =[];
                if (label == parsedResponse.webapps[i][0]) {
                    for ( var j = 0; j < parsedResponse.webapps[i][1].length; j++) {
                        for ( var l = 0; l < parsedResponse.webapps[i][1][j].length; l++) {
                            webappPage=parsedResponse.webapps[i][1][j][0]
                            webappPageCount=parsedResponse.webapps[i][1][j][1]
                        }
                        arr.push({version:webappPage,count:webappPageCount});
                    }
                    showTooltip(item.pageX,
                    item.pageY,
                    tableStatement );

                    for (var l=0;l<arr.length;l++){
                    var arrStr=JSON.stringify(arr);
                        var versionName=arr[l].version;
                        var versionCount=arr[l].count;
                        $('#tbody').append('<tr><td>'+versionName+'</td><td style="text-align:right">'+versionCount+'</td></tr>');
                    }
                }
            }
        }
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
                    draw_y_axis.push([value,y_iter]);
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

            // BAR CHART
            onCheckDataset = [{ data: draw_y_axis, color: "#5482FF" }];
                $.plot($("#placeholder41"), onCheckDataset, {
                    series: {
                        bars: {
                            show: true,
                            clickable: true
                        }
                    },
                    bars: {
                        align: "center",
                        barWidth : 0.5,
                        horizontal : true,
                    },
                    xaxis: {
                        axisLabel: "<b>Response Time (ms)</b>",
                        axisLabelUseCanvas: false,
                        axisLabelFontSizePixels: 12,
                        axisLabelFontFamily: 'Verdana, Arial',
                        axisLabelPadding: 20
                    },
                    yaxis: {
                        axisLabel: "Web App",
                        axisLabelUseCanvas: true,
                        axisLabelFontSizePixels: 12,
                        axisLabelFontFamily: 'Verdana, Arial',
                        axisLabelPadding: 3,
                        ticks: draw_x_axis
                    },
                    grid: {
                        clickable: true,
                        hoverable: true,
                        borderWidth: 2,
                        backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
                    }
                });
            });
    }
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

