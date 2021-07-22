//@ts-check

const { Indentation, LineBreak, LineBreakPattern, WhiteSpaceChars } = require("./constants");

/**
 * @param {never} x 
 * @return {never}
 */
function assertNever(x) {
    throw new Error("Unexpected object: " + x);
}

/**
 * @template T
 * @param {[string, T][]} keyValuePairs
 * @return {Record<string, T>}
 */
function pairsToObject(keyValuePairs) {
    /** @type {Record<string, T>} */
    const result = {};
    for (const pair of keyValuePairs) {
        result[pair[0]] = pair[1];
    }
    return result;
}

/**
 * @template T
 * @param {{key: string, value: T}[]} entries
 * @return {Record<string, T>}
 */
function entriesToObject(entries) {
    /** @type {Record<string, T>} */
    const result = {};
    for (const entry of entries) {
        result[entry.key] = entry.value;
    }
    return result;
}

/**
 * @template T
 * @template S
 * @param {T[] | undefined} arr
 * @param {(item: T, index: number) => Promise<S | undefined>} mapper
 * @return {Promise<S[]>}
 */
async function asyncMap(arr, mapper) {
    /** @type {S[]} */
    const result = [];
    if (arr) {
        for (let index = 0; index < arr.length; ++index) {
            const value = await mapper(arr[index], index);
            if (value !== undefined) {
                result.push(value);
            }
        }
    }
    return result;
}

/**
 * @template T
 * @template S
 * @param {T[] | undefined} arr
 * @param {(item: T, index: number) => Promise<S | S[] | undefined>} mapper
 * @return {Promise<S[]>}
 */
async function asyncFlatMap(arr, mapper) {
    /** @type {S[]} */
    const result = [];
    if (arr) {
        for (let index = 0; index < arr.length; ++index) {
            const value = await mapper(arr[index], index);
            if (value !== undefined) {
                if (Array.isArray(value)) {
                    result.push(...value);
                }
                else {
                    result.push(value);
                }
            }
        }
    }
    return result;
}


/**
 * @template T
 * @param {T | undefined} value
 * @return {value is T} 
 */
function isNotUndefined(value) {
    return value !== undefined;
}

/**
 * @template T
 * @param {T | null} value
 * @return {value is T} 
 */
function isNotNull(value) {
    return value !== null;
}

/**
 * @template K
 * @template V
 * @param {{get: (key: K) => V | undefined, set: (key: K, value: V) => void}} map
 * @param {K} key
 * @param {(key: K) => V} computer
 * @return {V}
 */
function mapCompute(map, key, computer) {
    const oldValue = map.get(key);
    if (oldValue === undefined) {
        const newValue = computer(key);
        map.set(key, newValue);
        return newValue;
    }
    else {
        return oldValue;
    }
}

/**
 * Converted for JSON#stringify that can handle Maps and Sets etc.
 * @param {string} key 
 * @param {any} value
 * @return {any}
 */
function baseJsonConverter(key, value) {
    if (value === undefined || value === null) {
        return null;
    }
    if (value instanceof Map) {
        return Array.from(value);
    }
    if (value instanceof Set) {
        return Array.from(value);
    }
    if (typeof value === "symbol" || value instanceof RegExp) {
        return String(value);
    }
    if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
        return value;
    }
    if (value[Symbol.iterator]) {
        return Array.from(value[Symbol.iterator]());
    }
    return value;
}

/**
 * @param {unknown[]} arr An array to process.
 * @return {number} The number of non-empty slots in the array.
 */
function countNonEmptyArraySlots(arr) {
    let count = 0;
    for (let index = 0, len = arr.length; index < len; ++index) {
        if (index in arr) {
            count += 1;
        }
    }
    return count;
}

/**
 * @param {unknown[]} arr An array to process.
 * @return {number} The number of empty slots in the array.
 */
function countEmptyArraySlots(arr) {
    let count = 0;
    for (let index = 0, len = arr.length; index < len; ++index) {
        if (!(index in arr)) {
            count += 1;
        }
    }
    return count;
}

/**
 * Creates a comparator that compares by a certain property of an object.
 * @template TItem Type of the items to compare.
 * @template TField Type of the property to compare by
 * @param {(item: TItem) => TField} getter Getter for the property
 * @return {(lhs: TItem, rhs: TItem) => -1 | 0 | 1} A comparator function comparing items by the property
 */
