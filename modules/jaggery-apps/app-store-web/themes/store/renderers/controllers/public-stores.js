var render = function (theme, data, meta, require) {
    theme('2-column-right', {
        title: data.title,
        header: [
            {
                partial: 'public-stores-header',
                context: {}
            }
        ],
        navigation: [
            {
                partial: 'navigation',
                context: {}//require('/helpers/navigation.js').currentPage(data.navigation, data.type, data.search)
            }
        ],
        body: [
            {
                partial: 'public-stores',
                context: {currentPage: data.currentPageNumber, stores: data.stores}
            }
        ]
    });
};