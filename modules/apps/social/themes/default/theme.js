var engine = caramel.engine('handlebars', (function () {
    if (request.getParameter('debug')=='1') {
        return {
            render: function (data, meta) {

                response.addHeader("Content-Type", "application/json");
                print(stringify(data));
            }
        };
    }
}()));
