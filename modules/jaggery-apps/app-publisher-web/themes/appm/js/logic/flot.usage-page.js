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
var label;
	$("#placeholder51").bind("plotclick", function(event, pos, item) {

		if (item!=null){
		                var numbers =[]
                        var option
                        $("#placeholder51").parents('.widget').addClass('graph-maximized');
                        $('.backdrop').show();

                        var x = item.datapoint[0];

                         label = item.series.xaxis.ticks[x].label;

                        for ( var i = 0; i < parsedResponse.webapp_.length; i++) {
                            var count = 0;
                            var app ='';

                            if(label == (parsedResponse.webapp_[i][0]).replace(/\s+/g, '')){

                                for ( var j = 0; j < parsedResponse.webapp_[i][1].length; j++) {

                                    numbers.push(parsedResponse.webapp_[i][1][j][0])

                                }

                            }

                        }
                        var option ;

                        for (i=0;i<numbers.length;i++){
                        option += '<option value="'+ numbers[i] + '">' + numbers[i] + '</option>';
                        }
                        $('#items').html(option);
                         var e = document.getElementById("items");
                        var strUser = e.options[e.selectedIndex].value;
            drawPopupChart(parsedResponse,label,strUser);

        }
    });

    $('#items').change(function(){

        var e = document.getElementById("items");
    var strUser = e.options[e.selectedIndex].value;
    drawPopupChart(parsedResponse,label,strUser);

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
function drawPopupChart(parsedResponse,label,strUser){

            var webappPage = [];
            var webappPageCount = [];
            for ( var i = 0; i < parsedResponse.webapp_.length; i++) {

                var app ='';

                if(label == (parsedResponse.webapp_[i][0]).replace(/\s+/g, '')){

                var arr=[];


                    for ( var j = 0; j < parsedResponse.webapp_[i][1].length; j++) {

                        if(strUser == (parsedResponse.webapp_[i][1][j][0]).replace(/\s+/g, '')){

                            var newArr = [],found, x, y;

                            var maximumUsers = parsedResponse.webapp_[i][1][j][1].length;

                            var origLen = parsedResponse.webapp_[i][1][j][1].length,

                            maxrowspan = parsedResponse.webapp_[i][1][j][1].length;


                            for ( var k = 0; k < maximumUsers; k++) {

                                found = undefined;

                                for (y = 0; y < newArr.length; y++) {

                                    if (parsedResponse.webapp_[i][1][j][1][k][0] === newArr[y]) {
                                        found = true;

                                        break;
                                    }
                                }
                                if (!found) {
                                    newArr.push(parsedResponse.webapp_[i][1][j][1][k][0]);
                                }

                            }
                            for(var l = 0; l < newArr.length; l++){
                            var allcount=0;
                                for ( var k = 0; k < maximumUsers; k++) {
                                    if(newArr[l] == parsedResponse.webapp_[i][1][j][1][k][0]){


                                            allcount = Number(allcount)+Number(parsedResponse.webapp_[i][1][j][1][k][1]);
                                        }

                                }
                                webappPageCount.push([allcount,l]);
                            }



                        }

                    }


                }

            }

            for(p = 0; p < newArr.length; p++){
                webappPage.push([p,newArr[p]])
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

