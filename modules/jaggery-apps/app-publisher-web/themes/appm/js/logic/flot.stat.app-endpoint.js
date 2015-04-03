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
    var userParsedResponse;

    $.ajax({
        async: false,
        url: '/publisher/api/assets/' + operation + '/' + type
            + '/getAppsByEndPoint/',
        type: 'POST',
        data: {
            'startDate': from,
            'endDate': to
        },

        success: function (response) {

//            drawSubscribedAPIsByUsers('[["wso1", "1.0.0", "http://localhost:8280/wso1/1.0.0"],' +
//                '["wso2", "1.0.0", "http://localhost:8280/wso2/1.0.0"],' +
//                '["wso3", "1.0.0", "http://localhost:8280/wso3/1.0.0"]]', usageByContext);
            drawSubscribedAPIsByUsers(response, usageByContext);
            $('#spinner').hide();
        },
        error: function (response) {

        }
    });


    $('.btn-remove').on('click', function () {
        $(this).parents('.graph-maximized').removeClass('graph-maximized');
        $('.backdrop').hide();
    });
}


var drawSubscribedAPIsByUsers = function (response, usageByContext) {

    var parsedResponse = JSON.parse(response);

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
    var states = [];
    for (var i = 0; i < parsedResponse.length; i++) {
        states.push(parsedResponse[i][0]);
    }

    $('input.type-ahead').typeahead({
        hint: true,
        highlight: true,
        minLength: 1
    }, {
        name: 'states',
        displayKey: 'value',
        source: substringMatcher(states)
    });
    $('.type-ahead.input-lg').siblings('input.tt-hint').addClass('hint-large');

    tableStatement = '';
    rawStatement = '';
    /* Overall Web Application Usage Graph */
    $("#target").click(function () {
        getWebAppUsage();
    });

    /**
     * Get Web Application Usage
     */
    function getWebAppUsage() {
        $('div#webAppTable2_wrapper.dataTables_wrapper.no-footer').remove();

        var $dataTable = $('<table class="display" width="100%" cellspacing="0" id="webAppTable2" ></table>');
        $dataTable.append($('<thead class="tableHead"><tr >'
            + '<th id ="webApp">Web App</th>'
            + '<th width="10%">App Version</th>'
            + '<th width="40%">End Point</th>'
            + '<th >Statistics</th>'
            + '</tr></thead>'));


        var detailNumber = 0;
        var rawNumber = 0;
        for (var i = 0; i < parsedResponse.length; i++) {
            for (var j = 0; j < 1; j++) {
                $dataTable.append(
                    $('<tr id=' + rawNumber + '><td id="appName">' + parsedResponse[i][0] +
                        '</td><td id="appVersion">' + parsedResponse[i][1]
                        + '</td><td>' + parsedResponse[i][2] +
                        '</td><td><a  href="/publisher/assets/apps/webapp/" id=' + detailNumber + '>Show Statistics</a>'
                        + '</td></tr>'));
                detailNumber++;
                rawNumber++;
            }
        }

        if (parsedResponse.length == 0) {

            $('#webAppTable2').hide();
            $('#placeholder2').html('<h1 class="no-data-heading">No data available</h1>');

        } else {
            $('#placeholder2').append($dataTable);
            $('#flot-placeholder').append($('<div id="lineWithFocusChart"><svg style="height:450px;"></svg></div>'));
            $('#placeholder2').show();
            $('#webAppTable2').dataTable();
        }
    }

    //init deafult
    getWebAppUsage();

    //ajax call to get hits
    $("#placeholder2").on("click", ".trigger-ajax", function () {

        $(this).parents('.widget').addClass('graph-maximized');
        $('.backdrop').show();
        $('.widget-head').show();
        $('#placeholder2').hide();
        $('#searchUserForm').hide();

        $('.btn-remove').on('click', function () {
            $('.widget-head').hide();
            $('#placeholder2').show();
            $('#searchUserForm').show();
            var svg = d3.select("svg");
            svg.selectAll("*").remove();
        })

        var answerid = $(this).attr('id');
        var test = $(this).closest('tr').attr('id');

        function getCell(column, row) {
            var column = $('#' + column).index();
            var row = $('#' + row)
            return row.find('td').eq(column);
        }

    });


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

}
