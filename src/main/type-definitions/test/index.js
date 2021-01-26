const { resolve } = require("path");
const { asyncMap, compareBy } = require("../src/lang");
const { withFakeConsole } = require("../src/fake-console");

/**
 * @return {TestCliArgs}
 */
function createDefaultCliArgs() {
    return {
        tests: [],
        items: [],
        verbose: false,
    };
}

/**
 * @param {string} testName 
 * @return {LoadedTest}
 */
function loadTest(testName) {
    try {
        const testPath = resolve(__dirname, `./${testName}`);
        const module = require(testPath);
        if (typeof module.startTest !== "function") {
            throw new Error(`Test ${testName} does not export a 'startTest' method`);
        }
        return {
            name: testName,
            startTest: module.startTest,
        };
    }
    catch (e) {
        throw new Error("Cannot find test " + testName + ":\n" + e.stack);
    }
}

/**
 * @param {TestCliArgs} cliArgs
 * @param {LoadedTest} test 
 */
async function runTest(cliArgs, test) {
    console.log(`Running ${test.name}`);
    try {
        await withFakeConsole(async () => test.startTest(cliArgs));
        return {
            name: test.name,
            error: undefined,
        };
    }
    catch (e) {
        return {
            name: test.name,
            error: e instanceof Error ? e : new Error(e),
        };
    }
}

/**
 * @return {TestCliArgs}
 */
function parseCliArgs() {
    const result = Object.assign({}, createDefaultCliArgs());
    const stack = process.argv.slice(2).reverse();
    while (stack.length > 0) {
        const arg = stack.pop();
        if (arg) {
            switch (arg.toLowerCase()) {
                case "-v":
                case "--verbose":
                    result.verbose = true;
                    break;
                case "--":
                    break;
                default:
                    if (arg.length === 0 || arg.startsWith("-")) {
                        throw new Error("Unknown CLI argument encountered: '" + arg + "'");
                    }
                    else {
                        const test = arg.split("#").map(x => x.trim());
                        if (test[0]) {
                            result.tests.push(test[0]);
                        }
                        if (test[1]) {
                            result.items.push(test[1])
                        }
                    }
                }
            }
        }
    console.log("Parsed test CLI arguments:", JSON.stringify(result));
    return result;
}

/**
 * @return {Promise<void>}
 */
async function main() {
    const cliArgs = parseCliArgs();

    const tests = cliArgs.tests.map(loadTest);

    if (tests.length === 0) {
        throw new Error("No tests were specified");
    }

    const testResults = await asyncMap(tests, x => runTest(cliArgs, x));
    const errors = testResults
        .sort(compareBy(test => test.name))
        .map(testResult => {
            if (testResult.error) {
                return `Failed ${testResult.name}: ${testResult.error.stack}`;
            }
            else {
                console.log(`Passed ${testResult.name}`);
                return undefined;
            }
        })
        .filter(x => x !== undefined);

    if (errors.length > 0) {
        throw new Error(errors.join("\n"));
    }
}

main().catch(e => {
    console.error(e);
    process.exit(1);
});