var render = function (theme, data, meta, require) {
    theme('simple', {
        title: data.title,
        body: [
            { partial: 'comment-input', context: data.review_param},
            { partial: 'stream', context: data.stream}
        ]
    });
};
