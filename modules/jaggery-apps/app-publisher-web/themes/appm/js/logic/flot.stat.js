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
        /* Web Application Last Access Time Graph */
        url : '/publisher/api/assets/' + operation + '/' + type + '/' + action
            + '/',
        type : 'POST',
        data : {
            'startDate' : from,
            'endDate' : to
        },
        success : function(response) {
            drawSubscriberCountByAPIs(response);
        },
        error : function(response) {
            alert('Error occured at statistics graph rendering');
        }
    });

    $.ajax({
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
            alert('Error occured at statistics graph rendering');
        }
    });

    $.ajax({
        url : '/publisher/api/assets/' + operation + '/' + type
            + '/getAPIUsageByUser/',
        type : 'POST',
        data : {
            'startDate' : from,
            'endDate' : to
        },
        success : function(response) {
            drawAPIUsageByUser(response);
        },
        error : function(response) {
            alert('Error occured at statistics graph rendering');
        }
    });

    $.ajax({
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

    $.ajax({
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
        $('.backdrop').hide();
    });

    $('.btn-remove').on('click', function() {
        $(this).parents('.widget').removeClass('graph-maximized');
        $('.backdrop').hide();
    });

}

var drawSubscriberCountByAPIs = function(response) {
	var parsedResponse = JSON.parse(response);

	/* Web Application Last Access Time Graph */
	$('#placeholder1').append(
			$('<table class="table graphTable" id="webAppTable">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Subscriptions</th>' + '</tr>'
					+ '</table>'));
	for ( var i = 0; i < parsedResponse.length; i++) {
		$('#webAppTable').append(
				$('<tr><td>' + parsedResponse[i].apiName
						+ '</td><td class="NumberCell">'
						+ parsedResponse[i].count + '</td></tr>'));

	}

	var data = [];

	for ( var i = 0; i < parsedResponse.length; i++) {
		data[i] = {
			label : parsedResponse[i].apiName,
			data : parsedResponse[i].count
		}
	}

    $.plot(
        '#placeholder',
        data, {
            series: {
                pie: {
                    show: true,
                    radius: 1,
                    label: {
                        show: true,
                        radius: 1 / 2,
                        formatter: function(label, series) {
                            return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                                +Math.round(series.percent)
                                + '%</div>';
                        }
                    }
                }
            },
            grid: {
                hoverable: true
            },
            tooltip: true,
            tooltipOpts: {
                content: "%p.0%, %s", // show percentages, rounding to 2 decimal places
                shifts: {
                    x: 20,
                    y: 0
                },
                defaultTheme: true
            },
            legend: {
                show: true
            }
        });

}

var drawSubscribedAPIsByUsers = function(response) {
	var parsedResponse = JSON.parse(response);

	/* Overall Web Application Usage Graph */
	$('#placeholder2').append(
			$('<table class="table graphTable" id="webAppTable2">' + '<tr>'
					+ '<th>User</th>' + '<th>Web Apps</th>' + '</tr>'
					+ '</table>'));
	for ( var i = 0; i < parsedResponse.length; i++) {
		var apps = parsedResponse[i].apps;

		$('#webAppTable2').append(
				$('<tr><td>' + parsedResponse[i].user + '</td><td>' + apps
						+ '</td></tr>'));

	}

}

	var drawAPIUsageByUser = function(response) {
	
	var parsedResponse = JSON.parse(response);
	

	/* Overall Web Application Usage Graph */
	$('#placeholder3').append(
			$('<table class="table graphTable" id="webAppTable3">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Version</th>' + '<th>User</th>'
					+ '<th>Number of Access</th>' + '</tr>' + '</table>'));

	var tableStatement = '';

	for ( var i = 0; i < parsedResponse.length; i++) {
		var statement = '';
		var count = 0;

		for ( var j = 0; j < parsedResponse[i][1].length; j++) {

			if (j != 0) {
				statement = statement + '<tr>'
			}
			
			var maximumUsers = parsedResponse[i][1][j][1].length;
			maxrowspan = parsedResponse[i][1][j][1].length;
			if(maximumUsers > 5){
				
				maximumUsers  = 5;
			}
			statement = statement + '<td rowspan='
					+ maximumUsers + '>'
					+ parsedResponse[i][1][j][0] + '</td>'

			
					
			for ( var k = 0; k < maximumUsers; k++) {
				if (k != 0) {
					statement = statement + '<tr>'
				}
				count++;

				for ( var l = 0; l < parsedResponse[i][1][j][1][k].length; l++) {
					statement = statement + '<td>'
							+ parsedResponse[i][1][j][1][k][l] + '</td>'
				}
				statement = statement + '</tr>'

			}
		}
		userParsedResponse = parsedResponse;

		statement = '<tr><td rowspan='+ count + '><a id="link" href="#" onclick="$(this).parents(\'.widget\').addClass(\'graph-maximized\');$(\'.backdrop\').show();maximizeTable('+  i+ ');">' + parsedResponse[i][0] + '</td>' + statement;
		tableStatement = tableStatement + statement;
		

	}

	$('#webAppTable3').append($(tableStatement));
	
	
}

function maximizeTable(row) {

	$('#placeholder31').append(
			$('<table class="table graphTable" id="webAppTable31">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Version</th>' + '<th>User</th>'
					+ '<th>Number of Access</th>' + '</tr>' + '</table>'));
	
	
	
	//var parsedResponse = JSON.parse(response);
	//var parsedResponse = userParsedResponse;
		var maxviewTableStatement = '';
	
		var maxviewStatement  = '';
		var maxviewCount =0;
		
		for(var j=0;j<userParsedResponse[row][1].length;j++){
	
			if(j != 0){
				maxviewStatement = maxviewStatement + '<tr>'
			}
			maxviewStatement = maxviewStatement + '<td rowspan='+ userParsedResponse[row][1][j][1].length + '>' + userParsedResponse[row][1][j][0] + '</td>'
	
			for(var k=0;k<userParsedResponse[row][1][j][1].length;k++){
				  if(k != 0){
					  maxviewStatement = maxviewStatement +  '<tr>'
				  }
				  maxviewCount++;
	
			      for(var l=0;l<userParsedResponse[row][1][j][1][k].length;l++){
			    	  maxviewStatement = maxviewStatement +'<td>'+ userParsedResponse[row][1][j][1][k][l]  +'</td>'
			       }
			      maxviewStatement = maxviewStatement +'</tr>'
				
			}
		}
		
		maxviewStatement = '<tr><td rowspan='
			+ maxviewCount
			+ '>' + userParsedResponse[row][0] + '</td>' + maxviewStatement;
		maxviewTableStatement = maxviewTableStatement + maxviewStatement;
		
		
		$('#webAppTable31').append($(maxviewTableStatement));
		
		
		$('#userbtnremove').on('click', function() {
			
			$('#webAppTable31').remove();
			$('.backdrop').hide();
		});


}

var drawAPIResponseTime = function(response) {

	var parsedResponse = JSON.parse(response);

	$('#placeholder4').append(
			$('<table class="table graphTable" id="webAppTable4">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Page</th>'
					+ '<th>Response Time(ms)</th>' + '</tr>' + '</table>'));

	var tableStatement = '';

	for ( var i = 0; i < parsedResponse.webapps.length; i++) {

		var statement = '';
		var count = 0;

		for ( var j = 0; j < parsedResponse.webapps[i][1].length; j++) {

			if (j != 0) {
				statement = statement + '<tr>'
			}

			count++;

			for ( var l = 0; l < parsedResponse.webapps[i][1][j].length; l++) {
				statement = statement + '<td>'
						+ parsedResponse.webapps[i][1][j][l] + '</td>'
			}
			statement = statement + '</tr>'

		}

		statement = '<tr><td rowspan=' + count + '>'
				+ parsedResponse.webapps[i][0] + '</td>' + statement;
		tableStatement = tableStatement + statement;

	}

	$('#webAppTable4').append($(tableStatement));

	var max = 0;
	for (t = 0; t < parsedResponse.serviceTime.length; t++) {
		if (max < parsedResponse.serviceTime[t][0]) {
			max = parsedResponse.serviceTime[t][0];
		}
	}

	max = max + 100;

	var data2 = [ {
		data : parsedResponse.serviceTime,
		color : '#5482FF',
		bars : {
			show : true,
			barWidth : 0.5,
			align : "center",
			horizontal : true,
			order : 1
		}
	} ];

	var options2 = {
		yaxis : {
			show : true,
			ticks : parsedResponse.referer,
            tickLength: 0,
            axisLabel: "Web Pages"
		},
		xaxis : {
			show : true,
			min : 0,
			max : max,
            axisLabelUseCanvas :false,
            axisLabel: "<b>Response Time(ms)</b>"

		}

	};

	$.plot($("#placeholder41"), data2, options2);
}

var drawAPIUsageByPage = function(response) {

	var parsedResponse = JSON.parse(response);

	$('#placeholder5').append(
			$('<table class="table graphTable" id="webAppTable5">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Number of Access</th>'
					+ '</tr>' + '</table>'));
	for ( var i = 0; i < parsedResponse.webapp.length; i++) {
		$('#webAppTable5').append(
				$('<tr><td>' + parsedResponse.webapp[i][1]
						+ '</td><td class="NumberCell">'
						+ parsedResponse.totalPageCount[i][1] + '</td><tr>'));
	}

	var data = parsedResponse.totalPageCount;
	var dataset = [ {
		data : data,
		color : "#5482FF"
	} ];
	var ticks = parsedResponse.webapp;

	var options = {
		series : {
			bars : {
				show : true
			}
		},
		bars : {
			align : "center",
			barWidth : 0.3
		},
		xaxis : {
            axisLabelUseCanvas :false,
			axisLabel : "<b>Web Apps</b>",
			tickLength : 0,
			ticks : ticks
		},
		yaxis : {
			axisLabel : "Total Request"

		},
		grid : {
			clickable : true,
			borderWidth : 1
		}
	};

	$.plot($("#placeholder51"), dataset, options);

    $("#placeholder51").bind("plotclick", function (event, pos, item) {
        if (item != null) {
            $(this).parents('.widget').addClass('graph-maximized');
            $('.backdrop').show();

            var x = item.datapoint[0];
            var label = item.series.xaxis.ticks[x].label;
            var webappPage = [];
            var webappPageCount = [];

            for (t = 0; t < parsedResponse.webappDeatails.length; t++) {
                if (label == parsedResponse.webappDeatails[t][0]) {
                    webappPage = parsedResponse.webappDeatails[t][1];
                    webappPageCount = parsedResponse.webappDeatails[t][2]
                }
            }

            var max = 0;
            for (t = 0; t < webappPageCount.length; t++) {
                if (max < webappPageCount[t][0]) {
                    max = webappPageCount[t][0];
                }
            }

            max = max + 10;

            var data = webappPageCount;
            var dataset = [
                {
                    data: data,
                    color: "#5482FF"
                }
            ];
            var ticks = webappPage;

            var options = {
                series: {
                    bars: {
                        show: true,
                        horizontal: true

                    }
                },
                bars: {
                    align: "center",
                    barWidth: 0.5
                },
                xaxis: {
                    axisLabelUseCanvas: false,
                    axisLabel: "<b>Total Request Count</b>",
                    reserveSpace: true,
                    labelWidth: 150,
                    min: 0,
                    max: max
                },
                yaxis: {
                    ticks: ticks,
                    tickLength: 0,
                    axisLabel: "Accessed Page"

                }

            };

            $.plot($("#placeholder52"), dataset, options);
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


