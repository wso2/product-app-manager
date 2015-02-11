$(function () {
    drawGraphs();

});

function drawGraphs() {

    var url = window.location.pathname;
    var comps = url.split('/');
    var type = comps[comps.length - 2];
    var operation = comps[comps.length - 3];
    var action = 'getSubscriberCountByAPIs';
    var dateRange = $('#date-range-field span').text();
    var from = dateRange.split('to')[0];
    var to = dateRange.split('to')[1];
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

    $('').append(
    $('<table class="table graphTable" id="webAppTable4">' + '<tr>'
    + '<th>Web App</th>' + '<th>Page</th>'
    + '<th>Response Time(ms)</th>' + '</tr>' + '</table>'));

    var tableStatement = '';

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
    // BAR CHART
    var dataset = [{
        data: timedatastructure,
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
            axisLabel: "Response Time (ms)",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10
        },
        yaxis: {
            axisLabel: "Web App",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,
            ticks: webappdatasructure
        },
        grid: {
            clickable: true,
            hoverable: true,
            borderWidth: 2,
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
        }
    });

    //tooltip
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

