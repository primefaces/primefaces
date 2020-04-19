/**
 * Tests typedef on object, property and method
 * @typedef {RegExp | number} TopLevelTypedef top level typedef
 */
({
    /**
     * prop bar
     * @typedef {boolean | number} PropTypedef prop typedef
     * @type {number}
     */
    bar: 0,
    /**
     * method foo
     * @typedef {string | number} MethodTypedef method typedef
     */
    foo() {}
})