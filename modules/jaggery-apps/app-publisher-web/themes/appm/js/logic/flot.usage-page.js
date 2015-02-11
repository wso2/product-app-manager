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
      //  $(this).parents('.widget').toggleClass('widget-maximized');
        $('.backdrop').hide();
    });

    $('.btn-remove').on('click', function() {
        $(this).parents('.graph-maximized').removeClass('graph-maximized');
        $('.backdrop').hide();
    });

}

var drawAPIUsageByPage = function(response) {

	var parsedResponse = JSON.parse(response);


//	var parsedResponse ={"usage":[{"apiName":"Conference Booking(admin)","version":"1","userid":"admin","referer":"/conferencebooking/1","context":"/conferencebooking","count":9},{"apiName":"Leave Managment(admin)","version":"1","userid":"admin","referer":"/leavemanagement/1","context":"/leavemanagement","count":30},{"apiName":"Travel Booking(admin)","version":"1","userid":"admin","referer":"/travelbooking/1","context":"/travelbooking","count":29},{"apiName":"pizza(admin)","version":"1","userid":"test","referer":"/pizza/1/login.jsp","context":"/pizza","count":29},{"apiName":"sample(admin)","version":"1","userid":"admin","referer":"/sample/1/servlets","context":"/sample","count":10},{"apiName":"pizza(admin)","version":"1","userid":"admin","referer":"/pizza/1/login.jsp","context":"/pizza","count":2},{"apiName":"sample(admin)","version":"1","userid":"test","referer":"/sample/1","context":"/sample","count":6},{"apiName":"sample(admin)","version":"1","userid":"test","referer":"/sample/1/servlets","context":"/sample","count":13},{"apiName":"Hardware Repo(admin)","version":"1","userid":"admin","referer":"/hardwarerepo/1","context":"/hardwarerepo","count":18},{"apiName":"sample(admin)","version":"1","userid":"admin","referer":"/sample/1","context":"/sample","count":10},{"apiName":"Event Management(admin)","version":"1","userid":"admin","referer":"/eventmanagement/1","context":"/eventmanagement","count":11},{"apiName":"Net Usage Analyser(admin)","version":"1","userid":"admin","referer":"/netusage/1","context":"/netusage","count":30},{"apiName":"Sales Tracking Portal(admin)","version":"1","userid":"admin","referer":"/salestrackingportal/1","context":"/salestrackingportal","count":2},{"apiName":"Travel Claims(admin)","version":"1","userid":"admin","referer":"/travelclaims/1","context":"/travelclaims","count":42},{"apiName":"webappsample(admin)","version":"1","userid":"test","referer":"/webappsample/1","context":"/webappsample","count":27},{"apiName":"MyApp(test)","version":"1","userid":"test","referer":"/myapp/1","context":"/myapp","count":14}],
//	"webapp":[[0,"Conference Booking(admin)"],[1,"Leave Managment(admin)"],[2,"Travel Booking(admin)"],[3,"pizza(admin)"],[4,"sample(admin)"],[5,"Hardware Repo(admin)"],[6,"Event Management(admin)"],[7,"Net Usage Analyser(admin)"],[8,"Sales Tracking Portal(admin)"],[9,"Travel Claims(admin)"],[10,"webappsample(admin)"],[11,"MyApp(test)"],[12,"MyApp1(test)"],[13,"a1"],,[14,"a1"],,[15,"a1"],,[16,"a1"],[17,"a1"],,[18,"a1"],,[19,"a1"],,[20,"a1"]
//	,[21,"a1"],,[22,"a1"],,[23,"a1"],,[24,"a1"],[25,"a1"],,[26,"a1"],,[27,"a1"],,[28,"a1"],[29,"a1"],,[30,"a1"],,[31,"a1"],[32,"a1"],[33,"a1"],[34,"a1"],,[35,"a1"],,[36,"a1"]],
//	"totalPageCount":[[0,9],[1,30],[2,29],[3,31],[4,39],[5,18],[6,11],[7,30],[8,2],[9,42],[10,27],[11,14],[12,4],[13,9],[14,30],[15,29],[16,31],[17,39],[18,18],[19,11],[20,30],[21,2],[22,42],[23,27],[24,14],[25,4]],
//	"webappDeatails":[["Conference Booking(admin)",[[0,"/conferencebooking/1"]],[[9,0]]],["Leave Managment(admin)",[[0,"/leavemanagement/1"]],[[30,0]]],["Travel Booking(admin)",[[0,"/travelbooking/1"]],[[29,0]]],["pizza(admin)",[[0,"/pizza/1/login.jsp"]],[[31,0]]],["sample(admin)",[[0,"/sample/1/servlets"],[1,"/sample/1"]],[[23,0],[16,1]]],["Hardware Repo(admin)",[[0,"/hardwarerepo/1"]],[[18,0]]],["Event Management(admin)",[[0,"/eventmanagement/1"]],[[11,0]]],["Net Usage Analyser(admin)",[[0,"/netusage/1"]],[[30,0]]],["Sales Tracking Portal(admin)",[[0,"/salestrackingportal/1"]],[[2,0]]],["Travel Claims(admin)",[[0,"/travelclaims/1"]],[[42,0]]],["webappsample(admin)",[[0,"/webappsample/1"]],[[27,0]]],["MyApp(test)",[[0,"/myapp/1"]],[[14,0]]]]}

//	$('#placeholder5').append(
//			$('<table class="table graphTable" id="webAppTable5">' + '<tr>'
//					+ '<th>Web App</th>' + '<th>Number of Access</th>'
//					+ '</tr>' + '</table>'));
//	for ( var i = 0; i < parsedResponse.webapp.length; i++) {
//		$('#webAppTable5').append(
//				$('<tr><td>' + parsedResponse.webapp[i][1]
//						+ '</td><td class="NumberCell">'
//						+ parsedResponse.totalPageCount[i][1] + '</td><tr>'));
//	}

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

	$("#placeholder51").bind("plotclick", function(event, pos, item) {
		if (item!=null){
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
				var dataset = [ {
					data : data,
					color : "#5482FF"
				} ];
				var ticks = webappPage;

				var options = {
					series : {
						bars : {
							show : true,
							horizontal : true,

						}
					},
					bars : {
						align : "center",
						barWidth : 0.5
					},
					xaxis : {
		                axisLabelUseCanvas :false,
		                axisLabel : "<b>Total Request Count</b>",
						reserveSpace : true,
						labelWidth : 150,
						min : 0,
						max : max
					},
					yaxis : {
						ticks : ticks,
						tickLength : 0,
						axisLabel : "Accessed Page",

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

