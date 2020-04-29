/**
 * Test that property names are properly escaped.
 * @interface
 */
Test.QuotedPropInterface.Iface = {
    /**
     * prop foo-bar
     * @type {number}
     */
    "foo-bar": 9,
    /**
     * prop quote-"
     * @type {boolean}
     */
    "quote-\"": true
};