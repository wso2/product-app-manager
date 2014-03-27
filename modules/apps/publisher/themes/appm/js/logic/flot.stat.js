$(function() {
	var url = window.location.pathname;

	var comps = url.split('/');
	var type = comps[comps.length - 2];
	var operation = comps[comps.length - 3];
	var action = 'getSubscriberCountByAPIs';

	var dateRange = $('#date-range-field span').text();
	var from = dateRange.split('to')[0];
	var to = dateRange.split('to')[1];

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
				+ '/getProviderAPIUsage/',
		type : 'POST',
		data : {
			'startDate' : from,
			'endDate' : to
		},
		success : function(response) {
			drawProviderAPIUsage(response);
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

});

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

	$.plot('#placeholder', data, {
		series : {
			pie : {
				show : true,
				radius : 1,
				label : {
					show : true,
					radius : 3 / 4,
					formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    }
				}
			}
		},
		legend : {
			show : false
		}
	});

}

var drawProviderAPIUsage = function(response) {
	var parsedResponse = JSON.parse(response);
	/* Overall Web Application Usage Graph */
	$('#placeholder2').append(
			$('<table class="table graphTable" id="webAppTable2">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Number of Calls</th>' + '</tr>'
					+ '</table>'));
	for ( var i = 0; i < parsedResponse.length; i++) {
		$('#webAppTable2').append(
				$('<tr><td>' + parsedResponse[i].apiName
						+ '</td><td class="NumberCell">'
						+ parsedResponse[i].count + '</td></tr>'));
	}
}

var drawAPIUsageByUser = function(response) {


	var parsedResponse = JSON.parse(response);

	var data = [];
	var webapps = [0];

	for (x = 0; x < parsedResponse.length; x++) {

		if (data.length == 0) {
			
			var webappArray = [];
			var webappindex = -1;
			if(webapps.indexOf(parsedResponse[x].apiName) == -1){

				 webapps.push(parsedResponse[x].apiName);
				 webappindex = webapps.indexOf(parsedResponse[x].apiName);
			 }
			 else{

				 webappindex = webapps.indexOf(parsedResponse[x].apiName);

			 }
			
			 webappArray.push([webappindex,parsedResponse[x].count]);
			 data[data.length] = {label : parsedResponse[x].userId,
					             data : webappArray
			                    };


		} else {
			var index = -1;

			for (y = 0; y < data.length; y++) {
				if (data[y].label == parsedResponse[x].userId) {
					index = y;
				}
				
			}

			
			if(index == -1){
				var webappArray = [];
				var webappindex;
				 if(webapps.indexOf(parsedResponse[x].apiName) == -1){
					 webapps.push(parsedResponse[x].apiName);
					 webappindex = webapps.indexOf(parsedResponse[x].apiName);
				 }
				 else{
					 webappindex = webapps.indexOf(parsedResponse[x].apiName);
				 }
				 
				 webappArray.push([webappindex,parsedResponse[x].count]);
				
				 data[data.length] = { label : parsedResponse[x].userId,
						               data : webappArray
				                      };
				 
			} else{

				var webappArray  = data[index].data;
				var webappindex;

				if(webapps.indexOf(parsedResponse[x].apiName) == -1){
					 webapps.push(parsedResponse[x].apiName);
					 webappindex = webapps.indexOf(parsedResponse[x].apiName);
				 }
				 else{

					 webappindex = webapps.indexOf(parsedResponse[x].apiName);
				 }

				webappArray.push([webappindex,parsedResponse[x].count]);
				 data[index] = {label : parsedResponse[x].userId,
	                                  data : webappArray
                                     };
				
			}
			
			
		}

	}
	
	var webappNames = [];
	for(z=0;z<webapps.length;z++){
		webappNames.push([z,webapps[z]]);
	}
	
		
	var options = {
            xaxis: {
                min: 0,
                max: 7,
                mode: null,
                ticks: webappNames,
                tickLength: 0,
                axisLabel: "Web Apps",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: "Verdana, Arial, Helvetica, Tahoma, sans-serif",
                axisLabelPadding: 5
            }, yaxis: {
                axisLabel: "Number Of Access",
                tickDecimals: 0,
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: "Verdana, Arial, Helvetica, Tahoma, sans-serif",
                axisLabelPadding: 5
            }, grid: {
                hoverable: true,
                clickable: false,
                borderWidth: 1
            }, legend: {
                labelBoxBorderColor: "none",
                position: "right"
            }, series: {
                shadowSize: 1,
                bars: {
                    show: true,
                    barWidth: 0.13,
                    order: 1
                }
            }
        };
	
	$.plot($("#placeholder31"), data, options);

	/* Overall Web Application Usage Graph */
	$('#placeholder3').append(
			$('<table class="table graphTable" id="webAppTable3">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Version</th>'
					+  '<th>User</th>'
					+ '<th>Number of Access</th>' + '</tr>' + '</table>'));
	for ( var i = 0; i < parsedResponse.length; i++) {
		$('#webAppTable3').append(
				$('<tr><td>' + parsedResponse[i].apiName + '</td><td>'
						+ parsedResponse[i].version + '</td><td>'
						+ parsedResponse[i].userId
						+ '</td><td class="NumberCell" >'
						+ parsedResponse[i].count + '</td><tr>'));
	}

}

