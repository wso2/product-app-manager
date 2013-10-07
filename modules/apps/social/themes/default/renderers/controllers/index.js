var render = function (theme, data, meta, require) {
    theme('simple', {
        title: data.title,
        body: [
            { partial: 'comment-input', context: data.target},
            { partial: 'stream', context: data.stream}
        ]
    });
};
