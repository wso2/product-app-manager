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
    var from = dateRange.split('to')[0].trim() + ":00";
    var to = dateRange.split('to')[1].trim() + ":00";

     $.ajax({
        /* Web Application Last Access Time Graph */
        async: false,
        url: '/publisher/api/assets/' + operation + '/' + type + '/' + action
            + '/',
        type: 'POST',
        data: {
            'startDate': from,
            'endDate': to
        },
        success: function (response) {

            usageByContext = JSON.parse(response);
            $('#spinner').hide();

        },
        error: function (response) {
            alert('Error occured at statistics graph rendering');
        }
    });


    $.ajax({
        async: false,
        url: '/publisher/api/assets/' + operation + '/' + type
            + '/getAPIUsageByUser/',
        type: 'POST',
        data: {
            'startDate': from,
            'endDate': to
        },
        success: function (response) {

            drawAPIUsageByUser(response,usageByContext);
            $('#spinner').hide();
        },
        error: function (response) {
            alert('Error occured at statistics graph rendering');
        }
    });

}


var drawAPIUsageByUser = function (response,usageByContext) {

    var dataStructure = [];
    for (var i = 0; i < usageByContext.length; i++) {
        var Num =0;
        for (var j = 0; j < usageByContext[i][1].length; j++) {
        Num =Num+usageByContext[i][1][j][1];

        }
        dataStructure.push({
            "appName": usageByContext[i][0],
            "subCount": Num,
            "checked" : false

        });
    }


    var parsedResponse = JSON.parse(response);

    length = parsedResponse.length;
    $("#tooltipTable").find("tr:gt(0)").remove();
    var data = [];
    for (var i = 0; i < parsedResponse.length; i++) {

        var statement = '';
        var count = 0;
        var app = '';
        app = (parsedResponse[i][0]);
        app = app.replace(/\s+/g, '');
        allcount = 0;
        for (var j = 0; j < parsedResponse[i][1].length; j++) {
            var newArr = [], found, x, y;



            if (j != 0) {
                statement = statement + '<tr>'
            }

            var maximumUsers = parsedResponse[i][1][j][1].length;


            var origLen = parsedResponse[i][1][j][1].length,

                maxrowspan = parsedResponse[i][1][j][1].length;


            for (var k = 0; k < maximumUsers; k++) {

                found = undefined;
                for (y = 0; y < newArr.length; y++) {

                    if (parsedResponse[i][1][j][1][k][0] === newArr[y]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newArr.push(parsedResponse[i][1][j][1][k][0]);
                }

                count++;
                allcount = Number(allcount) + Number(parsedResponse[i][1][j][1][k][1]);


            }


        }

            myFunction(allcount,app)

    }

    function myFunction(allcount,app) {

        for(var z = 0; z < dataStructure.length; z++){

            if(app == dataStructure[z].appName.replace(/\s+/g, '')){
                dataStructure[z].checked = true;
                data.push({
                    API_name: app,
                    Subscriber_Count: dataStructure[z].subCount,
                    Hits: allcount,
                    API: app
                });
            }
        }
    }
      // alert(JSON.stringify(data))
    for(var p = 0; p < dataStructure.length; p++){

        if(dataStructure[p].checked == false){

            data.push({
                API_name: dataStructure[p].appName,
                Subscriber_Count: dataStructure[p].subCount,
                Hits: 0,
                API: dataStructure[p].appName
            });
        }

    }
    $('.graph-container').html('');
    $('#tableContainer').html('');
    var svg = dimple.newSvg(".graph-container", "100%", 500);
    chart = new dimple.chart(svg, data);

    chart.setBounds("10%", "10%", "75%", "60%");
    x = chart.addCategoryAxis("x", "API");

    y = chart.addMeasureAxis("y", "Subscriber_Count");
    y.title = "Subscriber Count";
    x.title ="APPs";
    y.tickFormat = '1d';

    z = chart.addMeasureAxis("z", "Hits");
    s = chart.addSeries("API", dimple.plot.bubble);
    var div = d3.select("body").append("div").attr("class", "toolTip");


    var filterValues = dimple.getUniqueValues(data, "API");
    var state_array = [];
    var defaultFilterValues=[];
    var sortData=[];
    var chartData=[];

    var $dataTable = $('<table class="display" width="100%" cellspacing="0" id="apiSelectTable"></table>');

    $dataTable.append($('<thead class="tableHead"><tr>' +
        '<th width="10%"></th>' +
        '<th>API</th>' +
        '<th style="text-align:right" width="30%" >Subscriber Count</th>'+
        '</tr></thead>'));

    sortData = dimple.filterData(data, "API", filterValues);
    sortData.sort(function(obj1, obj2) {
        return obj2.Hits - obj1.Hits;
    });

    //default display of 20 checked entries on table
    for (var n = 0; n < sortData.length; n++) {

        if(n<20){
            $dataTable.append($('<tr><td >'
                + '<input name="item_checkbox' + n + '"  checked   id=' + n +
                '  type="checkbox"  data-item=' + sortData[n].API_name  + ' class="inputCheckbox"/>'
                + '</td>'
                + '<td style="text-align:left;"><label for=' + n + '>' + sortData[n].API_name + '</label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Subscriber_Count +'</label></td></tr>'));
            state_array.push(true);
            defaultFilterValues.push(sortData[n].API_name);
            chartData.push(sortData[n].API_name);
        }else{
            $dataTable.append($('<tr><td >'
                +'<input name="item_checkbox'+n+'"  id='+n+'  type="checkbox"  data-item='+sortData[n].API_name +' class="inputCheckbox"/>'
                +'</td>'
                +'<td style="text-align:left;"><label for='+n+'>'+sortData[n].API_name +'</label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Subscriber_Count +'</label></td></tr>'));
            state_array.push(false);
            chartData.push(sortData[n].API_name);

        }
    }

    if (length == 0) {
        $('.graph-container').html('<h1 class="no-data-heading">No data available</h1>');

    } else {

        $('.graph-container').show();
        $('#tableContainer').append($dataTable);
        $('#tableContainer').show();
        $('#apiSelectTable').DataTable({
            retrieve: true,
            "order": [
                [ 2, "desc" ]
            ],
            "fnDrawCallback": function(){
                if(this.fnSettings().fnRecordsDisplay()<=$("#apiSelectTable_length option:selected" ).val()
                || $("#apiSelectTable_length option:selected" ).val()==-1)
                    $('#apiSelectTable_paginate').hide();
                else
                    $('#apiSelectTable_paginate').show();
            },
            "aoColumns": [
                { "bSortable": false },
                null,
                null
            ],

        });

        chart.data = dimple.filterData(data, "API", defaultFilterValues);
        var count=20;

        $('#apiSelectTable').on('change', 'input.inputCheckbox', function () {
            var id = $(this).attr('id');
            var check = $(this).is(':checked');
            var temp = $(this).attr('data-item');
            var draw_chart=[];

            if (check) {
              $('#displayMsg').html('');
              count++;
                //limiting to show 20 entries at a time
                if(count>20){
                    $('#displayMsg').html('<h5 style="color:#555" >Please Note that the graph will be showing only 20 entries</h5>');
                    state_array[id] = false;
                    $(this).prop("checked", "");
                    count--;
                  }else{
                    state_array[id] = true;
                  }
              } else {
                    $('#displayMsg').html('');
                    state_array[id] = false;
                    count--;
              }

              $.each(chartData, function (index, value) {
                    if (state_array[index]){
                        draw_chart.push(value);
                    }
              });

              chart.data = dimple.filterData(data, "API", draw_chart);
            chart.draw();
        });
    }


    s.afterDraw = function (shp, d) {
        var shape = d3.select(shp);
        var circle = d3.select("#" + d.aggField + "_" + d.aggField + "__");

        circle.on("mouseover", function (d) {
            for (var i = 0; i < parsedResponse.length; i++) {
                var count = 0;
                var app = '';

                if (d.aggField == (parsedResponse[i][0]).replace(/\s+/g, '')) {
                    var arr = [];


                    for (var j = 0; j < parsedResponse[i][1].length; j++) {
                        app = (parsedResponse[i][0]);

                        if (j != 0) {
                        }

                        var maximumUsers = parsedResponse[i][1][j][1].length;
                        maxrowspan = parsedResponse[i][1][j][1].length;
                        allcount = 0;
                        for (var k = 0; k < maximumUsers; k++) {
                            if (k != 0) {
                            }
                            count++;
                            allcount = Number(allcount) + Number(parsedResponse[i][1][j][1][k][1]);

                        }
                        arr.push({version: parsedResponse[i][1][j][0], count: allcount});

                    }

                    div.style("left", d3.event.pageX + 10 + "px");
                    div.style("top", d3.event.pageY - 25 + "px");
                    div.style("display", "inline-block");
                    var chartid = "chart" + k;
                    div.html('<b style="color:#555">'+app +'</b><p style="color:#666;margin-top:5px;">Subscription Count : '+data[i].Subscriber_Count+'</p><table class="table" id="tooltipTable" ><thead><tr><th>version</th><th>Hits' +
                        '</th></tr></thead><tbody></tbody></table>');

//div.html('<div style="color:#555; text-align:left">API : '+app +'</div><div style="color:#666;margin-top:5px;text-align:left">Subscription Count : '+data[i].Subscriber_Count+'</div><table class="table" id="tooltipTable"><thead><tr><th>Version</th><th>Hits</th></tr></thead><tbody></tbody></table>');

                    //    div.html('<label>' + "API NAME :" + '</label>')
//$('#tooltipTable tbody').append('<tr><td>' + "API NAME :" + '</td><td>' );
                    for (var l = 0; l < arr.length; l++) {
                        var arrStr = JSON.stringify(arr);
                        var versionName = arr[l].version;
                        var versionCount = arr[l].count;
                        $('#tooltipTable tbody').append('<tr><td>' + versionName + '</td><td>' + versionCount +
                            '</td></tr>');

                    }

                }

            }

        });
        circle.on("mouseout", function (d) {
            div.style("display", "none");
        });

    };
    chart.draw();
    window.onresize = function () {

    chart.draw(0, true);
    };


}

var onDateSelected = function () {
    $('.graph-container').empty();
    drawGraphs();
    data = [];
}

function clearTables() {
    $('#tbody').empty();
    $('.chartContainer').remove();

}

