/**
 * Query tokenizer. Example queries are(TODO: fix for a b)
 *
 * a:b c:d e:"f g: \\"h\\" k" l:"m"
 * "a":b "c":d
 * "a":"b" "c"
 * a:b c
 * c
 * a:"b" c
 * "a":b c
 * "a":b "c"
 * "a": "b"
 * a : b
 * a:b "c\\"d":"e\\"f"
 * a:b c:d e:"f g: \\"h\\" k" l:"m"
 * @param q can be something similar to the above example queries
 * @return {Array} [{key: "a", value: "b"}, {key:"c"}]
 */
var tokenize = function (q) {
    var i, ch, last, esc,
        started = false,
        buff = [],
        length = q.length,
        type = 'key',
        token = {},
        tokens = [];

    var process = function (typ) {
        started = false;
        esc = false;
        token = token || {};
        token[typ] = buff.join('');
        buff = [];
        switch (type) {
            case 'key':
                type = 'value';
                break;
            case 'value':
            case 'query':
                type = 'key';
                tokens.push(token);
                token = null;
                break;
        }
    };

    for (i = 0; i < length; i++) {
        ch = q.charAt(i);
        if (ch === '\\') {
            buff.push(ch);
        } else if (ch === ' ') {
            if (!started) {
                continue;
            }
            if (esc) {
                buff.push(ch);
                continue;
            }
            process(type);
        } else if (ch === ':') {
            if (!started) {
                continue;
            }
            if (esc) {
                buff.push(ch);
                continue;
            }
            process(type);
        } else if (ch === '"') {
            if (!started) {
                esc = true;
                continue;
            }
            if (last === '\\') {
                buff.push(ch);
                continue;
            }
            process(type);
        } else {
            if (!started) {
                started = true;
            }
            buff.push(ch);
        }
        last = ch;
    }
    if (buff.length) {
        process(type);
    }
    if (token) {
        tokens.push(token);
    }
    return tokens;
};
