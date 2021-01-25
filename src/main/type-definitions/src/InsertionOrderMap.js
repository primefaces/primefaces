//@ts-check

/**
 * @template K
 * @template V
 * @implements {InsertionOrderMap<K, V>}
 */
class NativeInsertionOrderMap {
    /** @type {Map<K, V>} */
    map;

    /**
     * @return {IterableIterator<[K,V]>}
     */
    [Symbol.iterator]() {
        return this.map[Symbol.iterator]();
    }

    get [Symbol.toStringTag]() {
        return this.map[Symbol.toStringTag];
    }

    /**
     * @param {Iterable<readonly [K,V]> | readonly (readonly [K,V])[] | undefined} initial
     */
    constructor(initial = undefined) {
        this.map = initial !== undefined ? new Map(initial) : new Map();
    }

    get size() {
        return this.map.size;
    }

    clear() {
        this.map.clear;
    }

    /**
     * @param {K} key
     * @return {boolean}
     */
    has(key) {
        return this.map.has(key);
    }
    /**
     * @param {K} key
     * @return {V | undefined}
     */
    get(key) {
        return this.map.get(key);
    }
    /**
     * @param {K} key
     * @param {V} value
     * @return {this}
     */
    set(key, value) {
        this.map.set(key, value);
        return this;
    }
    /**
     * @param {K} key
     * @return {boolean}
     */
    delete(key) {
        return this.map.delete(key);
    }
    /**
     * @return {V[]}
     */
    toArray() {
        // JS maps are sorted in insertion order
        return Array.from(this.map.values());
    }
    /**
     * @return {IterableIterator<V>}
     */
    values() {
        return this.map.values();
    }
    /**
     * @return {IterableIterator<K>}
     */
    keys() {
        return this.map.keys();
    }
    /**
     * @return {IterableIterator<[K,V]>}
     */
    entries() {
        return this.map.entries();
    }
    /**
     * @param {(value: V, key: K, map: InsertionOrderMap<K,V>) => void} fn 
     */
    forEach(fn) {
        this.map.forEach((value,key) => fn(value, key, this));
    }
}

module.exports = {
    NativeInsertionOrderMap,
};