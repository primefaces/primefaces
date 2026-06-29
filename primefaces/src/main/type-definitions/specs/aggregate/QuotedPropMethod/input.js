/**
 * Test that method names are properly escaped.
 * @interface
 */
Test.QuotedPropMethod.Iface = {
    /**
     * method foo-bar
     * @param {number} x param x
     * @return {number} return value
     */
    "foo-bar"(x) {
        return x;
    },

    /**
     * method quote-"
     * @param {boolean} a param a
     * @return {boolean} return value
     */
    "quote-\""(a) {
        return !a;
    }
};