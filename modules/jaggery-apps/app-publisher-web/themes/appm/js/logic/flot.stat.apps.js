
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

 var parsedResponse;
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


    parsedResponse = JSON.parse(response);

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

            checkStatus(allcount,app)

    }

    function checkStatus(allcount,app) {
    var status =false;

        for(var z = 0; z < dataStructure.length; z++){

            if(app == dataStructure[z].appName.replace(/\s+/g, '')){
                status=true;
                dataStructure[z].checked = true;
                data.push({
                    API_name: app,
                    Subscriber_Count: dataStructure[z].subCount,
                    Hits: allcount,
                    API: app
                });
            }
        }
        if(!status){
            data.push({
                API_name: app,
                Subscriber_Count: 0,
                Hits: allcount,
                API: app
            });
        }

    }

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
        '<th>App</th>' +
        '<th style="text-align:right" width="20%" >Subscriber Count</th>'+
        '<th class="details-control pull-right sorting_disabled"></th>'+
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
                + '<td style="text-align:left;"><label id ="label" for=' + n + '>' + sortData[n].API_name +' </label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Subscriber_Count +'</label></td>'
                +'<td class="details-control sorting_disabled" style="text-align:right;padding-right:30px;">Show more details<div style="display :inline"class="showDetail"></div></td></tr>'));
            state_array.push(true);
            defaultFilterValues.push(sortData[n].API_name);
            chartData.push(sortData[n].API_name);
        }else{
            $dataTable.append($('<tr><td >'
                +'<input name="item_checkbox'+n+'"  id='+n+'  type="checkbox"  data-item='+sortData[n].API_name +' class="inputCheckbox"/>'
                +'</td>'
                +'<td style="text-align:left;"><label id ="label" for='+n+'>'+sortData[n].API_name +' </label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Subscriber_Count +'</label></td>'
                +'<td class="details-control" style="text-align:right;padding-right:30px;">Show more details<div style="display :inline"class="showDetail"></div></td></tr>'));
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
       var table= $('#apiSelectTable').DataTable({
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
                null,
                false,
            ],

        });



        $('.details-control').removeClass('sorting');
        // Array to track the ids of the details displayed rows
       var detailRows = [];


                    $('#apiSelectTable tbody').on( 'click', 'tr td.details-control', function () {
                       var tr = $(this).closest('tr');
                               var row = table.row( tr );
                               if ( row.child.isShown() ) {
                                   // This row is already open - close it
                                   $('div.slider', row.child()).slideUp( function () {
                                                   row.child.hide();
                                                   tr.removeClass('shown');
                                               } );
                               }
                               else {
                                   row.child( format(row.data()), 'no-padding' ).show();
                                   tr.addClass('shown');
                                   $('div.slider', row.child()).slideDown();
                               }
                    } );

                    $('select').css('width','60px');
                    chart.data = dimple.filterData(data, "API", defaultFilterValues);

                    var count=20;

                    //on checkbox check and uncheck event
                    $('#apiSelectTable').on( 'change', 'input.inputCheckbox', function () {
                          var id =  $(this).attr('id');
                          var check=$(this).is(':checked');
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

                    s.afterDraw = function (shp, d) {
                        var shape = d3.select(shp);

                        var circle=d3.select("#"+d.aggField+"_"+d.aggField+"__");

                            circle.on("click", function(d){
                            //circle on click
                            for ( var i = 0; i < parsedResponse.length; i++) {
                                var count = 0;
                                var app ='';

                                if(d.aggField == parsedResponse[i][0].replace(/\s+/g, '')){
                                    var versionCount=[];
                                    for ( var j = 0; j < parsedResponse[i][1].length; j++) {
                                          app =(parsedResponse[i][0]);

                                          var maximumUsers = parsedResponse[i][1][j][1].length;

                                          hitCount = 0;
                                          for ( var k = 0; k < maximumUsers; k++) {
                                            count++;
                                            hitCount = Number(hitCount)+Number(parsedResponse[i][1][j][1][k][1]);
                                          }

                                        versionCount.push({version:parsedResponse[i][1][j][0],count:hitCount});
                                    }

                                div.style("left", d3.event.pageX+10+"px");
                                div.style("top", d3.event.pageY-25+"px");
                                div.style("display", "inline-block");

                                div.html('<div style="color:#555; text-align:left">API : '+app +'</div><div style="color:#666;margin-top:5px;text-align:left">Subscription Count : '+data[i].Subscriber_Count+'</div><table class="table" id="tooltipTable"><thead><tr><th>Version</th><th>Hits</th></tr></thead><tbody></tbody></table>');
                                    for (var l=0;l<versionCount.length;l++){
                                        var versionName=versionCount[l].version;
                                        var version_Count=versionCount[l].count;
                                        $('#tooltipTable tbody').append('<tr><td>'+versionName+'</td><td>'+version_Count+'</td></tr>');
                                    }
                                }
                            }
                        });

                        circle.on("mouseout", function(d){
                           div.style("display", "none");
                        });
                    };
                    chart.draw();
                    window.onresize = function () {

                    chart.draw(0, true);
                    };
                }


}

function format ( d ) {
    var subTable=$('<div class="slider pull-right " ><table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;"></table></div>');
    subTable.append($('<thead><tr><th>Version</th><th>Subscriber Count</th><th>Hit Count</th></tr></thead>'));

    var versions=[];
    var isChecked=false;
    for (var i = 0; i < usageByContext.length; i++) {
        if(usageByContext[i][0] == $(d[0]).attr('data-item')){
            for(var j=0;j<usageByContext[i][1].length;j++){
                versions.push({"apiName":usageByContext[i],"version":usageByContext[i][1][j][0],"SubCount":usageByContext[i][1][j][1],"isChecked":isChecked,"hitCount":0});
            }
        }
    }

    for ( var i = 0; i < parsedResponse.length; i++) {
        var count = 0;
        var app ='';

        if( $(d[0]).attr('data-item')== parsedResponse[i][0].replace(/\s+/g, '')){
            var versionCount=[];
            for ( var j = 0; j < parsedResponse[i][1].length; j++) {
                  app =(parsedResponse[i][0]);
                  var maximumUsers = parsedResponse[i][1][j][1].length;
                  hitCount = 0;

                  for ( var l = 0; l < versions.length; l++) {
                      if(parsedResponse[i][1][j][0]==versions[l].version){
                          versions[l].isChecked=true;
                          for ( var k = 0; k < maximumUsers; k++) {
                            count++;
                            hitCount = Number(hitCount)+Number(parsedResponse[i][1][j][1][k][1]);
                          }
                          versionCount.push({version:parsedResponse[i][1][j][0],count:hitCount});
                          versions[l].hitCount = hitCount;
                      }
                  }
            }

            for ( var l = 0; l < versions.length; l++) {
                if(versions[l].isChecked==false){
                   versionCount.push({version:versions[l].version,count:0});
                   versions[l].isChecked=true;
                }
            }

            for(var k = 0; k < versions.length;k++){
                subTable.append($('<tr><td >'+versions[k].version +'</td><td style="text-align:right">'+versions[k].SubCount+'</td><td style="text-align:right">'+versions[k].hitCount+'</td></tr>'));
            }
        }
    }

    for ( var l = 0; l < versions.length; l++) {
        if($(d[0]).attr('data-item')==versions[l].apiName){
            if(versions[l].isChecked==false){
                   subTable.append($('<tr style="text-align:right"><td >'+versions[l].version +'</td><td >'+versions[l].SubCount+'</td><td style="text-align:right">'+versions[l].hitCount+'</td></tr>'));
            }
        }
    }
    return subTable;
}

