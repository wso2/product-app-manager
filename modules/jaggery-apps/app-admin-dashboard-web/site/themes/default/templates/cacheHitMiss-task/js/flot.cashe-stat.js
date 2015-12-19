var  btn = 'month-btn';
var context = this.jagg.site.context;
$(function () {

    drawGraphs();

});
$("button").click(function() {
      btn =this.id; // or alert($(this).attr('id'));

});
function drawGraphs() {

    var url = window.location.pathname;

    var comps = url.split('/');
    var type = comps[comps.length - 2];
    var operation = comps[comps.length - 3];


    var dateRange = $('#date-range').val();


    var from = dateRange.split('to')[0].trim() + ":00";
    var to = dateRange.split('to')[1].trim() + ":00";


    var userParsedResponse;

    $.ajax({

        async: false,
        url: context + '/apis/cache-hit-miss',
        type: 'POST',
        data: {
            'startDate': from,
            'endDate': to
        },
        success: function (response) {


            drawCacheHit(response);
            $('#spinner').hide();



        },
        error: function (response) {
            alert('Error occured at statistics graph rendering');
        }
    });

    $('.btn-maximize').on('click', function () {
        $(this).parents('.widget').addClass('widget-maximized');
        $('.backdrop').show();
    });

    $('.btn-minimize').on('click', function () {
        $(this).parents('.widget').removeClass('widget-maximized');
        //  $(this).parents('.widget').toggleClass('widget-maximized');
        $('.backdrop').hide();
    });

    $('.btn-remove').on('click', function () {
        $(this).parents('.graph-maximized').removeClass('graph-maximized');
        $('.backdrop').hide();
        $('.widget-head').hide();
    });

}

   var drawCacheHit = function (response) {

   var usageByContext = JSON.parse(response);

    if(usageByContext.length>0){
    $('#tableContainer').empty();
    volumeData =[];
    values =[];
    for (var i = 0; i < usageByContext.length; i++) {
        function dateToUnix(year, month, day, hour, minute, second) {
            return ((new Date(year, month - 1, day, hour, minute, second)).getTime());
        }
            var hits =0 ;
            for (var j = 0; j < 1; j++) {

                var time = usageByContext[i][0];
                var str = time;
                var d = new Date(str.split(' ')[0].split('-').join(',') + ',' +
                    str.split(' ')[1].split('-').join(','));
                var year = d.getFullYear();
                var month = d.getMonth();
                var date = d.getDate();
                var hour = d.getHours();
                var min = d.getMinutes();
                var second = d.getSeconds();

                var dateInSeconds = dateToUnix(year, (month + 1), date, hour, min, second);
                var seconds = dateInSeconds ;

                if (usageByContext[i][1][j][0]  == "1"  ) {
                    numOfVersion = usageByContext[i][1][j][1].length;
                    for (var t = 0; t < numOfVersion; t++) {

                            for (var k = 0; k < usageByContext[i][1][j][1][t][1].length; k++) {
                                dataTest = [];

                                for (var l = 0; l < usageByContext[i][1][j][1][t][1][k][1].length; l++) {

                                    hits = hits+usageByContext[i][1][j][1][t][1][k][1][l][1];

                                }

                            }
                    }

                }else{

                   hits = 0;
                }

            }
             values.push([seconds,hits]);

    }
    volumeData.push({"key":"Hits","values":values});


    var values =[];

        for (var i = 0; i < usageByContext.length; i++) {
            function dateToUnix(year, month, day, hour, minute, second) {
                return ((new Date(year, month - 1, day, hour, minute, second)).getTime());
            }

    var hits =0 ;
                for (var j = 1; j < 2; j++) {

                    var time = usageByContext[i][0];
                    var str = time;
                    var d = new Date(str.split(' ')[0].split('-').join(',') + ',' +
                        str.split(' ')[1].split('-').join(','));
                    var year = d.getFullYear();
                    var month = d.getMonth();
                    var date = d.getDate();
                    var hour = d.getHours();
                    var min = d.getMinutes();
                    var second = d.getSeconds();

                    var dateInSeconds = dateToUnix(year, (month + 1), date, hour, min, second);
                    var seconds = dateInSeconds ;

                    if (usageByContext[i][1].length > 1 ) {

                        numOfVersion = usageByContext[i][1][j][1].length;
                        for (var t = 0; t < numOfVersion; t++) {
                                 for (var k = 0; k < usageByContext[i][1][j][1][t][1].length; k++) {
                                    dataTest = [];
                                    for (var l = 0; l < usageByContext[i][1][j][1][t][1][k][1].length; l++) {

                                        hits = hits+usageByContext[i][1][j][1][t][1][k][1][l][1];

                                    }

                                }
                        }

                    }else if (usageByContext[i][1][0][0]  == "0"  ) {

                                         numOfVersion = usageByContext[i][1][0][1].length;
                                         for (var t = 0; t < numOfVersion; t++) {

                                                 for (var k = 0; k < usageByContext[i][1][0][1][t][1].length; k++) {
                                                     dataTest = [];

                                                     for (var l = 0; l < usageByContext[i][1][0][1][t][1][k][1].length; l++) {

                                                         hits = hits+usageByContext[i][1][0][1][t][1][k][1][l][1];

                                                     }

                                                 }
                                         }

                                     }else{

                                        hits = 0;
                                     }

                }
                 values.push([seconds,hits]);

            //}

        }
        $(".graph-container").html($('<div id="casheHitCountChart" class="with-3d-shadow with-transitions"><svg style="height:400px;"></svg></div>'));
        $(".graph-container").show();


volumeData.push({"key":"Miss","values":values});

(function(data){
    var colors = d3.scale.category20();
    keyColor = function(d, i) {return colors(d.key)};
    var div = d3.select("body").append("div").attr("class", "toolTip");

    var chart;
    nv.addGraph(function() {
        chart = nv.models.stackedAreaChart()


        .x(function(d) { return d[0] })
        .y(function(d) { return d[1] })
         .useInteractiveGuideline(true)
        .color(keyColor)
        .margin({left:100,bottom:160});

        chart.xAxis.tickFormat(function(d) { return d3.time.format('%x %X')(new Date(d)) });

        chart.yAxis.tickFormat(d3.format('d'));
        chart.yAxis
            .axisLabel('Hit / Miss count')
            .axisLabelDistance(40);
        chart.xAxis
            .axisLabel('Time')
            .axisLabelDistance(40);
            chart.xAxis.rotateLabels(-45);

        d3.select('#casheHitCountChart svg')
        .datum(data)
        .transition().duration(0)


        .call(chart);



        function disableAreaClick() {

            //I'm probably over-killing with the amount of events I'm cancelling out
            //but it doesn't seem to have any side effects:
             chart.legend.dispatch.on("legendClick", null);
                       chart.legend.dispatch.on("legendDblclick", null);
            chart.stacked.dispatch.on("areaClick", null);
            chart.stacked.dispatch.on("areaClick.toggle", null);

            chart.stacked.scatter.dispatch.on("elementClick", null);
            chart.stacked.scatter.dispatch.on("elementClick.area", null);


            chart.legend.dispatch.on("stateChange", null);

            if (chart.update) {
               //if the chart currently has an update function
               //(created when the chart is called on a container selection)
               //then modify it to re-call this function after update

               var originalUpdate = chart.update;
                   //assign the update function to a new name

               chart.update = function(){
                  originalUpdate();
                  disableAreaClick();
               }
            }
          }
        //Call your function, disabling events on current chart and future updates:
          disableAreaClick();
          //this must be called *after* calling the chart on a selection
          //so that it has an update function to modify

        nv.utils.windowResize(chart.update);



        var circle = d3.selectAll("circle");


        $('#dateLabel').html('<h4>Select point to display data</h4>')



        circle.on("click",function(d){

           var unixTime = d[0] ;
           var casheHit = d.seriesIndex;

            chart.useInteractiveGuideline(false)




//------------drill down of the selected point-----------------------------
        for (var i = 0; i < usageByContext.length; i++) {

                    var time = usageByContext[i][0];
                    var str = time;
                    var d = new Date(str.split(' ')[0].split('-').join(',') + ',' +
                        str.split(' ')[1].split('-').join(','));
                    var year = d.getFullYear();
                    var month = d.getMonth();
                    var date = d.getDate();
                    var hour = d.getHours();
                    var min = d.getMinutes();
                    var second = d.getSeconds();

                    var dateInSeconds = dateToUnix(year, (month + 1), date, hour, min, second);
                    var seconds = dateInSeconds ;

            if (seconds == unixTime) {

             var hitOrMiss;
             var hitMissVal;

                            if(casheHit == 0){
                                hitOrMiss = "1";
                                hitMissVal = "Hit"
                            }else{
                                hitOrMiss = "0";
                                hitMissVal = "Miss"

                            }


                            $('div#casheStatTable_wrapper.dataTables_wrapper.no-footer').remove();

                                    var $dataTable = $('<table class="display" width="100%" cellspacing="0" id="casheStatTable" ></table>');
                                    $dataTable.append($('<thead class="tableHead"><tr >'
                                        + '<th>App</th>'
                                        + '<th >Version</th>'
                                        + '<th >Context</th>'
                                        + '<th >Count</th>'
                                        + '</tr></thead>'));

                    if (usageByContext[i][1].length > 1 ) {

                    if(hitOrMiss == usageByContext[i][1][0][0]){

                            numOfVersion = usageByContext[i][1][0][1].length;

                            div.style("left", d3.event.pageX + 10 + "px");
                            div.style("top", d3.event.pageY - 25 + "px");
                            div.style("display", "inline-block");
                            for (var t = 0; t < numOfVersion; t++) {
                                for (var k = 0; k < usageByContext[i][1][0][1][t][1].length; k++) {

                                   for (var l = 0; l < usageByContext[i][1][0][1][t][1][k][1].length; l++) {
                                      appContext = usageByContext[i][1][0][1][t][1][k][1][l][0];
                                      appName = usageByContext[i][1][0][1][t][0];
                                      count = usageByContext[i][1][0][1][t][1][k][1][l][1];
                                      version = usageByContext[i][1][0][1][t][1][k][0];
                                          $dataTable.append('<tr><td>' + appName + '</td><td>'+version+'</td><td>' + appContext +
                                                '</td class="pull-right"><td>' + count + '</td></tr>');

                                   }
                                }


                            }
                            $('#dateLabel').html('<h4>'+hitMissVal +" Count "+time+'</h4>')
                            $('#tableContainer').append($dataTable);
                            $('#tableContainer').show();
                            $('#casheStatTable').dataTable();
                    }
                    else{
                    for (var j = 1; j < 2; j++) {

                  numOfVersion = usageByContext[i][1][j][1].length;
                  div.style("left", d3.event.pageX + 10 + "px");
                  div.style("top", d3.event.pageY - 25 + "px");
                  div.style("display", "inline-block");
                        for (var t = 0; t < numOfVersion; t++) {

                                for (var k = 0; k < usageByContext[i][1][j][1][t][1].length; k++) {
                                    dataTest = [];


                                    for (var l = 0; l < usageByContext[i][1][j][1][t][1][k][1].length; l++) {
                                        appName = usageByContext[i][1][j][1][t][0];
                                        appContext = usageByContext[i][1][j][1][t][1][k][1][l][0]
                                        count = usageByContext[i][1][j][1][t][1][k][1][l][1];
                                        version = usageByContext[i][1][j][1][t][1][k][0];
                                          $dataTable.append('<tr><td>' + appName + '</td><td>'+version+'</td><td>' + appContext +
                                                '</td class="pull-right"><td>' + count + '</td></tr>');
                                    }

                                }
                        }

                    }
                            $('#dateLabel').html('<h4>'+hitMissVal +" Count "+ time +'</h4>')
                            $('#tableContainer').append($dataTable);
                            $('#tableContainer').show();
                            $('#casheStatTable').dataTable();


                    }

                    }else{

                        if(hitOrMiss == usageByContext[i][1][0][0]){
                                numOfVersion = usageByContext[i][1][0][1].length;
                                div.style("left", d3.event.pageX + 10 + "px");
                                div.style("top", d3.event.pageY - 25 + "px");
                                div.style("display", "inline-block");
                                for (var t = 0; t < numOfVersion; t++) {
                                //appVersion = usageByContext[i][1][j][1][t][0]

                                    for (var k = 0; k < usageByContext[i][1][0][1][t][1].length; k++) {

                                       for (var l = 0; l < usageByContext[i][1][0][1][t][1][k][1].length; l++) {
                                          appContext = usageByContext[i][1][0][1][t][1][k][1][l][0];
                                          appName = usageByContext[i][1][0][1][t][0];
                                          count = usageByContext[i][1][0][1][t][1][k][1][l][1];
                                          version = usageByContext[i][1][0][1][t][1][k][0];
                                          $dataTable.append('<tr><td>' + appName + '</td><td>'+version+'</td><td>' + appContext +
                                                '</td class="pull-right"><td>' + count + '</td></tr>');

                                       }
                                    }


                                }
                                $('#dateLabel').html('<h4>'+hitMissVal +" Count "+ time +'</h4>')
                                $('#tableContainer').append($dataTable);
                                $('#tableContainer').show();
                                $('#casheStatTable').dataTable();
                        }

                    }


            }

        }

        });


       circle.on("mouseout", function (d) {
                    div.style("display", "none");
                });




       // return chart;
    });
})(volumeData);

$('#dateLabel').show();
}
else{
 $('.graph-container').html('<h1 class="no-data-heading span10">No data available</h1>');
 $('#tableContainer').hide();
 $('#dateLabel').hide();
}
}













