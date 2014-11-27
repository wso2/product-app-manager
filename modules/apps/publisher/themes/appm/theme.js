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
            
            
            Handlebars.registerHelper('list', function(items,output) {
            	
            	data = output.data.fields;
            	var log =  new Log();
          	
            	
            	var provider;
            	var apiName;
            	var version;
            	for (index = 0; index < data.length; ++index) {
                    if(data[index].name == "overview_provider"){
                        provider = data[index].value;
                    }else if(data[index].name == "overview_name"){
                    	apiName = data[index].value;
                    }else if(data[index].name == "overview_version"){
                    	version = data[index].value;
                    }
                }
            	
            	
            	var out = "";
            	 for (index = 0; index < items.length; ++index) {

 			var docLastUpdated = items[index].docLastUpdated;
            		var date  = new Date(parseInt(docLastUpdated));
            		var row = '<tr id ='+ apiName +'-'+ items[index].docName.replace(/ /g,'__')+'><td>' + items[index].docName + '</td><td>' + items[index].docType +'</td><td>'+  date.toString() +'</td><td id="buttonTd">';
            		 
            		var source = "";
            		var urlPostfix;
            		var tenantDomain =output.cuser.tenantDomain ;
            		if(tenantDomain = "carbon.super"){
            			 urlPostfix='';
            		}else {
            			urlPostfix = tenantDomain;
            		}
            		
            		if(items[index].sourceType == "URL"){
            			source =  '<a href="' + items[index].sourceUrl+  urlPostfix + '" target="_blank" ><i class="icon-check"></i> View</a>';
            		}else if(items[index].sourceType == "FILE"){
            			source =  '<a href="' + items[index].filePath+  urlPostfix + '" target="_blank" download><i class="icon-check"></i> Open</a>';

            		}else if(items[index].sourceType == "INLINE"){
            			source = '<a href="javascript:editInlineContent(\'' + output.artifact.id + '\',\'' + output.shortName + '\',\''+ items[index].docName + '\',\'edit\'' + ',\''+ urlPostfix + '\')"><i class="icon-edit"></i>EditContent</a>';
            		}
            		
            		
            		 var update = '<a href="javascript:updateDocumentation(\'' + apiName + '-' + items[index].docName + '\',\'' + items[index].docName + '\',\'' + items[index].docType + '\',\'' + items[index].summary + '\',\'' + items[index].sourceType + '\',\'' +  items[index].sourceUrl + '\',\'' + items[index].filePath + '\',\'' + items[index].otherTypeName +  '\')"><i class="icon-retweet"></i> Update</a>';
         			
                     var remove =   '<a href="javascript:removeDocumentation(\'' + provider + '\',\'' + apiName + '\',\'' + version + '\',\'' +  items[index].docName + '\',\'' + items[index].docType + '\')"><i class="icon-trash"></i>  Delete</a>';
            		 var end = '</td></tr>';
                   
                   out = out + row + source + '|'  + update + '|'+ remove + end;
                 }  

            	return out;
            	  
           });



            Handlebars.registerHelper('compare', function (lvalue, rvalue, options) {

                if (arguments.length < 3)
                    throw new Error("Handlerbars Helper 'compare' needs 2 parameters");

                operator = options.hash.operator || "==";

                var operators = {
                    '==': function (l, r) {
                        return l == r;
                    },
                    '===': function (l, r) {
                        return l === r;
                    },
                    '!=': function (l, r) {
                        return l != r;
                    },
                    '<': function (l, r) {
                        return l < r;
                    },
                    '>': function (l, r) {
                        return l > r;
                    },
                    '<=': function (l, r) {
                        return l <= r;
                    },
                    '>=': function (l, r) {
                        return l >= r;
                    },
                    'typeof': function (l, r) {
                        return typeof l == r;
                    }
                }

                if (!operators[operator])
                    throw new Error("Handlerbars Helper 'compare' doesn't know the operator " + operator);

                var result = operators[operator](lvalue, rvalue);

                if (result) {
                    return options.fn(this);
                } else {
                    return options.inverse(this);
                }

            });


            Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {

                switch (operator) {
                    case '==':
                        return (v1 == v2) ? options.fn(this) : options.inverse(this);
                    case '!=':
                        return (v1 != v2) ? options.fn(this) : options.inverse(this);
                    case '===':
                        return (v1 === v2) ? options.fn(this) : options.inverse(this);
                    case '<':
                        return (v1 < v2) ? options.fn(this) : options.inverse(this);
                    case '<=':
                        return (v1 <= v2) ? options.fn(this) : options.inverse(this);
                    case '>':
                        return (v1 > v2) ? options.fn(this) : options.inverse(this);
                    case '>=':
                        return (v1 >= v2) ? options.fn(this) : options.inverse(this);
                    default:
                        return options.inverse(this);
                }
            });


        }
    }
}()));
