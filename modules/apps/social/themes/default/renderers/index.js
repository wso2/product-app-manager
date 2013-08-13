var render = function (theme, data, meta, require) {
    theme('simple', {
    title: [
         { partial:'title', context: data.title}
     ],
     body: [
         { partial:'body', context: data.body}
     ]
    });
};
