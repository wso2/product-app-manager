$(function () {

    var url = window.location.pathname;

    //Break the url into components
    var comps = url.split('/');

    var type = comps[comps.length - 2];
    var operation = comps[comps.length - 3];

    $(document).ready(function () {
        $.ajax({
            url: '/publisher/api/assets/' + operation + '/' + type + '/',
            type: 'GET',
            success: function (response) {
                var parsedResponse = JSON.parse(response);
                $.plot("#placeholder", [parsedResponse.stats], {
                    series: {
                        lines: { show: true },
                        points: { show: true }
                    },
                    xaxis: {
                        show: true,
                        ticks: parsedResponse.ticks

                    },
                    yaxis: {
                        show: true,
                        tickDecimals: 0

                    },
                    grid: {
                        backgroundColor: { colors: [ "#fff", "#eee" ] },
                        borderWidth: {
                            top: 1,
                            right: 1,
                            bottom: 2,
                            left: 2
                        }
                    }
                });

            },
            error: function (response) {
                alert('Error occured at statistics graph rendering');
            }
        });
    });


});


