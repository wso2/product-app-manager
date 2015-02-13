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
alert(response)
	var parsedResponse = JSON.parse(response);


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

