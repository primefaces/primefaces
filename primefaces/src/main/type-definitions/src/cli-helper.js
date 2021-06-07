// @ts-check

/**
 * @param {string} value 
 * @return {boolean}
 */
function toBoolean(value) {
    return new Set(["t", "y"]).has((value).toLowerCase()[0]);
}

/**
 * @param {string} value 
 * @return {string[]}
 */
function toStringArray(value) {
    return value.split(",").map(x => x.trim());
}

/**
 * @param {string[]} stack 
 * @param {(value: string) => void} consumer
 */
function consumeString(stack, consumer) {
    if (stack.length === 0 || stack[stack.length - 1].startsWith("--") || stack[stack.length - 1] === "''" || stack[stack.length - 1] === "\"\"") {
        // no argument given, skip
    }
    else {
        return consumer(stack.pop() || "");
    }
}

/**
 * @param {string[]} stack 
 * @param {(value: boolean) => void} consumer
 */
function consumeBoolean(stack, consumer) {
    consumeString(stack, v => consumer(toBoolean(v)));
}

/**
 * @param {string[]} stack 
 * @param {(value: string[]) => void} consumer
 */
function consumeStringArray(stack, consumer) {
    consumeString(stack, v => consumer(toStringArray(v)));
}

/**
 * @param {string[]} stack 
 * @param {(value: number) => void} consumer
 */
function consumeInt(stack, consumer) {
    return consumeString(stack, value => consumer(parseInt(value)));
}

module.exports = {
    consumeBoolean,
    consumeInt,
    consumeString,
    consumeStringArray,
};