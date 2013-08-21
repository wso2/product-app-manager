var render = function (theme, data, meta, require) {
    theme('simple', {
    title: data.title,
    body: [
         { partial:'body', context: data.body},
         { partial:'stream', context: data.stream}
     ]
    });
};
