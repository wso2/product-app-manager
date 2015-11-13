var parsedResponse;
$(function () {
    drawGraphs();
});

function drawGraphs() {
    var url = window.location.pathname;
    var comps = url.split('/');
    var type = comps[comps.length - 2];
    var operation = comps[comps.length - 3];
    var action = 'getAppsPopularity';
    var dateRange = $('#date-range').val();

    var from = dateRange.split('to')[0].trim() + ":00";
    var to = dateRange.split('to')[1].trim() + ":00";

    $.ajax({
        async: false,
        url: '/publisher/api/assets/' + operation + '/' + type + '/' + action + '/',
        type: 'POST',
        data: {
            'startDate': from,
            'endDate': to
        },
        success: function (response) {
            parsedResponse = JSON.parse(response);
            drawPopularityOverTime(parsedResponse);
            $('#spinner').hide();
        },
        error: function (response) {
            alert('Error occurred at statistics graph rendering');
        }
    });

    $('.btn-remove').on('click', function () {
        $(this).parents('.graph-maximized').removeClass('graph-maximized');
        $('.backdrop').hide();
        drawPopularityOverTime(parsedResponse);
    });

}

var drawPopularityOverTime = function (parsedResponse) {
    var data = [];
    var length = parsedResponse.length;

    var dataStructure = [];
    var app = '';
    var hitCount = 0;
    $("#tooltipTable").find("tr:gt(0)").remove();
    for (var p = 0; p < parsedResponse.length; p++) {
        var appName = parsedResponse[p].AppName.replace(/\s+/g, '');
        var appNameWithVersion = appName + "_v" + parsedResponse[p].AppVersion;
        data.push({
            App: appNameWithVersion,
            Hits: parsedResponse[p].TotalHits
        });
    }

    $('.graph-container').html('');
    $('#tableContainer').html('');
    var svg = dimple.newSvg(".graph-container", "100%", 500);
    chart = new dimple.chart(svg, data);

    chart.setBounds("10%", "10%", "75%", "60%");
    x = chart.addCategoryAxis("x", "App");

    y = chart.addMeasureAxis("y", "Hits");
    y.title = "Hit Count";    
    var data = [];
    var length = parsedResponse.length;

    var dataStructure = [];
    var app = '';
    var hitCount = 0;
    $("#tooltipTable").find("tr:gt(0)").remove();
    for (var p = 0; p < parsedResponse.length; p++) {
        var appName = parsedResponse[p].AppName.replace(/\s+/g, '');
        var appNameWithVersion = appName + "_v" + parsedResponse[p].AppVersion;
        data.push({
            App: appNameWithVersion,
            Hits: parsedResponse[p].TotalHits
        });
    }

    $('.graph-container').html('');
    $('#tableContainer').html('');
    var svg = dimple.newSvg(".graph-container", "100%", 500);
    chart = new dimple.chart(svg, data);

    chart.setBounds("10%", "10%", "75%", "60%");
    x = chart.addCategoryAxis("x", "App");
    y = chart.addMeasureAxis("y", "Hits");
    chart.addButtonName
    y.title = "Hit Count";
    x.title = "Apps";
    y.tickFormat = '1d';

    s = chart.addSeries("App", dimple.plot.bar);
    var div = d3.select("body").append("div").attr("class", "toolTip");

    var filterValues = dimple.getUniqueValues(data, "App");
    var state_array = [];
    var defaultFilterValues = [];
    var sortData = [];
    var chartData = [];

    var $dataTable = $('<table class="display" width="100%" cellspacing="0" id="apiSelectTable"></table>');
    $dataTable.append($('<thead class="tableHead"><tr>' +
        '<th width="10%"></th>' +
        '<th>App Name</th>' +
        '<th style="text-align:right" width="20%" >Hits Count</th>'+
        '</tr></thead>'));

    sortData = dimple.filterData(data, "App", filterValues);
    sortData.sort(function (obj1, obj2) {
        return obj2.Hits - obj1.Hits;
    });

    for (var n = 0; n < sortData.length; n++) {
        if (n < 20) {
            $dataTable.append($('<tr><td >'
                + '<input name="item_checkbox' + n + '"  checked   id=' + n +
                '  type="checkbox"  data-item=' + sortData[n].App  + ' class="inputCheckbox"/>'
                + '</td>'
                + '<td style="text-align:left;"><label id ="label" for=' + n + '>' + sortData[n].App +' </label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Hits +'</label></td>'
                +'</tr>'));
            state_array.push(true);
            defaultFilterValues.push(sortData[n].App);
            chartData.push(sortData[n].App);
        } else {
            $dataTable.append($('<tr><td >'
                +'<input name="item_checkbox'+n+'"  id='+n+'  type="checkbox"  data-item='+sortData[n].App +' class="inputCheckbox"/>'
                +'</td>'
                +'<td style="text-align:left;"><label id ="label" for='+n+'>'+sortData[n].App +' </label></td>'
                +'<td style="text-align:right;"><label for='+n+'>'+sortData[n].Hits +'</label></td>'
                +'</tr>'));
            state_array.push(false);
            chartData.push(sortData[n].App);
        }
    }

    if (parsedResponse.length == 0) {
        $('.graph-container').html('<h1 class="no-data-heading">No data available</h1>');
    } else {
        $('.graph-container').show();
        $('#tableContainer').append($dataTable);
        $('#tableContainer').show();
        var table= $('#apiSelectTable').DataTable({
            retrieve: true,
            "order": [
                [ 1, "desc" ]
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
            ]
        });

        $('.details-control').removeClass('sorting');
        var detailRows = [];
        $('#apiSelectTable tbody').on( 'click', 'tr td.details-control', function () {
            var tr = $(this).closest('tr');
            var row = table.row( tr );
            if ( row.child.isShown() ) {
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
        chart.data = dimple.filterData(data, "App", defaultFilterValues);
        var count = 20;
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

            chart.data = dimple.filterData(data, "App", draw_chart);
            chart.draw();
        });

        s.afterDraw = function (shp, d) {
            var shape = d3.select(shp);
            var bar = d3.selectAll("rect");
            bar.on("click", function (d) {
                drawPopupChart(parsedResponse, d.aggField, this);
            });
            bar.on("mouseout", function (d) {
                div.style("display", "none");
            });
        };
        chart.draw();
        window.onresize = function () {
            chart.draw(0, true);
        };
    }
}

var onDateSelected = function () {
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

function drawPopupChart(parsedResponse, appName, holderId) {

    $('#lineWithFocusChart').html($('<svg style="height:450px;"></svg></div>'));
    var div = document.getElementById('div-selected-app-name');
    div.innerHTML = "User Hits Of " + appName;
    $(holderId).parents('.widget').addClass('graph-maximized');
    $('.backdrop').show();
    $('.widget-head').show();
    $(holderId).hide();


    $('.btn-remove').on('click', function () {
        $('.widget-head').hide();
        $(holderId).show();
    })

    var userHitsStats = [];

    for (var x = 0; x < parsedResponse.length; x++) {
        var responsedAppName = parsedResponse[x].AppName.replace(/\s+/g, '');
        var appNameWithVersion = responsedAppName + "_v" + parsedResponse[x].AppVersion;
        if (appName == appNameWithVersion) {
            var userHitsResponse = parsedResponse[x].UserHits;
            for (var y = 0; y < userHitsResponse.length; y++) {
                userHitsStats.push({
                    "User": userHitsResponse[y].UserName,
                    "Hits": userHitsResponse[y].Hits
                });
            }
        }
    }

    $('.graph-container').html('');
    $('#tableContainer').html('');
    $('#lineWithFocusChart').html('');
    var svg = dimple.newSvg(".graph-container", "100%", 500);
    chart = new dimple.chart(svg, userHitsStats);

    chart.setBounds("10%", "10%", "75%", "60%");
    x = chart.addCategoryAxis("x", "User");

    y = chart.addMeasureAxis("y", "Hits");
    y.title = "User Hits Count";
    x.title = "User Id";
    y.tickFormat = '1d';

    s = chart.addSeries("User", dimple.plot.bar);
    var div = d3.select("body").append("div").attr("class", "toolTip");


    var filterValues = dimple.getUniqueValues(userHitsStats, "User");
    var state_array = [];
    var defaultFilterValues = [];
    var sortData = [];
    var chartData = [];

    var $dataTable = $('<table class="display" width="90%" cellspacing="0" id="apiSelectTable"></table>');

    $dataTable.append($('<thead class="tableHead"><tr>' +
        '<th width="10%"></th>' +
        '<th>User</th>' +
        '<th style="text-align:right" width="20%" >User Hits Count</th>' +
        '</tr></thead>'));


    sortData = dimple.filterData(userHitsStats, "User", filterValues);
    sortData.sort(function (obj1, obj2) {
        return obj2.Hits - obj1.Hits;
    });

    for (var n = 0; n < sortData.length; n++) {

        if (n < 20) {
            $dataTable.append($('<tr><td >'
                + '<input name="item_checkbox' + n + '"  checked   id=' + n +
                '  type="checkbox"  data-item=' + sortData[n].User + ' class="inputCheckbox"/>'
                + '</td>'
                + '<td style="text-align:left;"><label id ="label" for=' + n + '>' + sortData[n].User + ' </label></td>'
                + '<td style="text-align:right;"><label for=' + n + '>' + sortData[n].Hits + '</label></td>'
                + '</tr>'));
            state_array.push(true);
            defaultFilterValues.push(sortData[n].User);
            chartData.push(sortData[n].User);
        } else {
            $dataTable.append($('<tr><td >'
                + '<input name="item_checkbox' + n + '"  id=' + n + '  type="checkbox"  data-item=' + sortData[n].User + ' class="inputCheckbox"/>'
                + '</td>'
                + '<td style="text-align:left;"><label id ="label" for=' + n + '>' + sortData[n].User + ' </label></td>'
                + '<td style="text-align:right;"><label for=' + n + '>' + sortData[n].Hits + '</label></td>'
                + '</tr>'));
            state_array.push(false);
            chartData.push(sortData[n].App);
        }
    }


    $('#tableContainer').append($dataTable);
    $('#tableContainer').show();

    var table = $('#apiSelectTable').DataTable({
        retrieve: true,
        "order": [
            [ 2, "desc" ]
        ],
        "fnDrawCallback": function () {
            if (this.fnSettings().fnRecordsDisplay() <= $("#apiSelectTable_length option:selected").val()
                || $("#apiSelectTable_length option:selected").val() == -1)
                $('#apiSelectTable_paginate').hide();
            else
                $('#apiSelectTable_paginate').show();
        },
        "aoColumns": [
            { "bSortable": false },
            null,
            null
        ]

    });

    $('.details-control').removeClass('sorting');

    // Array to track the ids of the details displayed rows
    var detailRows = [];
    chart.data = dimple.filterData(userHitsStats, "User", defaultFilterValues);
    var count = 20;
    $('.details-control').removeClass('sorting');

    // Array to track the ids of the details displayed rows
    var detailRows = [];

    $('#apiSelectTable tbody').on('click', 'tr td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        if (row.child.isShown()) {
            // This row is already open - close it
            $('div.slider', row.child()).slideUp(function () {
                row.child.hide();
                tr.removeClass('shown');
            });
        } else {
            row.child(format(row.data()), 'no-padding').show();
            tr.addClass('shown');
            $('div.slider', row.child()).slideDown();
        }
    });


    $('select').css('width', '60px');

    var count = 20;

    //on checkbox check and uncheck event
    $('#apiSelectTable').on('change', 'input.inputCheckbox', function () {
        var id = $(this).attr('id');
        var check = $(this).is(':checked');
        var draw_chart = [];

        if (check) {
            $('#displayMsg').html('');
            count++;
            //limiting to show 20 entries at a time
            if (count > 20) {
                $('#displayMsg').html('<h5 style="color:#555" >Please Note that the graph will be showing only 20 entries</h5>');
                state_array[id] = false;
                $(this).prop("checked", "");
                count--;
            } else {
                state_array[id] = true;
            }
        } else {
            $('#displayMsg').html('');
            state_array[id] = false;
            count--;
        }

        $.each(chartData, function (index, value) {
            if (state_array[index]) {
                draw_chart.push(value);
            }
        });

        chart.data = dimple.filterData(userHitsStats, "User", draw_chart);
        chart.draw();
    });


    s.afterDraw = function (shp, d) {
        var shape = d3.select(shp);
        var circle = d3.selectAll("circle");

        circle.on("mouseout", function (d) {
            div.style("display", "none");
        });
    };
    chart.draw();
    window.onresize = function () {
        chart.draw(0, true);
    };
}
