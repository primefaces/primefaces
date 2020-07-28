//@ts-check

/**
 * @param  {any[]} args 
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
 * @template {(...args: any[]) => any} F
 * @param {F} fn
 * @param {Parameters<F>} args
 * @return {Promise<AsyncReturnType<F>>}
 */
async function withFakeConsole(fn, ...args) {
    const oldConsole = global.console;
    try {
        global.console = createFakeConsole();
        return await fn(...args);
    }
    finally {
        global.console = oldConsole;
    }
}

module.exports = {
    withFakeConsole,
};