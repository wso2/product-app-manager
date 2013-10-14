var sugyamaModule = function () {

    var data={"class" : "org.wso2.jaggery.scxml.aspects.JaggeryTravellingPermissionLifeCycle", "name" : "MobileAppLifeCycle", "configuration" : [{"type" : "literal", "lifecycle" : [{"scxml" : [{"initialstate" : "Initial", "version" : "1.0", "xmlns" : "http://www.w3.org/2005/07/scxml", "state" : [{"id" : "Initial", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Create", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Created", "value" : "private_{asset_author}:+add,+delete,+authorize"}, {"name" : "STATE_RULE2:Created", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Create", "target" : "Created"}]}, {"id" : "Created", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Submit", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:In-Review", "value" : "private_{asset_author}:-add,-delete,-authorize"}, {"name" : "STATE_RULE2:In-Review", "value" : "reviewer:+add,-delete,+authorize"}, {"name" : "STATE_RULE3:In-Review", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Submit", "target" : "In-Review"}]}, {"id" : "In-Review", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Approve", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Approved", "value" : "private_{asset_author}:+get,-add,-delete,-authorize"}, {"name" : "STATE_RULE2:Approved", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Approved", "value" : "Internal/everyone:-add,-delete,-authorize"}]}, {"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Reject", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Rejected", "value" : "private_{asset_author}:+add,+delete,+authorize"}, {"name" : "STATE_RULE2:Rejected", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Rejected", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Approve", "target" : "Approved"}, {"event" : "Reject", "target" : "Rejected"}]}, {"id" : "Approved", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Publish", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Published", "value" : "private_{asset_author}:-add,+delete,-authorize"}, {"name" : "STATE_RULE2:Published", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Published", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Publish", "target" : "Published"}]}, {"id" : "Rejected", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Submit", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:In-Review", "value" : "private_{asset_author}:-add,-delete,-authorize"}, {"name" : "STATE_RULE2:In-Review", "value" : "reviewer:+add,-delete,+authorize"}, {"name" : "STATE_RULE3:In-Review", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Re-Submit", "target" : "In-Review"}]}, {"id" : "Published", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Unpublish", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Unpublished", "value" : "private_{asset_author}:-add,-delete,-authorize"}, {"name" : "STATE_RULE2:Unpublished", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Unpublished", "value" : "Internal/everyone:-add,-delete,-authorize"}]}, {"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Deprecate", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Deprecated", "value" : "private_{asset_author}:-add,-delete,-authorize"}, {"name" : "STATE_RULE2:Deprecated", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Deprecated", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Unpublish", "target" : "Unpublished"}, {"event" : "Deprecate", "target" : "Deprecated"}]}, {"id" : "Unpublished", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Publish", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Published", "value" : "private_{asset_author}:-add,+delete,-authorize"}, {"name" : "STATE_RULE2:Published", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Published", "value" : "Internal/everyone:-add,-delete,-authorize"}]}, {"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Retire", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Retired", "value" : "private_{asset_author}:-add,+delete,-authorize"}, {"name" : "STATE_RULE2:Retired", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Retired", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Publish", "target" : "Published"}, {"event" : "Retire", "target" : "Retired"}]}, {"id" : "Deprecated", "datamodel" : [{"data" : [{"name" : "transitionExecution", "execution" : [{"class" : "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent" : "Retire", "parameter" : [{"name" : "PERMISSION:get", "value" : "http://www.wso2.org/projects/registry/actions/get"}, {"name" : "PERMISSION:add", "value" : "http://www.wso2.org/projects/registry/actions/add"}, {"name" : "PERMISSION:delete", "value" : "http://www.wso2.org/projects/registry/actions/delete"}, {"name" : "PERMISSION:authorize", "value" : "authorize"}, {"name" : "STATE_RULE1:Retired", "value" : "private_{asset_author}:-add,+delete,-authorize"}, {"name" : "STATE_RULE2:Retired", "value" : "reviewer:-add,-delete,-authorize"}, {"name" : "STATE_RULE3:Retired", "value" : "Internal/everyone:-add,-delete,-authorize"}]}]}]}], "transition" : [{"event" : "Retire", "target" : "Retired"}]}, {"id" : "Retired"}]}]}]}]};
   /*var data = {"class": "org.wso2.jaggery.scxml.aspects.JaggeryTravellingPermissionLifeCycle", "name": "SampleLifeCycle2", "configuration": [
        {"type": "literal", "lifecycle": [
            {"scxml": [
                {"initialstate": "Initial", "version": "1.0", "xmlns": "http://www.w3.org/2005/07/scxml", "state": [
                    {"id": "Initial", "datamodel": [
                        {"data": [
                            {"name": "transitionExecution", "execution": [
                                {"class": "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent": "Promote", "parameter": [
                                    {"name": "PERMISSION:get", "value": "http://www.wso2.org/projects/registry/actions/get"},
                                    {"name": "PERMISSION:add", "value": "http://www.wso2.org/projects/registry/actions/add"},
                                    {"name": "PERMISSION:delete", "value": "http://www.wso2.org/projects/registry/actions/delete"},
                                    {"name": "PERMISSION:authorize", "value": "authorize"},
                                    {"name": "STATE_RULE1:Created", "value": "Internal/private_{asset_author}:+get,+add,+delete,+authorize"},
                                    {"name": "STATE_RULE2:Created", "value": "Internal/reviewer:+get,-add,-delete,-authorize"},
                                    {"name": "STATE_RULE3:Created", "value": "Internal/everyone:+get,-add,-delete,-authorize"}
                                ]}
                            ]}
                        ]}
                    ], "transition": [
                        {"event": "Promote", "target": "Created"}
                    ]},
                    {"id": "Created", "datamodel": [
                        {"data": [
                            {"name": "transitionExecution", "execution": [
                                {"class": "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent": "Promote", "parameter": [
                                    {"name": "PERMISSION:get", "value": "http://www.wso2.org/projects/registry/actions/get"},
                                    {"name": "PERMISSION:add", "value": "http://www.wso2.org/projects/registry/actions/add"},
                                    {"name": "PERMISSION:delete", "value": "http://www.wso2.org/projects/registry/actions/delete"},
                                    {"name": "PERMISSION:authorize", "value": "authorize"},
                                    {"name": "STATE_RULE1:In-Review", "value": "Internal/private_{asset_author}:+get,-add,-delete,-authorize"},
                                    {"name": "STATE_RULE2:In-Review", "value": "Internal/reviewer:+get,+add,-delete,+authorize"},
                                    {"name": "STATE_RULE3:In-Review", "value": "Internal/everyone:+get,-add,-delete,-authorize"}
                                ]}
                            ]}
                        ]}
                    ], "transition": [
                        {"event": "Promote", "target": "In-Review"}
                    ]},
                    {"id": "In-Review", "datamodel": [
                        {"data": [
                            {"name": "transitionExecution", "execution": [
                                {"class": "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent": "Promote", "parameter": [
                                    {"name": "PERMISSION:get", "value": "http://www.wso2.org/projects/registry/actions/get"},
                                    {"name": "PERMISSION:add", "value": "http://www.wso2.org/projects/registry/actions/add"},
                                    {"name": "PERMISSION:delete", "value": "http://www.wso2.org/projects/registry/actions/delete"},
                                    {"name": "PERMISSION:authorize", "value": "authorize"},
                                    {"name": "STATE_RULE1:Published", "value": "Internal/private_{asset_author}:+get,+add,-delete,+authorize"},
                                    {"name": "STATE_RULE2:Published", "value": "Internal/reviewer:+get,-add,-delete,-authorize"},
                                    {"name": "STATE_RULE3:Published", "value": "Internal/everyone:+get,-add,-delete,-authorize"}
                                ]}
                            ]}
                        ]}
                    ], "transition": [
                        {"event": "Promote", "target": "Published"}
                    ]},
                    {"id": "Published", "datamodel": [
                        {"data": [
                            {"name": "transitionExecution", "execution": [
                                {"class": "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent": "Demote", "parameter": [
                                    {"name": "PERMISSION:get", "value": "http://www.wso2.org/projects/registry/actions/get"},
                                    {"name": "PERMISSION:add", "value": "http://www.wso2.org/projects/registry/actions/add"},
                                    {"name": "PERMISSION:delete", "value": "http://www.wso2.org/projects/registry/actions/delete"},
                                    {"name": "PERMISSION:authorize", "value": "authorize"},
                                    {"name": "STATE_RULE1:Unpublished", "value": "Internal/private_{asset_author}:+get,+add,+delete,+authorize"},
                                    {"name": "STATE_RULE2:Unpublished", "value": "Internal/reviewer:+get,-add,-delete,-authorize"},
                                    {"name": "STATE_RULE3:Unpublished", "value": "Internal/everyone:+get,-add,-delete,-authorize"}
                                ]}
                            ]}
                        ]}
                    ], "transition": [
                        {"event": "Demote", "target": "Unpublished"}
                    ]},
                    {"id": "Unpublished", "datamodel": [
                        {"data": [
                            {"name": "transitionExecution", "execution": [
                                {"class": "org.wso2.jaggery.scxml.generic.GenericExecutor", "forEvent": "Promote", "parameter": [
                                    {"name": "PERMISSION:get", "value": "http://www.wso2.org/projects/registry/actions/get"},
                                    {"name": "PERMISSION:add", "value": "http://www.wso2.org/projects/registry/actions/add"},
                                    {"name": "PERMISSION:delete", "value": "http://www.wso2.org/projects/registry/actions/delete"},
                                    {"name": "PERMISSION:authorize", "value": "authorize"},
                                    {"name": "STATE_RULE1:In-Review", "value": "Internal/private_{asset_author}:+get,-add,-delete,-authorize"},
                                    {"name": "STATE_RULE2:In-Review", "value": "Internal/reviewer:+get,+add,-delete,+authorize"},
                                    {"name": "STATE_RULE3:In-Review", "value": "Internal/everyone:+get,-add,-delete,-authorize"}
                                ]}
                            ]}
                        ]}
                    ], "transition": [
                        {"event": "Promote", "target": "In-Review"}
                    ]}
                ]}
            ]}
        ]}
    ]};*/

    function Vertex() {
        this.id = '';
        this.x = 0;
        this.y = 0;
    }

    function Edge() {
        this.a = null;
        this.b = null;
    }

    function Matrix(map) {
        this.matrix = map || {};
    }

    function Level(index) {
        this.index = index;
        this.vertices = [];
    }

    function LevelMap() {
        this.map = {};
    }

    /*
     The function adds a given a vertex to a provided depth
     */
    LevelMap.prototype.add = function (vertex, depth) {

        //Create a new depth for it
        if (!this.map.hasOwnProperty(['l' + depth])) {
            this.map['l' + depth] = [];
        }

        this.map['l' + depth].push(vertex);


        return this.map;
    }

    LevelMap.prototype.get = function (level) {

        if (this.map.hasOwnProperty('l' + level)) {
            return this.map['l' + level];
        }

        return [];
    }

    LevelMap.prototype.count=function(){
        var counter=0;

        for(var key in this.map){
            counter++;
        }

        return counter;
    }

    LevelMap.prototype.show = function () {
        for (var key in this.map) {
            console.log(key + ' ' + JSON.stringify(this.map[key]));
        }
    };

    function createCoordinateMap(levelMap, level, length, startX, startY) {
        //Get the residents for the level
        var residents = levelMap.get(level);
        var mapping = [];

        //Calculate the spacing
        var spacing = length / residents.length;

        for (var index in residents) {
            mapping.push({label: residents[index], x: startX, y: startY});
            startY += spacing;
        }

        return mapping;
    }

    var START_X=20;
    var START_Y=500;
    var LEVEL_SPACE=200;
    var VERTEX_RADIUS=10;
    var LEVEL_SEP=100;


    function getPoints(levelMap) {
        var points = [];
        var startX = START_X;
        var startY = START_Y;

        for (var index = levelMap.count(); index >= 0; index--) {

            var coords = createCoordinateMap(levelMap, index, LEVEL_SPACE, startX, startY);

            //Draw the items in the coords
            for (var key in coords) {
                var element = coords[key];
                points.push(element);
            }

            startX += LEVEL_SEP;
        }

        return points;
    }

    function drawChart(levelMap, paper) {

        var points = [];
        var startX = START_X;
        var startY = START_Y;

        for (var index = levelMap.count(); index >= 0; index--) {
            var coords = createCoordinateMap(levelMap, index, LEVEL_SPACE, startX, startY);


            //Draw the items in the coords
            for (var key in coords) {
                var element = coords[key];
                var circle = paper.circle(element.x, element.y, VERTEX_RADIUS);
                circle.attr('fill', '#6EC87F');
                circle.node.setAttribute('class', element.label);
                var txt = paper.text(element.x, element.y+VERTEX_RADIUS+10, element.label);
                points.push(element);
            }

            startX += LEVEL_SEP;
        }

        return points;
    }

    function drawEdges(matrix, paper, points) {
        var matrix = matrix.matrix;
        for (var index in matrix) {

            //Find the point of the matrix
            var fromPoint = findPoint(index, points);

            var connected=getAllConnectedEdges(matrix,index);

            for (var to in connected) {

                if (fromPoint) {

                    var toPoint = findPoint(connected[to], points);

                    if (toPoint) {
                        var path = [];

                        path.push('M')
                        path.push(fromPoint.x);
                        path.push(fromPoint.y);
                        path.push('L')
                        path.push(toPoint.x);
                        path.push(toPoint.y);
                        paper.path(path.join(','));

                        var angle = Math.atan2(toPoint.y - fromPoint.y, toPoint.x - fromPoint.x);

                        angle = angle * (180 / Math.PI);

                        drawArrow({x: (toPoint.x +fromPoint.x)/2, y: (toPoint.y+fromPoint.y)/2, angle: angle}, paper);
                    }
                }
            }
        }
    }

    function findPoint(label, points) {
        for (var index in points) {
            var point = points[index];

            if (point.label == label) {
                return point;
            }
        }

        return null;
    }

    /*
     The function creates a path object for an arrow at point
     */
    function drawArrow(options, paper) {
        var x = options.x;
        var y = options.y;
        var width = options.width || 4;
        var height = options.height || 8;
        var angle = options.angle || null;
        var attributes = options.attributes || {fill: 'black'};
        var rotateX = x;
        var rotateY = y;


        var path = [];
        path.push('M');
        path.push(x);
        path.push(y);
        path.push('L');
        path.push(x - height);
        path.push(y - width);
        path.push('L');
        path.push(x - height);
        path.push(y + width);
        path.push('L');
        path.push(x);
        path.push(y);

        var pathString = path.join(',');

        var pathInstance = paper.path(pathString);

        //Attempt to apply all the attributes
        for (var index in attributes) {
            pathInstance.attr(index, attributes[index]);
        }

        //Rotate the arrow by the provided angle
        if (angle) {
            pathInstance.rotate(angle, rotateX, rotateY);
        }

    }


    //var keys = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'];
    //var keys=['created','inreview','published','unpublished'];
    var keys = ['created', 'inreview', 'approve', 'published', 'depricate', 'unpublished', 'reject', 'delete', 'retired']

    /*
     The function creates a matrix with all cycles removed
     */
    function buildMatrix(master) {

        var matrix = master.matrix;


        for (var index in keys) {
            var item = keys[index];
            matrix[item] = {};
        }

        matrix['created']['inreview'] = 1;
        matrix['inreview']['approve'] = 1;
        matrix['inreview']['reject'] = 1;
        matrix['approve']['published'] = 1;
        matrix['published']['unpublished'] = 1;
        matrix['unpublished']['retired'] = 1;
        matrix['published']['depricate'] = 1;
        matrix['depricate']['retired'] = 1;
        matrix['reject']['delete'] = 1;
        matrix['retired']['delete'] = 1;

        /*matrix['created']['inreview']=1;
         matrix['inreview']['published']=1;
         matrix['published']['unpublished']=1;
         matrix['inreview']['unpublished']=1;*/


        /*  matrix['a']['b'] = 1;
         matrix['b']['c'] = 1;
         matrix['b']['i'] = 1;
         matrix['c']['d'] = 1;
         matrix['d']['e'] = 1;
         matrix['d']['f'] = 1;
         matrix['d']['g'] =1;
         matrix['f']['h']=1;
         //matrix['e']['d'] = 1;
         matrix['e']['g'] = 1;
         matrix['g']['h'] = 1;
         matrix['i']['h'] = 1; */

        /* matrix['a']['b']=1;
         matrix['b']['c']=1;
         matrix['c']['d']=1;
         matrix['b']['d']=1; */

        return matrix;
    }


    function findHighestSinks(master) {

        var matrix = master.matrix;

        var sinks = [];

        var edgeCounter = 0;
        var highest = 0;
        var highestVertex = '';

        //Go through each vertex
        for (var vertex in master.matrix) {

            edgeCounter = 0;

            for (var connected in keys) {
                var item = keys[connected];
                if (master.matrix[item][vertex]) {
                    edgeCounter++;
                }

            }

            if (edgeCounter > highest) {
                highest = edgeCounter;
                highestVertex = vertex;
            }

            if (edgeCounter > 1) {
                console.log(vertex + ' edge counter: ' + edgeCounter);
                sinks.push(vertex);
            }
        }

        console.log('sinks: ' + JSON.stringify(sinks));

        return highestVertex;
    }

    /*
     The function calculates the distance of the path from
     to
     */
    function calculatePath(matrix, from, to) {
        return recursivePath(0, from, to, matrix.matrix);
    }

    function getAllConnectedEdges(map, from) {
        var items = [];

        if (map.hasOwnProperty(from)) {

            for (var index in map[from]) {

                if(map[from][index]==1){
                    items.push(index);
                }

            }

            return items;
        }

        return items;
    }

    function recursivePath(length, from, to, matrix) {
        if ((matrix.hasOwnProperty(from)) && (matrix[from][to])) {
            return length;
        }
        else {
            length++;
            var result;
            var connections = getAllConnectedEdges(matrix, from);
            //Travel all posssible paths
            for (var key in connections) {

                result = recursivePath(length, connections[key], to, matrix);

                if (result > 0) {
                    return result;
                }
            }

            return -1;
        }
    }

    /*
     The function is used to layout the matrix
     */
    function layout(matrix, levels) {

        var sink = findHighestSinks(matrix);

        var connected = keys;

        for (var key in connected) {
            //Calculate the path distance for each connection
            var path = calculatePath(matrix, sink, connected[key]);

            if (path < 0) {
                path = calculatePath(matrix, connected[key], sink);
            }

            levels.add(connected[key], path + 1);
        }

        return levels;
    }

    function Sugyama(data,paper){
        this.paper=paper;
        this.data=data;
    }

    Sugyama.prototype.init=function(data,paper){
        this.paper=paper;
        this.data=data;
    }

    Sugyama.prototype.draw=function(startX,startY,vertexRadius,levelSpace,levelSep){
         START_X=startX;
         START_Y=startY;
        VERTEX_RADIUS=vertexRadius;
        LEVEL_SEP=levelSep;
        LEVEL_SPACE=levelSpace;

        var module = graphDataModule();

        var dataProvider = new module.DataProvider();

        dataProvider.prepareData(this.data);

        var matrix = new Matrix(dataProvider.map);
        keys=dataProvider.keys;

        var levels = new LevelMap();

        var instance = layout(matrix, levels);

        levels.show();

        var points = getPoints(levels);
        drawEdges(matrix, this.paper, points);
        drawChart(levels, this.paper);
    }

    return{
        Sugyama:Sugyama
    }

};

