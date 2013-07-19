var updateSortUI = function () {
    $('#ul-sort-assets').find('a[data-sort="' + store.asset.paging.sort + '"]').addClass('selected-type');
};

$(function() {
    updateSortUI();
});