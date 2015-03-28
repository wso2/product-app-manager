var COMMENT_TYPES = {
	LIFECYCLE: 'lifecycle-change'
};

/**
	Gets all comments about lifecycle state changes as a key-value list
	{
		"1": "App built. Submitting for review",
		"2": "App sucks! Rejected by reviewer"
	}
**/
var getLifecycleComments = function (artifactManager, rid, start, count) {
		var comments,
				registry = artifactManager.registry;

		var allComments = registry.comments(rid);
		var log = new Log();

		var lifecycleComments = {};

		allComments.forEach(function(commentObj) {
			try {
				commentObj = JSON.parse(commentObj.content);

				if(commentObj.comment && commentObj.type === COMMENT_TYPES.LIFECYCLE) {
					lifecycleComments[commentObj.order] = commentObj.comment;
				}
			} catch (e) {
				// i am leaving this intentionally blank
				// if it ever hits this, then this is a non-lifecycle comment-ish
			}
		});

		return lifecycleComments;
};


/**
 Gets the latest LC comment
 {
     "1": "App built. Submitting for review",
     "2": "App sucks! Rejected by reviewer"
 }
 **/
var getlatestLCComment = function (artifactManager, rid) {
    var comments,
            registry = artifactManager.registry;

    var allComments = registry.comments(rid);

    var lifecycleComments = {};

    if (allComments && allComments[0]) {
        try {
            var commentObj = JSON.parse(allComments[0].content);

            if(commentObj.comment && commentObj.type === COMMENT_TYPES.LIFECYCLE) {
                lifecycleComments[commentObj.order] = commentObj.comment;
            }
        } catch (e) {
            // i am leaving this intentionally blank
            // if it ever hits this, then this is a non-lifecycle comment-ish
        }
		return lifecycleComments;
    }
	return null;


};


/**
	Adds a comment about a lifecycle change
**/
var addLifecycleComment = function (artifactManager, rid, commentText, changeOrder) {

		var registry = artifactManager.registry;

		var comment = {
			type: COMMENT_TYPES.LIFECYCLE,
			order: changeOrder,
			comment: commentText
		};

		registry.comment(rid, JSON.stringify(comment));

		return registry.comments(rid);
};
