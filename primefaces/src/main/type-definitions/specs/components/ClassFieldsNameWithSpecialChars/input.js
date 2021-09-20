/**
 * Tests that special characters in the property name are escaped.
 */
({
    /**
     * @type {number}
     */
    "a,b": 9,

    /**
     * @type {number}
     */
    "a\"b": 9,

    /**
     * @type {number}
     */
    "a\\b": 9,
})