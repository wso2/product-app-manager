var render = function (theme, data, meta, require) {
    theme('simple', {
    title: data.title,
    body: [
         { partial:'comment-input'},
         { partial:'stream', context: data.stream}
     ]
    });
};
