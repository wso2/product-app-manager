var cache = false;

var engine = caramel.engine('handlebars', (function () {
    return {
        partials: function (Handlebars) {
            var theme = caramel.theme();
            var partials = function (file) {
                (function register(prefix, file) {
                    var i, length, name, files;
                    if (file.isDirectory()) {
                        files = file.listFiles();
                        length = files.length;
                        for (i = 0; i < length; i++) {
                            file = files[i];
                            register(prefix ? prefix + '.' + file.getName() : file.getName(), file);
                        }
                    } else {
                        name = file.getName();
                        if (name.substring(name.length - 4) !== '.hbs') {
                            return;
                        }
                        file.open('r');
                        Handlebars.registerPartial(prefix.substring(0, prefix.length - 4), file.readAll());
                        file.close();
                    }
                })('', file);
            };
            //TODO : we don't need to register all partials in the themes dir.
            //Rather register only not overridden partials
            partials(new File(theme.__proto__.resolve.call(theme, 'partials')));
            partials(new File(theme.resolve('partials')));

            Handlebars.registerHelper('form_render', function (fieldname , fields ) {                
                var field = {};
                for (index = 0; index < fields.length; ++index) {
                    if(fields[index].name == fieldname ){
                        field = fields[index];
                    }
                }                
 				var path = "/themes/appm/partials/form-field.hbs";
				var file = new File(path);
				file.open("r");
				var template = Handlebars.compile(file.readAll());
            	return template({ "field": field });
            });
        }
    }
}()));
