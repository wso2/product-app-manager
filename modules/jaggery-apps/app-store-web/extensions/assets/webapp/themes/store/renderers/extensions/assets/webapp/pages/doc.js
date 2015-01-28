
var render = function (theme, data, meta, require) {
	
	var log = new Log();

    theme('2-column-right', {
        title: 'InlineDocument',
        header: [
            {
              partial:'header',
              context:data.header
            },
            {
                partial: 'navigation',
                context: {}
            }
        ] ,
        body:[
            {
                partial:'doc',
                context:data
            }
        ]
        
    });

};