function compareBy(getter) {
    return (lhs, rhs) => {
        if (lhs === undefined) {
            return rhs === undefined ? 0 : -1;
        }
        if (rhs === undefined) {
            return 1;
        }
        const l = getter(lhs);
        const r = getter(rhs);
        return l < r ? -1 : l > r ? 1 : 0;
    };
}

/**
 * Creates a new empty array of the given type.
 * @template T Type of the array elements.
 * @return {T[]} A new, empty array of the given type.
 */
function newEmptyArray() {
    return [];
}

/**
 * Capitalizes the first character of the given string.
 * @param {string} str String to capitalize.
 * @return {string} The given string, with the first character turned to upper case.
 */
function capitalize(str) {
    return str ? str.charAt(0).toUpperCase() + str.substr(1) : "";
}

/**
 * @param {string} str 
 * @return {string}
 */
function removeLineBreaksFromStartAndEnd(str) {
    while (str.startsWith("\n") || str.startsWith("\r")) {
        str = str.substr(1);
    }
    while (str.endsWith("\n") || str.endsWith("\r")) {
        str = str.substr(0, str.length - 1);
    }
    return str;
}

/**
 * Takes string and splits it into its lines. Takes care of Windows and UNIX separator.
 * @param {string} str A string to split.
 * @param {{removeTrailingLines?: string, trimLines?: boolean, excludeEmptyLines?: boolean}} param1
 * @return {string[]} The string, split into individual lines. 
 */
function splitLines(str, {
    removeTrailingLines = "",
    trimLines = false,
    excludeEmptyLines = false,
} = {}) {
    let lines = str.split(LineBreakPattern);
    if (removeTrailingLines) {
        while (lines.length > 0 && lines[0].trim() === removeTrailingLines) {
            lines.shift();
        }
        while (lines.length > 0 && lines[lines.length - 1].trim() === removeTrailingLines) {
            lines.pop();
        }
    }
    if (trimLines) {
        lines = lines.map(line => line ? line.trim() : "");
    }
    if (excludeEmptyLines) {
        lines = lines.filter(isNotEmpty);
    }
    return lines;
}

/**
 * Normalizes line breaks to unix style ("\n").
 * @param {string} string A string with line breaks to normalize.
 * @return string The same string, but with line breaks normalized to UNIX "\n".
 */
function normalizeLineBreaksUnix(string) {
    return string.replace(LineBreakPattern, LineBreak);
}

/**
 * @param {Iterable<string>} strings 
 * @param {string} separator
 * @return {string}
 */
function strJoin(strings, separator) {
    const arr = Array.isArray(strings) ? strings : Array.from(strings);
    return arr.join(separator);
}

/**
 * @param {string} string
 * @return {boolean}
 */
function isNotEmpty(string) {
    return string !== undefined && string !== null && string.length > 0;
}

/**
 * @param {string} string
 * @return {boolean}
 */
function isEmpty(string) {
    return string === undefined || string === null || string.length === 0;
}

/**
 * @param {string} string
 * @return {boolean}
 */
function isBlank(string) {
    if (isEmpty(string)) {
        return true;
    }
    for (let i = 0; i < string.length; ++i) {
        if (!WhiteSpaceChars[string.charCodeAt(i)]) {
            return false;
        }
    }
    return true;
}

/**
 * @param {string} string
 * @return {boolean}
 */
function isNotBlank(string) {
    return !isBlank(string);
}


/**
 * @template T
 * @param {T[] | undefined} arr
 * @param {(item: T, index: number) => Result<unknown>} fn
 */
async function asyncForEach(arr, fn) {
    if (arr) {
        for (let index = 0; index < arr.length; ++index) {
            await fn(arr[index], index);
        }
    }
}

/**
 * @template T
 * @template U
 * @param {T[] | undefined} arr
 * @param {(previousValue: U, currentValue: T, currentIndex: number) => Result<U>} reducer
 * @param {U} initialValue
 */
async function asyncReduce(arr, reducer, initialValue) {
    if (arr) {
        for (let index = 0; index < arr.length; ++index) {
            const result = await reducer(initialValue, arr[index], index);
            initialValue = result !== undefined ? result : initialValue;
        }
    }
    return initialValue;
}

