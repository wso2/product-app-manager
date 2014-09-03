var render = function (theme, data, meta, require) {
    data.stream.isLogged = data.isLogged;
    if (data.isLogged) {
        theme('simple', {
            body: [
                { partial: 'comment-input', context: data.input_param},
                { partial: 'stream', context: data.stream}
            ]
        });
    } else {
        theme('simple', {
            body: [
                { partial: 'stream', context: data.stream}
            ]
        });
    }
};
