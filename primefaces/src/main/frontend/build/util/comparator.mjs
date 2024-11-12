// @ts-check

/**
 * @template T
 * @param {T} a 
 * @param {T} b 
 * @returns {number}
 */
export function naturalCompare(a, b) {
    return a < b ? -1 : a > b ? 1 : 0;
}

/**
 * @template T
 * @param  {...(a: T, b: T) => number} comparators 
 * @returns {(a: T, b: T) => number}
 */
export function comparingInStages(...comparators) {
    return (a, b) => {
        for (const comparator of comparators) {
            const result = comparator(a, b);
            if (result !== 0) {
                return result;
            }
        }
        return 0;
    };
}

/**
 * @template Item
 * @template Key
 * @param {(item: Item) => Key} keyExtractor 
 * @param {(a: Key, b: Key) => number} comparator 
 * @returns {(a: Item, b: Item) => number}
 */
export function comparingBy(keyExtractor, comparator = naturalCompare) {
    return (a, b) => {
        const keyA = keyExtractor(a);
        const keyB = keyExtractor(b);
        return comparator(keyA, keyB);
    };
}