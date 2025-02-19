/**
 * Prepends the prefix to each line of the value.
 * @param {string} value 
 * @param {string} first
 * @param {string} prefix 
 * @returns {string}
 */
function prependToEachLine(value, first, prefix) {
    return value.split(/\r\n|\n/).map((line, i) => `${i === 0 ? first : prefix}${line.trimStart()}`).join("\n");
}

/**
 * @param {unknown} error 
 * @param {string} prefix
 */
function logErrorRecursive(error, prefix) {
    if (error instanceof Error) {
        if (error.stack) {
            console.error(prependToEachLine(error.stack ?? "", prefix, prefix + "  "));
        } else {
            console.error(`${prefix}${error.message}`);
        }
        if (error instanceof AggregateError) {
            console.error(`${prefix}  [errors]: [`);
            for (const e of error.errors) {
                logErrorRecursive(e, prefix + "    ");
            }
            console.error(`${prefix}  ]`);
        }
    } else {
        console.error(`${prefix}${error}`);
    }
}
/**
 * Pretty prints the given error to the console. Takes care
 * of nested errors ({@link AggregateError}).
 * @param {unknown} error Error to log.
 */
export function logError(error) {
    logErrorRecursive(error, "");
}