var drawAPIResponseTime = function(response) {

	var parsedResponse = JSON.parse(response);
	var data2 = [ {
		data : parsedResponse.serviceTime,
		color : '#FFC826',
		bars : {
			show : true,
			barWidth : 0.6,
			align : "center",
			horizontal : true
		}
	} ];

	var options2 = {
		yaxis : {
			show : true,
			ticks : parsedResponse.apiname,
			tickLength : 0
		},
		xaxis : {
			show : true,
			min : 0,
			max : 1000

		}

	};

	$.plot($("#placeholder4"), data2, options2);
}

var drawAPIUsageByPage = function(response) {
	console.log("test");
	console.log(response);

	var parsedResponse = JSON.parse(response);

	$('#placeholder5').append(
			$('<table class="table graphTable" id="webAppTable5">' + '<tr>'
					+ '<th>Web App</th>' + '<th>Version</th>' + '<th>User</th>'
					+ '<th>Page</th>' + '<th>Number of Access</th>' + '</tr>'
					+ '</table>'));
	for ( var i = 0; i < parsedResponse.length; i++) {
		$('#webAppTable5').append(
				$('<tr><td>' + parsedResponse[i].apiName + '</td><td>'
						+ parsedResponse[i].version + '</td><td>'
						+ parsedResponse[i].userid + '</td><td>'
						+ parsedResponse[i].referer
						+ '</td><td class="NumberCell">'
						+ parsedResponse[i].count + '</td><tr>'));
	}

}

var convertDate = function(date) {
	var month = date.getMonth() + 1;
	var day = date.getDate();
	return date.getFullYear() + '-' + (('' + month).length < 2 ? '0' : '')
			+ month + '-' + (('' + day).length < 2 ? '0' : '') + day;
}
var onDateSelected = function(from, to) {
	var url = window.location.pathname;
	var comps = url.split('/');

	var type = comps[comps.length - 2];
	var operation = comps[comps.length - 3];
	$.ajax({
		url : '/publisher/api/assets/' + operation + '/' + type + '/',
		type : 'POST',
		data : {
			'startDate' : from,
			'endDate' : to,
			'isOnChoice' : true
		},
		success : function(response) {
			var parsedResponse = JSON.parse(response);

			/* Hot assets stats graph */
			var data2 = [ {
				data : parsedResponse.hotAssetStats,
				color : '#FFC826',
				label : 'Assets',
				bars : {
					show : true,
					barWidth : 0.6,
					align : "center"
				}
			} ];

			var options2 = {
				yaxis : {
					show : true,
					tickDecimals : 0

				},
				xaxis : {
					labelAngle : 90,
					ticks : parsedResponse.hotAssetTicks
				}

			};

			$.plot($("#placeholder2"), data2, options2);

		},
		error : function(response) {
			alert('Error occured at statistics graph rendering');
		}
	});
}
