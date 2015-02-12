var usageByContext;
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
    var to = dateRange.split('to')[1].trim()+":00";
    var userParsedResponse;


    $.ajax({
        async : false,
        url : '/publisher/api/assets/' + operation + '/' + type
            + '/getSubscribedAPIsByUsers/',
        type : 'POST',
        data : {
            'startDate' : from,
            'endDate' : to
        },
        success : function(response) {
            drawSubscribedAPIsByUsers(response);
        },
        error : function(response) {

        }
    });
     $.ajax({
                async : false,
                url : '/publisher/api/assets/' + operation + '/' + type
                    + '/getUsageByContext/',
                type : 'POST',
                data : {
                    'startDate' : from,
                    'endDate' : to
                },
                success : function(response) {
                      usageByContext= response;


                },
                error : function(response) {
                    alert('Error occured at statistics graph rendering');
                }
            });

            $('.btn-remove').on('click', function() {
                    $(this).parents('.graph-maximized').removeClass('graph-maximized');
                    $('.backdrop').hide();
                });

}

var drawSubscribedAPIsByUsers = function(response) {
    var parsedResponse = JSON.parse(response);
    //var parsedResponse =[["admin", [["Net Usage Analyser", [["1", "2014-11-28"], ["2", "2014-11-28"]]], ["Event Management", [["1", "2014-11-28"]]], ["Travel Claims", [["1", "2014-11-28"]]], ["pizza", [["1", "2014-11-17"]]], ["Hardware Repo", [["1", "2014-11-28"]]], ["test", [["1", "2014-11-28"]]], ["loooooooooooooooooooongnaaaaaaaaaaaaaame", [["1", "2014-11-18"]]], ["Travel Booking", [["1", "2014-11-28"]]], ["Leave Managment", [["1", "2014-11-28"]]], ["testapp12", [["1", "2014-11-28"]]], ["Conference Booking", [["1", "2014-11-28"]]], ["sample", [["1", "2014-11-17"]]], ["Sales Tracking Portal", [["1", "2014-11-28"]]], ["testapp2", [["1", "2014-11-28"]]], ["webappsample", [["1", "2014-11-17"]]]]], ["john", [["Net Usage Analyser", [["1", "2014-11-28"]]], ["Travel Claims", [["1", "2014-11-28"]]], ["Hardware Repo", [["1", "2014-11-28"]]], ["Travel Booking", [["1", "2014-11-28"]]], ["Leave Managment", [["1", "2014-11-28"]]]]], ["peter", [["Net Usage Analyser", [["1", "2014-11-28"]]], ["Travel Claims", [["1", "2014-11-28"]]], ["Hardware Repo", [["1", "2014-11-28"]]], ["Travel Booking", [["1", "2014-11-28"]]], ["Leave Managment", [["1", "2014-11-28"]]]]], ["test1", [["pizza", [["1", "2014-11-21"]]], ["sample", [["1", "2014-11-21"]]], ["webappsample", [["1", "2014-11-21"]]]]], ["test", [["pizza", [["1", "2014-11-18"]]], ["MyApp", [["1", "2014-11-28"]]], ["sample", [["1", "2014-11-17"]]], ["webappsample", [["1", "2014-11-17"]]]]]]    //search bar
    var substringMatcher = function (strs) {
        return function findMatches(q, cb) {
            var matches, substringRegex;

            // an array that will be populated with substring matches
            matches = [];

            // regex used to determine if a string contains the substring `q`
            substrRegex = new RegExp(q, 'i');

            // iterate through the pool of strings and for any string that
            // contains the substring `q`, add it to the `matches` array
            $.each(strs, function (i, str) {
                if (substrRegex.test(str)) {
                    // the typeahead jQuery plugin expects suggestions to a
                    // JavaScript object, refer to typeahead docs for more info
                    matches.push({
                        value: str
                    });
                }
            });

            cb(matches);
        };
    };
    var states=[];
    for ( var i = 0; i < parsedResponse.length; i++) {

                states.push(parsedResponse[i][0]);
        }

    $('input.typeahead').typeahead({
        hint: true,
        highlight: true,
        minLength: 1
    }, {
        name: 'states',
        displayKey: 'value',
        source: substringMatcher(states)
    });
    $('.typeahead.input-lg').siblings('input.tt-hint').addClass('hint-large');
    //$('.typeahead.input-lg').siblings('input.tt-hint').addClass('hint-large');

    tableStatement ='';
    rawStatement='';
    /* Overall Web Application Usage Graph */
    $( "#target" ).click(function() {

    $('div#webAppTable2_wrapper.dataTables_wrapper.no-footer').remove();

    	var $dataTable =$('<table class="display" width="100%" cellspacing="0" id="webAppTable2" ></table>');
    	$dataTable.append($('<thead class="tableHead"><tr >'
    	                                + '<th id ="webApp">Web App</th>'
    	                                + '<th width="10%">App Version</th>'
    	                                + '<th width="40%">Subscribe Date</th>'
    	                                + '<th >History</th>'
   	                + '</tr></thead>'));

        //$('#placeholder2').remove();
        //$('#webAppTable2').find("tr:gt(0)").remove();
        var detailNumber = 0;
        var rawNumber = 0;
        for ( var i = 0; i < parsedResponse.length; i++) {

            if( parsedResponse[i][0] ==  $("#search").val()){
                //alert("nums of apps length "+parsedResponse[i][1].length)
                for ( var j = 0; j < parsedResponse[i][1].length; j++) {
                    numOfVersion = parsedResponse[i][1][j][1].length;

                    for( var t = 0; t < numOfVersion; t++) {
                    //alert("number of vertion in app"+parsedResponse[i][1][j][0]+" "+parsedResponse[i][1][j][1].length)
                         $dataTable.append(
                            $('<tr id='+rawNumber+'><td id="appName">' +parsedResponse[i][1][j][0] + '</td><td id="appVersion">' + parsedResponse[i][1][j][1][t][0]
                            + '</td><td>' + parsedResponse[i][1][j][1][t][1] +'</td><td><a  href="#" class="trigger-ajax" id='+detailNumber+'>View History</a>'
                            + '</td></tr>'));
                            detailNumber++;
                            rawNumber++;
                    }
                }
            //
            }

       }

            if (parsedResponse.length == 0) {

                      $('#webAppTable2').hide();
                      //$('#tempLoadingSpace').html('');
                      $('#placeholder2').append($('<span class="label label-info">No data available</span>'));

                  }else{
               $('#placeholder2').append($dataTable);
               $('#flot-placeholder').append($('<div id="lineWithFocusChart"><svg style="height:450px;"></svg></div>'));
               $('#placeholder2').show();
               $('#webAppTable2').dataTable();
               }

    });

    //ajax call to get hits
    $("#placeholder2").on("click", ".trigger-ajax",function(){
    alert(usageByContext)
      $(this).parents('.widget').addClass('graph-maximized');
      $('.backdrop').show();
      $('#placeholder2').hide();
      $('#searchUserForm').hide();

      $('.btn-remove').on('click',function(){
        $('#placeholder2').show();
        $('#searchUserForm').show();
        var svg = d3.select("svg");
        svg.selectAll("*").remove();

      })

        var answerid = $(this).attr('id');
        var test= $(this).closest('tr').attr('id');

        function getCell(column, row) {
            var column = $('#' + column).index();
            var row = $('#' + row)
            return row.find('td').eq(column);

        }

        //alert(getCell('webApp', ''+test+'').html());
        //var usageByContext=[["admin", [["Travel Claims", [["1", [["/travelclaims", [[22, "2014-11-28 09:22:00"],[29, "2014-11-28 10:22:00"],[1, "2014-11-28 09:22:00"] [19, "2014-11-28 09:20:00"], [1, "2014-11-28 09:19:00"]]]]]]], ["Leave Managment", [["1", [["/leavemanagement", [[17, "2014-11-28 09:20:00"], [11, "2014-11-28 09:22:00"], [1, "2014-11-28 09:21:00"], [1, "2014-11-28 09:18:00"]]]]]]], ["Travel Booking", [["1", [["/travelbooking", [[15, "2014-11-28 09:20:00"], [12, "2014-11-28 09:21:00"], [2, "2014-11-28 09:18:00"]]]]]]], ["Net Usage Analyser", [["1", [["/netusage", [[15, "2014-11-28 09:20:00"], [13, "2014-11-28 09:21:00"], [2, "2014-11-28 09:18:00"]]]]]]], ["Hardware Repo", [["1", [["/hardwarerepo", [[12, "2014-11-28 09:19:00"], [5, "2014-11-28 09:21:00"], [1, "2014-11-28 09:17:00"]]]]]]], ["Event Management", [["1", [["/eventmanagement", [[11, "2014-11-28 10:07:00"]]]]]]], ["Conference Booking", [["1", [["/conferencebooking", [[9, "2014-11-28 10:01:00"]]]]]]], ["sample", [["1", [["/sample", [[2, "2014-11-21 14:02:00"], [1, "2014-11-21 14:25:00"], [1, "2014-11-24 16:17:00"], [1, "2014-11-24 15:15:00"], [1, "2014-11-21 13:48:00"], [1, "2014-11-25 11:11:00"], [1, "2014-11-28 09:02:00"], [1, "2014-11-21 13:44:00"], [1, "2014-11-21 14:00:00"], [1, "2014-11-21 14:50:00"], [1, "2014-11-21 14:04:00"], [1, "2014-11-21 14:24:00"], [1, "2014-11-21 14:06:00"], [1, "2014-11-21 14:48:00"], [1, "2014-11-21 13:45:00"], [1, "2014-11-21 14:23:00"], [1, "2014-11-25 10:15:00"], [1, "2014-11-24 16:44:00"], [1, "2014-11-21 14:09:00"]]]]]]], ["Sales Tracking Portal", [["1", [["/salestrackingportal", [[2, "2014-11-28 09:59:00"]]]]]]], ["pizza", [["1", [["/pizza", [[1, "2014-11-25 11:13:00"], [1, "2014-11-26 11:40:00"]]]]]]]]], ["test", [["pizza", [["1", [["/pizza", [[14, "2014-11-21 15:09:00"], [8, "2014-11-21 15:08:00"], [7, "2014-11-21 15:07:00"]]]]]]], ["webappsample", [["1", [["/webappsample", [[12, "2014-11-21 15:08:00"], [10, "2014-11-21 15:09:00"], [5, "2014-11-21 15:07:00"]]]]]]], ["sample", [["1", [["/sample", [[7, "2014-11-21 15:06:00"], [6, "2014-11-21 15:07:00"], [4, "2014-11-21 15:10:00"], [2, "2014-11-21 15:09:00"]]]]]]], ["MyApp", [["1", [["/myapp", [[5, "2014-11-28 10:47:00"], [4, "2014-11-28 10:46:00"], [3, "2014-11-28 10:49:00"], [1, "2014-11-28 10:58:00"], [1, "2014-11-28 10:57:00"]]]]]]]]]]
        var data =[]

        for ( var i = 0; i < usageByContext.length; i++) {

                            if( usageByContext[i][0] ==  $("#search").val()){
                                //alert("nums of apps length "+parsedResponse[i][1].length)
                                //alert("user equal "+usageByContext[i][0])
                                for ( var j = 0; j < usageByContext[i][1].length; j++) {
                                    if( usageByContext[i][1][j][0] ==  getCell('webApp', ''+test+'').html()){
                                   // alert("app equal "+usageByContext[i][1][j][0])
                                       numOfVersion = usageByContext[i][1][j][1].length;
                                        for( var t = 0; t < numOfVersion; t++) {
                                                if( usageByContext[i][1][j][1][t][0] ==  getCell('appVersion', ''+test+'').html()){
                                                   // alert("version equal "+usageByContext[i][1][j][1][t][0])

                                                    for( var k = 0; k < usageByContext[i][1][j][1][t][1].length; k++) {

                                                    dataTest=[];
                                                    function dateToUnix(year, month, day, hour, minute, second) {
                                                                        return ((new Date(year, month - 1, day, hour, minute, second)).getTime());
                                                                    }
                                                        for( var l = 0; l < usageByContext[i][1][j][1][t][1][k][1].length; l++) {
                                                            //dataTest.push([usageByContext[i][1][j][1][t][1][k][1][l][0],usageByContext[i][1][j][1][t][1][k][1][l][1]]);

                                                            hits=usageByContext[i][1][j][1][t][1][k][1][l][0];
                                                                    //hits.push([parsedResponse[i][1][j][1][t][1][k][0],parsedResponse[i][1][j][1][t][1][k][1]]);
                                                                 var time=usageByContext[i][1][j][1][t][1][k][1][l][1];
                                                                 var str = time;
                                                                 //alert(str)
                                                                 var d=new Date(str.split(' ')[0].split('-').join(',') + ',' + str.split(' ')[1].split('-').join(','));

                                                                 var year= d.getFullYear();
                                                                 var month=d.getMonth();
                                                                 var date= d.getDate();
                                                                 var hour=d.getHours();
                                                                 var min= d.getMinutes();
                                                                 var second=d.getSeconds();



                                                                var dateInSeconds = dateToUnix(year,(month+1),date,hour,min, second);
                                                                var s=dateInSeconds+"";
                                                               // alert(s)

                                                            dataTest.push({
                                                                     'y':hits,
                                                                     'x':s
                                                                 });

                                                        }


                                                dataTest.sort(function(obj1, obj2) {

                                                return obj1.x - obj2.x;
                                                });
                                                //alert(JSON.stringify(dataTest));



                                                nv.addGraph(function () {
                                                    var chart = nv.models.lineWithFocusChart().margin({right: 250});
                                                    chart.margin({left:200});
                                                    chart.color(d3.scale.category20b().range());
                                                    chart.yAxis.tickFormat(d3.format(',d'));
                                                    chart.y2Axis.tickFormat(d3.format(',d'));
                                                    chart.xAxis.tickFormat(function (d) {
                                                        return d3.time.format('%d %b %Y %H:%M')(new Date(d))
                                                    });

                                                    chart.x2Axis.tickFormat(function (d) {
                                                        return d3.time.format('%d %b %Y %H:%M')(new Date(d))
                                                    });
                                                    chart.tooltipContent(function (key, y, e, graph) {
                                                        var x = d3.time.format('%d %b %Y %H:%M:%S')(new Date(parseInt(graph.point.x)));
                                                        var y = String(graph.point.y);
                                                        if (key == 'Hits') {
                                                            var y = 'There is ' + String(graph.point.y) + ' calls';
                                                        }

                                                        tooltip_str = '<center><b>' + key + '</b></center>' + y + ' on ' + x;
                                                        return tooltip_str;
                                                    });


                                                    d3.select('#lineWithFocusChart svg')
                                                        .datum(data_lineWithFocusChart)
                                                        .transition().duration(500)
                                                        .attr('height', 450)
                                                        .call(chart);

                                                    return chart;
                                                });
                                                data_lineWithFocusChart = [{
                                                    'values': dataTest,
                                                    'key': usageByContext[i][1][j][1][t][1][k][0],
                                                    'yAxis': '1'

                                                    }];


                                                    }

                                                }

                                            }
                                        }
                                    }
                                }

                            }
                            //[["/netusage",[[15,"2014-11-28 09:20:00"],[13,"2014-11-28 09:21:00"],[2,"2014-11-28 09:18:00"]]]]


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

