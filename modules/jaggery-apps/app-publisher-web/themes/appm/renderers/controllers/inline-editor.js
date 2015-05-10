var render=function(theme,data,meta,require){
	

    var log = new Log();
  
   	var listPartial='inline-editor';

	theme('single-col-fluid', {
        title: data.title,
     	header: [
            {
                partial: 'header',
                context: data
            }
        ],
        ribbon: [
                 {
                     partial: 'ribbon',
     		        context:require('/helpers/breadcrumb.js').generateBreadcrumbJson(data)
                 }
        ],
        listassets: [
            {
                partial:listPartial,
		        context: data
            }
        ]
    });
};