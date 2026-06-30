// @ts-check

const EmptyIterableIterator = [][Symbol.iterator]();

/**
 * @template T
 * @extends Set<T>
 */
class EmptySet extends Set {
    /**
     * @override
     * @param {T} value 
     * @returns {this}
     */
    add(value) {
        throw new Error("Unmodifiable set.");
    }

    /**
     * @override
     */
    clear() {
    }

    /**
     * @override
     * @param {T} value 
     * @returns {boolean}
     */
    delete(value) {
        return false;
    }
    /**
     * @override
     * @param {(value: T, value2: T, set: Set<T>) => void} callbackfn
     * @param {any} thisArg
     * @returns {this}
     */
    forEach(callbackfn, thisArg) {
        return this;
    }
    /**
     * @override
     * @param {T} value 
     * @returns {boolean}
     */
    has(value) {
        return false;
    }

    /**
     * @override
     * @type {number}
     */
    size = 0;
    
    /**
     * @override
     * @returns {IterableIterator<T>}
     */
    keys() {
        return EmptyIterableIterator;
    }

    /**
     * @override
     * @returns {IterableIterator<T>}
     */
    values() {
        return EmptyIterableIterator;
    }
    /**
     * @override
     * @returns {IterableIterator<[T, T]>}
     */
    entries() {
        return EmptyIterableIterator;
    }
    /**
     * @override
     * @returns {IterableIterator<T>}
     */
    [Symbol.iterator]() {
        return EmptyIterableIterator;
    }
}

const Instance = new EmptySet();

/**
 * @template T
 * @returns {Set<T>}
 */
function emptySet() {
    return Instance;
}

module.exports = {
    emptySet,
};