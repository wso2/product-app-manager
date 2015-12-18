$(function() {
	var url = window.location.pathname;

	var comps = url.split('/');
	var type = comps[comps.length - 2];
	var operation = comps[comps.length - 3];
	var action = 'getProviderAPIVersionUserLastAccess';

	var dateRange = $('#date-range-field span').text();
	var from = dateRange.split('to')[0];
	var to = dateRange.split('to')[1];

	$.ajax({
		/*  Web Application Last Access Time Graph  */
		url : caramel.context + '/api/assets/' + operation + '/' + type + '/' + action + '/' ,
		type : 'POST',
		data : {
			'startDate' : from,
			'endDate' : to 
		},
		success : function(response) {
			drawProviderAPIVersionUserLastAccess(response);	
		},
		error : function(response) {
			alert('Error occured at statistics graph rendering');
		}
	});

	$.ajax({
		/* Overall Web Application Usage Graph */
		url : caramel.context + '/api/assets/' + operation + '/' + type + '/getProviderAPIUsage/',
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
});

var drawProviderAPIVersionUserLastAccess = function(response){
	var parsedResponse = JSON.parse(response);
	/*  Web Application Last Access Time Graph  */
	$('#placeholder1').append($('<table class="table table-condensed" id="webAppTable">' +
					'<tr>' +
    					'<th>Web App Name</th>' +
    					'<th>Version</th>' +
					'<th>User</th>' +
					'<th>Last Access Time</th>' +
					'</tr>' +
					'</table>'));
        for (var i = 0; i < parsedResponse.length; i++) {
            $('#webAppTable').append($('<tr><td>' + parsedResponse[i].api_name + '</td><td class="tdNumberCell">' + parsedResponse[i].api_version + '</td><td>' + parsedResponse[i].user + '</td><td>' + new Date(parsedResponse[i].lastAccess*1000) + '</td></tr>'));

        } 
}

var drawProviderAPIUsage = function(response){
	var parsedResponse = JSON.parse(response);
	/* Overall Web Application Usage Graph */
	$('#placeholder2').append($('<table class="table table-condensed" id="webAppTable2">' +
					'<tr>' +
    					'<th>Web App Name</th>' +
    					'<th>Number of Calls</th>' +
					'</tr>' +
					'</table>'));
        for (var i = 0; i < parsedResponse.length; i++) {
            $('#webAppTable2').append($('<tr><td>' + parsedResponse[i].apiName + '</td><td class="tdNumberCell">' + parsedResponse[i].count + '</td></tr>'));
        } 
}

var convertDate = function(date) {
	var month = date.getMonth() + 1;
	var day = date.getDate();
	return date.getFullYear() + '-' + (('' + month).length < 2 ? '0' : '') + month + '-' + (('' + day).length < 2 ? '0' : '') + day;
}
var onDateSelected = function(from, to) {
	var url = window.location.pathname;
	var comps = url.split('/');

	var type = comps[comps.length - 2];
	var operation = comps[comps.length - 3];
	$.ajax({
		url : caramel.context + '/api/assets/' + operation + '/' + type + '/',
		type : 'POST',
		data : {
			'startDate' : from,
			'endDate' : to,
			'isOnChoice' : true
		},
		success : function(response) {
			var parsedResponse = JSON.parse(response);
			
			
			/* Hot assets stats graph */
			var data2 = [{
				data : parsedResponse.hotAssetStats,
				color : '#FFC826',
				label : 'Assets',
				bars : {
						show : true,
						barWidth : 0.6,
						align : "center"
					}
			}];

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

