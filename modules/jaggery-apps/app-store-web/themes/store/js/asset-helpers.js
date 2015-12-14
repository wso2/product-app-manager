var renderAssets, mouseStop, renderAssetsScroll;

(function () {
    renderAssets = function (data) {
        var el = $('.store-left');
        caramel.css($('head'), data.body['sort-assets'].resources.css, 'sort-assets');
        caramel.code($('head'), data.body['assets'].resources.code);
        caramel.partials(data._.partials, function () {
            var assets = Handlebars.partials['assets'](data.body.assets.context),
                sort = Handlebars.partials['sort-assets'](data.body['sort-assets'].context);
            theme.loaded(el, sort);
            el.append(assets);
            caramel.js($('body'), data.body['assets'].resources.js, 'assets', function () {
                renderAssetsForPagination(data);
                renderPageIndices(data)
            });
            caramel.js($('body'), data.body['sort-assets'].resources.js, 'sort-assets', function () {
                updateSortUI();
            });
            $(document).scrollTop(0);

            infiniteScroll = data.body.assets.context.assets.length >= 12;
        });
    };

    renderAssetsForPagination = function(data){
        var temp = '{{#slice assets size="4"}}<div class="row-fluid">';
        temp += '{{#each .}}';
        temp += '<div class="span3 asset" data-id="{{id}}" data-path="{{path}}" data-type="{{type}}">';
        temp += '   {{#attributes}}';
        temp += '       {{#compare  overview_advertiseOnly "true" operator="=="}}';
        temp += '           <div class="ribbon-wrapper-orange"><div class="ribbon-orange">Advertised</div></div>';
        temp += '       {{/compare}}';
        temp += '		    {{#if ../isSubscribed}}';
        temp += '               <div class="ribbon-wrapper-green"><div class="ribbon-green">Subscribed</div></div>';
        temp += '			{{/if}}';
        temp += '       {{#compare  overview_advertiseOnly "true" operator="=="}}';
        temp += '           <a href="{{url "/t"}}/{{overview_appTenant}}/assets/{{../../type}}/{{overview_advertisedAppUuid}} " id="assetsLink">';
        temp += '       {{else}}';
        temp += '           <a href="{{tenantedUrl "/assets"}}/{{../../type}}/{{../../id}}" id="assetsLink">';
        temp += '       {{/compare}}';
        temp += '	        <div class="asset-icon">';
        temp += '               <input type="hidden"  value="{{overview_allowAnonymous}}" id="allowAnonymous" name="allowAnonymous">';
        temp += '	            {{#if ../indashboard}}';
        temp += '	            {{/if}}';
        temp += '	            <img src="{{#if images_thumbnail}}{{images_thumbnail}}{{/if}}">';
        temp += '	        </div>';
        temp += '			</a>';
        temp += '	    <div class="asset-details">';
        temp += '		<div class="asset-name">';
        temp += '		    <input type="hidden"  value="{{overview_allowAnonymous}}" id="allowAnonymous" name="allowAnonymous">';
        temp += '           {{#compare  overview_advertiseOnly "true" operator="=="}}';
        temp += '               <a href="{{url "/t"}}/{{overview_appTenant}}/assets/{{../../type}}/{{overview_advertisedAppUuid}} " id="assetDeatils"> <h4>{{overview_displayName}}</h4></a>';
        temp += '           {{else}}';
        temp += '               <a href="{{tenantedUrl "/assets"}}/{{../../type}}/{{../../id}}" id="assetDeatils"> <h4>{{overview_displayName}}</h4></a>';
        temp += '           {{/compare}}';
        temp += '		</div>';
        temp += '		<div class="asset-rating">';
        temp += '			<div class="asset-rating-{{../rating}}star">';
        temp += '			</div>';
        temp += '		</div>';
        temp += '		<div class="asset-author-category">';
        temp += '			<ul>';
        temp += '				<li>';
        temp += '					<h4>{{t "Version"}}</h4>';
        temp += '					<a class="asset-version" href="#">{{overview_version}}</a>';
        temp += '				</li>';
        temp += '				<li>';
        temp += '					<h4>{{t "Category"}}</h4>';
        temp += '					<a class="asset-category" href="#">{{cap ../type}}</a>';
        temp += '				</li>';
        temp += '				<li>';
        temp += '					<h4>{{t "Author"}}</h4>';
        temp += '					<a class="asset-author" href="#">{{overview_provider}}</a>';
        temp += '				</li>';
        temp += '			</ul>';
        temp += '			{{#if ../indashboard}}';
        temp += '			<a href="#" class="btn disabled btn-added">{{t "Bookmarked"}}</a>';
        temp += '			{{else}}';
        temp += '				{{# if ../../../../sso}}';
        temp += '				<a href="{{url "/login"}}" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
        temp += '				{{else}}';
        temp += '					<a href="#" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
        temp += '				{{/if}}';
        temp += '				{{# if ../../../../user.username}}';
        temp += '				<a href="#" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
        temp += '				{{/if}}';
        temp += '			{{/if}}';
        temp += '		</div>';
        temp += '	</div>';
        temp += '	{{/attributes}}';
        temp += '</div>';
        temp += '{{/each}}';
        temp += '</div>{{/slice}}';

        var assetsTemp = Handlebars.compile(temp);
        var render = assetsTemp(data.body.assets.context);
        $('#assets-container').html(render);

        caramel.js($('body'), data.body['assets'].resources.js, 'assets', function () {
//                mouseStop();
        });

    };

    renderPageIndices = function(data){
        var temp = '<ul class="pagination">';
        temp+= '{{#if leftNav}}';
        temp+= '    <li><a href="{{tenantedUrl "/assets/webapp?"}}{{this.urlQuery}}{{t "page="}}{{this.leftNav}}">';
        temp+= '    <span aria-hidden="true">&laquo;</span><span class="sr-only"></span></a></li>';
        temp+= '{{/if}}';
        temp+= '{{#each pageIndeces}}';
        temp+= '   {{#if this.isDisabled}}';
        temp+= '        <li class="disabled"><a>{{this.index}}</a></li>';
        temp+= '   {{else}}';
        temp+= '        <li><a href="{{tenantedUrl "/assets/webapp?"}}{{this.urlQuery}}{{t "page="}}{{this.index}}">';
        temp+= '   {{/if}}';
        temp+= '{{/each}}';
        temp+= '{{#if rightNav}}';
        temp+= '    <li><a href="{{tenantedUrl "/assets/webapp?"}}{{this.urlQuery}}{{t "page="}}{{this.rightNav}}">';
        temp+= '    <span aria-hidden="true">&raquo;</span><span class="sr-only"></span></a></li>';
        temp+= '{{/if}}';
        temp+= '</ul>';

        var assetsTemp = Handlebars.compile(temp);
        var render = assetsTemp(data.body.assets.context);
        $('#paging-indices').html(render);
        var state = History.getState();
        if (state.data.id === 'tags') {
            location.reload();
        }
    };

    renderAssetsScroll = function(data){
    	var temp = '{{#slice assets size="4"}}<div class="row-fluid">';
        	temp += '{{#each .}}';
			temp += '<div class="span3 asset" data-id="{{id}}" data-path="{{path}}" data-type="{{type}}">';
			temp += '	{{#attributes}}';
			temp += '	<a href="{{url "/assets"}}/{{../type}}/{{../id}}">';
			temp += '	<div class="asset-icon">';	
			temp += '		{{#if ../indashboard}}';	
			temp += '				<i class="icon-bookmark store-bookmark-icon"></i>';	
			temp += '		{{/if}}';		
			temp += '	<img src="{{#if images_thumbnail}}{{images_thumbnail}}{{/if}}">';
			temp += '	</div> </a>';
			temp += '	<div class="asset-details">';
			temp += '		<div class="asset-name">';
			temp += '			<a href="{{url "/assets"}}/{{../type}}/{{../id}}"> <h4>{{overview_displayName}}</h4> </a>';
			temp += '		</div>';
			temp += '		<div class="asset-rating">';
			temp += '			<div class="asset-rating-{{../rating/average}}star">';
			temp += '			</div>';
			temp += '		</div>';
			temp += '		<div class="asset-author-category">';
			temp += '			<ul>';
			temp += '				<li>';
			temp += '					<h4>{{t "Version"}}</h4>';
			temp += '					<a class="asset-version" href="#">{{overview_version}}</a>';
			temp += '				</li>';
			temp += '				<li>';
			temp += '					<h4>{{t "Category"}}</h4>';
			temp += '					<a class="asset-category" href="#">{{cap ../type}}</a>';
			temp += '				</li>';
			temp += '				<li>';
			temp += '					<h4>{{t "Author"}}</h4>';
			temp += '					<a class="asset-author" href="#">{{overview_provider}}</a>';					
			temp += '				</li>';
			temp += '			</ul>';
			temp += '			{{#if ../indashboard}}';
			temp += '			<a href="#" class="btn disabled btn-added">{{t "Bookmarked"}}</a>';
			temp += '			{{else}}';
			temp += '				{{# if ../../../../sso}}';			
			temp += '				<a href="{{url "/login"}}" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
			temp += '				{{else}}';							
			temp += '					<a href="#" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
			temp += '				{{/if}}';
			temp += '				{{# if ../../../../user.username}}';		
			temp += '				<a href="#" class="btn btn-primary asset-add-btn">{{t "Bookmark"}}</a>';
			temp += '				{{/if}}';
			temp += '			{{/if}}';
			temp += '		</div>';
			temp += '	</div>';
			temp += '	{{/attributes}}';
			temp += '</div>';
			temp += '{{/each}}';
			temp += '</div>{{/slice}}';
			
      var assetsTemp = Handlebars.compile(temp);
 	  var render = assetsTemp(data.body.assets.context);
      $('#assets-container').append(render);
      
       caramel.js($('body'), data.body['assets'].resources.js, 'assets', function () {
//                mouseStop();
            });
    	
    };

//    mouseStop = function () {
//    	var windowWidth = $(window).width();
//    	var offsetTop = windowWidth < 980 ? 167 : 200;
//        var id;
//        $('.asset').mousestop(function () {
//            var that = $(this);
//            id = setTimeout(function () {
//		that.find('.store-bookmark-icon').animate({
//		    top : -200
//		}, 200);
//                that.find('.asset-details').animate({
//                    top: 0
//                }, 200);
//            }, 300);
//        }).mouseleave(function () {
//                clearTimeout(id);
//		$(this).find('.store-bookmark-icon').animate({top: -4}, 200);
//                $(this).find('.asset-details').animate({top: offsetTop}, 200);
//            });
//    };
}());
