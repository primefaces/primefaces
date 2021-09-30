//@ts-check

/**
 * @type {WeakMap<object, Console>}
 */
const ConsoleMap = new Map();

/**
 * @param  {unknown[]} args 
 */
function noOp(...args) {
}

/**
 * @return {Console}
 */
function createFakeConsole() {
    return new class FakeConsole {
        Console = FakeConsole;
        /** @type {any} */
        memory = undefined;
        assert = noOp;
        clear = noOp;
        count = noOp;
        countReset = noOp;
        debug = noOp;
        error = noOp;
        dir = noOp;
        dirxml = noOp;
        exception = noOp;
        group = noOp;
        groupCollapsed = noOp;
        groupEnd = noOp;
        info = noOp;
        log = noOp;
        markTimeline = noOp;
        profile = noOp;
        profileEnd = noOp;
        table = noOp;
        time = noOp;
        timeEnd = noOp;
        timeLog = noOp;
        timeStamp = noOp;
        timeline = noOp;
        timelineEnd = noOp;
        trace = noOp;
        warn = noOp;
    };
}

/**
 * @template {unknown[]} A
 * @template R
 * @param {(...args: A) => R} fn
 * @param {A} args
 * @return {Promise<R>}
 */
async function withFakeConsole(fn, ...args) {
    const oldConsole = global.console;
    try {
        ConsoleMap.set(global, oldConsole);
        global.console = createFakeConsole();
        return await fn(...args);
    }
    finally {
        global.console = oldConsole;
    }
}

/**
 * @returns {Console} The real console when a faked one is currently used.
 */
function getRealConsole() {
    return ConsoleMap.get(global) ?? global.console;
}

module.exports = {
    getRealConsole,
    withFakeConsole,
};