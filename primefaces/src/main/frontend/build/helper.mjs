// @ts-check

/**
 * @template T
 * @param {T} a 
 * @param {T} b 
 * @returns {number}
 */
function naturalCompare(a, b) {
    return a < b ? -1 : a > b ? 1 : 0;
}

/**
 * Creates a regexp pattern that matches the given literal text.
 * @param {string} text A piece of literal text. 
 * @returns {string} The resulting RegExp pattern.
 */
export function escapeRegExp(text) {
    return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

/**
 * Creates a RegExp pattern that matches any of the given literal texts.
 * @param {string[]} patterns A list of literal texts. 
 * @param {string} prefix Optional prefix pattern to add in front of the pattern.
 * @param {string} suffix Optional suffix pattern to add at the end of the pattern.
 * @returns {RegExp} A RegExp matching any of the given texts.
 */
export function joinRegExp(patterns, prefix, suffix) {
    if (patterns.length === 0) {
        throw new Error("At least one pattern is required.");
    }
    return new RegExp(`${prefix}(${patterns.map(escapeRegExp).join("|")})${suffix}`);
}

/**
 * Creates a comparator that compares items by the given key.
 * @template Item The type of items to compare.
 * @template Key The type of key to compare by.
 * @param {(value: Item) => Key} keyExtractor Extracts the key from an item.
 * @param {(a: Key, b: Key) => number} comparator Compares two keys.
 * @returns {(a: Item, b: Item) => number} A comparator that compares items by the given key.
 */
export function comparingBy(keyExtractor, comparator = naturalCompare) {
    return (a, b) => comparator(keyExtractor(a), keyExtractor(b));
}

/**
 * Creates a comparator that compares items in stages. The comparators are
 * applied in order until a non-zero result is returned.
 * @template T The type of items to compare.
 * @param  {((a: T, b: T) => number)[]} comparators A list of comparators to use in order. 
 * @returns {(a: T, b: T) => number} A comparator that compares items in stages.
 */
export function comparingInStages(...comparators) {
    return (a, b) => {
        for (const compare of comparators) {
            const result = compare(a, b);
            if (result !== 0) {
                return result;
            }
        }
        return 0;
    };
}