/**
 * @param {number} indent
 * @return {string}
 */
function createIndent(indent) {
    return Indentation.repeat(indent);
}

/**
 * @template K
 * @template V
 * @param {Map<K, V[]>} map
 * @param {K} key
 * @param {V} item
 */
function pushToMappedArray(map, key, item) {
    const list = map.get(key);
    if (list !== undefined) {
        list.push(item);
    }
    else {
        map.set(key, [item]);
    }
}

/**
 * @param {string[]} lines
 * @param {number} indent
 * @return {string[]}
 */
function indentLines(lines, indent) {
    const indented = createIndent(indent);
    return lines.map(line => `${indented}${line}`);
}

/**
 * @param {string} data 
 * @return {Json}
 */
function parseJson(data) {
    return JSON.parse(data);
}

/**
 * @param {string} data 
 * @return {JsonObject}
 */
function parseJsonObject(data) {
    const parsed = parseJson(data);
    if (isJsonObject(parsed)) {
        return parsed;
    }
    else {
        throw new Error("Given data is not a valid JSON object.");
    }
}

/**
 * @param {Json | undefined} json
 * @return {json is JsonObject}
 */
function isJsonObject(json) {
    return json !== undefined && typeof json === "object" && json !== null && !Array.isArray(json);
}

/**
 * @template T
 * @template K
 * @template V
 * @param {Iterable<T>} items
 * @param {(item: T) => K} keyMapper
 * @param {(item: T) => V} valueMapper
 * @param {Partial<CollectToMapOpts<K, V>>} param3
 * @return {Map<K,V>}
 */
function collectToMap(items, keyMapper, valueMapper, {
    filter = (k, v) => true,
    reducer = (prev, cur) => cur,
} = {}) {
    /** @type {Map<K,V>} */
    const map = new Map();
    for (const item of items) {
        const key = keyMapper(item);
        const value = valueMapper(item);
        if (filter(key, value)) {
            const oldValue = map.get(key);
            const newValue = oldValue === undefined ? value : reducer(oldValue, value);
            map.set(key, newValue);
        }
    }
    return map;
}

/**
 * @template T
 * @template {(item: T, ...args: any) => any} F
 * @param {F} fn
 * @param {T} item
 * @return {(this: unknown, ...args: ParametersFromSecond<F>) => ReturnType<F>}
 */
function curry1(fn, item) {
    return fn.bind(null, item);
}

/**
 * @template T
 * @param {T[]} prev 
 * @param {T[]} cur 
 * @return {T[]}
 */
function reduceIntoFirstArray(prev, cur) {
    prev.push(...cur);
    return prev;
}

/**
 * @template K
 * @template V
 * @param {Map<K, V>} map
 * @param {(oldValue: V, neValue: V) => V} reducer
 * @param {Map<K, V>[]} moreMaps
 * @return {Map<K,V>}
 */
function mergeIntoMap(map, reducer, ...moreMaps) {
    for (const toMerge of moreMaps) {
        for (const [key, newValue] of toMerge) {
            const oldValue = map.get(key);
            const value = oldValue !== undefined ? reducer(oldValue, newValue) : newValue;
            map.set(key, value);
        }
    }
    return map;
}

/**
 * @template T
 * @param {T | undefined} item 
 * @return {T[]}
 */
function singletonArray(item) {
    return item !== undefined ? [item] : [];
}

module.exports = {
    assertNever,
    asyncForEach,
    asyncMap,
    asyncFlatMap,
    asyncReduce,
    baseJsonConverter,
    capitalize,
    compareBy,
    countEmptyArraySlots,
    countNonEmptyArraySlots,
    collectToMap,
    createIndent,
    curry1,
    entriesToObject,
    indentLines,
    isBlank,
    isJsonObject,
    isEmpty,
    isNotBlank,
    isNotEmpty,
    isNotNull,
    isNotUndefined,
    mapCompute,
    mergeIntoMap,
    newEmptyArray,
    normalizeLineBreaksUnix,
    pairsToObject,
    parseJson,
    parseJsonObject,
    pushToMappedArray,
    reduceIntoFirstArray,
    removeLineBreaksFromStartAndEnd,
    singletonArray,
    splitLines,
    strJoin,
};