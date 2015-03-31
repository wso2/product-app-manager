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

            drawSubscriberCountByAPIs(response);
            $('#spinner').hide();

        },
        error: function (response) {
            alert('Error occured at statistics graph rendering');
        }
    });
}

var drawSubscriberCountByAPIs = function (response) {

    $('#pie-chart').empty();

    var parsedResponse = JSON.parse(response);


    if (parsedResponse.length > 0) {
        var chartColorScheme = [];
        var colorRangeArray = [];

        function getRandomColor() {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';

            for (var i = 0; i < 6; i++) {
                color += letters[Math.floor(Math.random() * 16)];
            }

            return color;
        }

        function hexToRgb(hex) {
            var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
            return result ? {
                r: parseInt(result[1], 16),
                g: parseInt(result[2], 16),
                b: parseInt(result[3], 16)
            } : null;
        }


        var totalCount = 0;
        for (var i = 0; i < parsedResponse.length; i++) {
            for (var j = 0; j < parsedResponse[i][1].length; j++) {
                totalCount += Number(parsedResponse[i][1][j][1]);
            }
        }

        for (var i = 0; i < parsedResponse.length; i++) {

            var dataStructure = [];
            var otherCount = 0;

            for (var j = 0; j < parsedResponse[i][1].length; j++) {
                var user = parsedResponse[i][1][j][0];
                otherCount += Number(parsedResponse[i][1][j][1]);
                dataStructure.push({
                    "itemLabel": "V " + parsedResponse[i][1][j][0],
                    "itemValue": Number(parsedResponse[i][1][j][1])

                });
            }
            var randomColor = getRandomColor();
            var rgb = hexToRgb(randomColor);

            var r = rgb.r;
            var g = rgb.g;
            var b = rgb.b;

            if (r < 210 && g < 210 && b < 210) {
                for (var j = 0; j <= parsedResponse[i][1].length; j++) {
                    r -= 10;
                    g -= 10;
                    b -= 10;

                    chartColorScheme.push("rgb(" + r + "," + g + "," + b + ")");
                }
            } else {
                for (var j = 0; j <= parsedResponse[i][1].length; j++) {
                    r -= 13;
                    g -= 13;
                    b -= 13;

                    chartColorScheme.push("rgb(" + r + "," + g + "," + b + ")");
                }
            }

            chartColorScheme.splice(parsedResponse[i][1].length, 0, "#eee");
            colorRangeArray.push(chartColorScheme);

            dataStructure.push({
                "itemLabel": "other",
                "itemValue": totalCount - otherCount,
            });

            var data;
            var div = d3.select("body").append("div").attr("class", "toolTip");
            var w = 250;
            var h = 260;
            var r = 60;
            var ir = 35;
            var textOffset = 24;
            var tweenDuration = 1050;

            //OBJECTS TO BE POPULATED WITH DATA LATER
            var lines, valueLabels, nameLabels;
            var pieData = [];
            var oldPieData = [];
            var filteredPieData = [];

            //D3 helper function to populate pie slice parameters from array data
            var donut = d3.layout.pie().value(function (d) {
                return d.itemValue;
            });

            //D3 helper function to create colors from an ordinal scale
            // var color = d3.scale.category20c();
            var color = d3.scale.ordinal()
                .range(colorRangeArray[i]);

            chartColorScheme = [];
            //D3 helper function to draw arcs, populates parameter "d" in path object
            var arc = d3.svg.arc()
                .startAngle(function (d) {
                    return d.startAngle;
                })
                .endAngle(function (d) {
                    return d.endAngle;
                })
                .innerRadius(ir)
                .outerRadius(r);

            // CREATE VIS & GROUPS
            var vis = d3.select("#pie-chart").append("svg:svg")
                .attr("width", w)
                .attr("height", h-40);
            vis.append("text").attr("class", "title_text")
                .attr("x", 125)
                .attr("y", 17)
                .style("font-size", "14px").style("font-weight", "10px")

                .style("font-family", "'Helvetica Neue',Helvetica,Arial,sans-serif")
                .style("z-index", "19")
                .style("text-anchor", "middle")
                .style("color", "gray")
                .text('\n' + parsedResponse[i][0]);


            //GROUP FOR ARCS/PATHS
            var arc_group = vis.append("svg:g")
                .attr("class", "arc")
                .attr("transform", "translate(" + (w / 2) + "," + (h / 2) + ")");

            //GROUP FOR LABELS
            var label_group = vis.append("svg:g")
                .attr("class", "label_group")
                .attr("transform", "translate(" + (w / 2) + "," + (h / 2) + ")");

            //GROUP FOR CENTER TEXT
            var center_group = vis.append("svg:g")
                .attr("class", "center_group")
                .attr("transform", "translate(" + (w / 2) + "," + (h / 2) + ")");

            //WHITE CIRCLE BEHIND LABELS
            var whiteCircle = center_group.append("svg:circle")
                .attr("fill", "white")
                .attr("r", ir);


            // to run each time data is generated
            function update() {

                data = dataStructure;
                oldPieData = filteredPieData;
                pieData = donut(data);

                var sliceProportion = 0; //size of this slice
                filteredPieData = pieData.filter(filterData);
                function filterData(element, index, array) {
                    element.name = data[index].itemLabel;
                    element.value = data[index].itemValue;
                    sliceProportion = totalCount;
                    return (element.value > 0);
                }

                //DRAW ARC PATHS
                paths = arc_group.selectAll("path").data(filteredPieData);
                paths.enter().append("svg:path")
                    .attr("stroke", "white")
                    .attr("stroke-width", 0.5)
                    .attr("fill", function (d, i) {
                        return color(i);
                    })
                    .attr("cursor", function (d) {
                        if (d.name == "other") {
                            return "default";
                        } else {
                            return "pointer";
                        }
                    })
                    .attr("pointer-events", function (d) {
                        if (d.name == "other") {
                            return "none";
                        }
                    })
                    .transition()
                    .duration(tweenDuration)
                    .attrTween("d", pieTween);
                paths
                    .transition()
                    .duration(tweenDuration)
                    .attrTween("d", pieTween);
                paths.exit()
                    .transition()
                    .duration(tweenDuration)
                    .attrTween("d", removePieTween)
                    .remove();

                paths.on("mousemove", function (d) {
                    var percentage = (d.value / sliceProportion) * 100;
                    div.style("left", d3.event.pageX + 10 + "px");
                    div.style("top", d3.event.pageY - 25 + "px");
                    div.style("display", "inline-block");
                    div.html((d.data.itemLabel) + "<br>" + ("Count : " + d.data.itemValue)+"<br>Percentage : "+percentage.toFixed(1) + "%");
                    if (d.data.version == "other") {
                        div.style("display", "none");
                    }
                });

                paths.on("mouseout", function (d) {
                    div.style("display", "none");
                });

                var val = i;
                paths.on("click", function (d) {
                    for (var j = 0; j < parsedResponse[val][1].length; j++) {

                        if ("V " + parsedResponse[val][1][j][0] == d.data.itemLabel) {

                            document.location.href = "/publisher/asset/webapp/" + parsedResponse[val][1][j][2];
                        }
                    }
                });

                //DRAW TICK MARK LINES FOR LABELS
                lines = label_group.selectAll("line").data(filteredPieData);
                lines.enter().append("svg:line")
                    .attr("x1", 0)
                    .attr("x2", 0)
                    .attr("y1", -r - 3)
                    .attr("y2", -r - 15)
                    .attr("stroke", "gray")
                    .attr("display", function (d) {
                        if (d.name == "other") {
                            return "none";
                        }
                    })
                    .attr("transform", function (d) {
                        return "rotate(" + (d.startAngle + d.endAngle) / 2 * (180 / Math.PI) + ")";
                    });
                lines.transition()
                    .duration(tweenDuration)
                    .attr("transform", function (d) {
                        return "rotate(" + (d.startAngle + d.endAngle) / 2 * (180 / Math.PI) + ")";
                    });
                lines.exit().remove();

                //DRAW LABELS WITH PERCENTAGE VALUES
                valueLabels = label_group.selectAll("text.value").data(filteredPieData)
                    .attr("dy", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
                            return 5;
                        } else {
                            return -7;
                        }
                    })
                    .attr("text-anchor", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
                            return "beginning";
                        } else {
                            return "end";
                        }
                    })
                    .text(function (d) {
                        var percentage = (d.value / sliceProportion) * 100;
                        return percentage.toFixed(1) + "%";
                    });

                valueLabels.enter().append("svg:text")
                    .attr("class", "value")
                    .attr("transform", function (d) {
                        return "translate(" + Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (r + textOffset) +
                            "," + Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (r + textOffset) + ")";
                    })
                    .attr("dy", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
                            return 5;
                        } else {
                            return -7;
                        }
                    })
                    .attr("text-anchor", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
                            return "beginning";
                        } else {
                            return "end";
                        }
                    });

                valueLabels.transition().duration(tweenDuration).attrTween("transform", textTween);

                valueLabels.exit().remove();


                //DRAW LABELS WITH ENTITY NAMES
                nameLabels = label_group.selectAll("text.units").data(filteredPieData)
                    .attr("dy", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
                            return 17;
                        } else {
                            return 5;
                        }
                    })
                    .attr("text-anchor", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
                            return "beginning";
                        } else {
                            return "end";
                        }
                    }).text(function (d) {
                        return d.name;
                    });

                nameLabels.enter().append("svg:text")
                    .attr("class", "units")
                    .attr("transform", function (d) {
                        return "translate(" + Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (r + textOffset) + "," +
                            Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (r + textOffset) + ")";
                    })
                    .attr("dy", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
                            return 17;
                        } else {
                            return 5;
                        }
                    })
                    .attr("text-anchor", function (d) {
                        if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
                            return "beginning";
                        } else {
                            return "end";
                        }
                    }).text(function (d) {
                        if (d.name == "other") {
                            return "";
                        } else {
                            return d.name;
                        }
                    });

                nameLabels.transition().duration(tweenDuration).attrTween("transform", textTween);

                nameLabels.exit().remove();

            }

            // Interpolate the arcs in data space.
            function pieTween(d, i) {
                var s0;
                var e0;
                if (oldPieData[i]) {
                    s0 = oldPieData[i].startAngle;
                    e0 = oldPieData[i].endAngle;
                } else if (!(oldPieData[i]) && oldPieData[i - 1]) {
                    s0 = oldPieData[i - 1].endAngle;
                    e0 = oldPieData[i - 1].endAngle;
                } else if (!(oldPieData[i - 1]) && oldPieData.length > 0) {
                    s0 = oldPieData[oldPieData.length - 1].endAngle;
                    e0 = oldPieData[oldPieData.length - 1].endAngle;
                } else {
                    s0 = 0;
                    e0 = 0;
                }
                var i = d3.interpolate({startAngle: s0, endAngle: e0}, {startAngle: d.startAngle, endAngle: d.endAngle});
                return function (t) {
                    var b = i(t);
                    return arc(b);
                };
            }

            function removePieTween(d, i) {
                s0 = 2 * Math.PI;
                e0 = 2 * Math.PI;
                var i = d3.interpolate({startAngle: d.startAngle, endAngle: d.endAngle}, {startAngle: s0, endAngle: e0});
                return function (t) {
                    var b = i(t);
                    return arc(b);
                };
            }

            function textTween(d, i) {
                var a;
                if (oldPieData[i]) {
                    a = (oldPieData[i].startAngle + oldPieData[i].endAngle - Math.PI) / 2;
                } else if (!(oldPieData[i]) && oldPieData[i - 1]) {
                    a = (oldPieData[i - 1].startAngle + oldPieData[i - 1].endAngle - Math.PI) / 2;
                } else if (!(oldPieData[i - 1]) && oldPieData.length > 0) {
                    a = (oldPieData[oldPieData.length - 1].startAngle + oldPieData[oldPieData.length - 1].endAngle - Math.PI) / 2;
                } else {
                    a = 0;
                }
                var b = (d.startAngle + d.endAngle - Math.PI) / 2;

                var fn = d3.interpolateNumber(a, b);
                return function (t) {
                    var val = fn(t);
                    return "translate(" + Math.cos(val) * (r + textOffset) + "," + Math.sin(val) * (r + textOffset) + ")";
                };
            }

            update();
        }
        if (parsedResponse.length != 0) {
            var items = $("#pie-chart svg");
            var numItems = items.length;
            var perPage = 6;
            // only show the first 2 (or "first per_page") items initially
            items.slice(perPage).hide();
            // now setup pagination
            $("#pagination").pagination({
                items: numItems,
                itemsOnPage: perPage,
                cssStyle: "",
                onPageClick: function (pageNumber) { // this is where the magic happens
                    // someone changed page, lets hide/show trs appropriately
                    var showFrom = perPage * (pageNumber - 1);
                    var showTo = showFrom + perPage;
                    items.hide() // first hide everything, then show for the new page
                        .slice(showFrom, showTo).show();
                }
            });

        }

    } else {
        $('#pie-chart').html($('<h1 class="no-data-heading">No data available</h1>'));
        $('#pagination').empty();
    }
}

var onDateSelected = function () {
    clearTables();
    $('#pie-chart').empty();
    $('#pagination').empty();
    drawGraphs();
}

function clearTables() {
    $('#webAppTable').remove();
    $('#webAppTable2').remove();
    $('#webAppTable3').remove();
    $('#webAppTable4').remove();
    $('#webAppTable5').remove();

}

