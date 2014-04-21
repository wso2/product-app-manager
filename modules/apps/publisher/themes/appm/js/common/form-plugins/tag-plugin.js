/**
 * Description:The tag-plugin is used to render the tag cloud and return the selected tags
 *             This plugin has a dependency on the jquery.tokeninput plug-in (and should always be loaded first)
 */
$(function () {

    var DEFAULT_TAG_THEME = 'facebook';
    var ALLOW_FREE_TAG = true;


    function TagPlugin() {
        this.tagContainer = '';
        this.tagUrl = '';
    }

    /**
     * The function initializes the tokenInput plug-in
     * @param element The field which has requested this plugin be applied to it
     */
    TagPlugin.prototype.init = function (element) {
        //TODO: Replace where we get the asset type from
        var type = $('#meta-asset-type').val();
        this.tagUrl = element.meta.tagApi+type;
        this.tagContainer = '#' +element.id;
        if (!this.tagUrl) {
            console.log('Unable to locate tag api url');
            return;
        }
        fetchInitTags(this.tagUrl, this.tagContainer);
    };

    /**
     * The method returns the tags selected by the user
     * @param element The element which encapsulates the tag field
     * @returns An array of tags selected by the user
     */
    TagPlugin.prototype.getData = function (element) {
        var data = {};
        var tags=[];
        var selectedTags;

        selectedTags = $(this.tagContainer).tokenInput('get');

        for (var index in selectedTags) {
            tags.push(selectedTags[index].name);
        }

        data[element.id] = tags;

        return data;
    }

    /**
     * The function calls the tag api to fetch the tag cloud for the current asset type
     * and then initalizes the tokenInput plug-in which is used to render the tag field
     * @param tagUrl  The tag api
     * @param tagContainer The element which is used to render the tag field
     */
    var fetchInitTags = function (tagUrl, tagContainer) {

        //Obtain all of the tags for the given asset type
        $.ajax({
            url: tagUrl,
            type: 'GET',
            success: function (response) {
                var tags = JSON.parse(response);
                $(tagContainer).tokenInput(tags, {
                    theme: DEFAULT_TAG_THEME,
                    allowFreeTagging: ALLOW_FREE_TAG
                });

            },
            error: function () {
                console.log('unable to fetch tag cloud for ' + type);
            }
        });
    }

    FormManager.register('TagPlugin', TagPlugin);
});