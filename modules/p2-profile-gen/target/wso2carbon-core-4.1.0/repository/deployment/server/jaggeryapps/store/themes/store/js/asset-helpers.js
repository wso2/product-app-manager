var renderAssets, mouseStop, isAssertTrue, addAssert;

(function () {
    renderAssets = function (data) {
        var el = $('.store-left');
        caramel.css($('head'), data.header['sort-assets'].resources.css, 'sort-assets');
        caramel.code($('head'), data.body['assets'].resources.code);
        async.parallel({
            assets: function (callback) {
                caramel.render('assets', data.body.assets.context, callback);
            },
            paging: function (callback) {
                caramel.render('pagination', data.body.pagination.context, callback);
            },
            sort: function (callback) {
                caramel.render('sort-assets', data.header['sort-assets'].context, callback);
            }
        }, function (err, result) {
            theme.loaded(el, result.sort);
            el.append(result.assets);
            el.append(result.paging);
            caramel.js($('body'), data.body['assets'].resources.js, 'assets', function () {
                mouseStop();
            });
            caramel.js($('body'), data.header['sort-assets'].resources.js, 'sort-assets', function () {
                updateSortUI();
            });
            $(document).scrollTop(0);
        });
    };

    mouseStop = function () {
    	var windowWidth = $(window).width();
    	var offsetTop = windowWidth < 980 ? 167 : 200;
        var id;
        $('.asset').mousestop(function () {
            var that = $(this);
            id = setTimeout(function () {
		that.find('.store-bookmark-icon').animate({
		    top : -200
		}, 200);
                that.find('.asset-details').animate({
                    top: 0
                }, 200);
            }, 300);
        }).mouseleave(function () {
                clearTimeout(id);
		$(this).find('.store-bookmark-icon').animate({top: -4}, 200);
                $(this).find('.asset-details').animate({top: offsetTop}, 200);
            });
    };

    isAssertTrue = function (aid,type) {
	var array = new Array();
	caramel.get('/apis/asset/'+type,{
            }, function (data) {
		for(j = 0; j < data.length; j++){
		    if(data[j]['path']==aid){
		       array.push(data[j]['path']);
		    		}
        	}
		addAssert(aid,type,array);
            });
	};

     addAssert = function (aid,type,array) {
	if(array.length>0){
		asset.process(type, aid, location.href);
		}	
	};
}());
