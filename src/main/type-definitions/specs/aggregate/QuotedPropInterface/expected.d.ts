
declare namespace Test.QuotedPropInterface {
    /**
     * Test that property names are properly escaped.
     */
    export interface Iface {
        /**
         * prop foo-bar
         */
        "foo-bar": number;

        /**
         * prop quote-"
         */
        "quote-\"": boolean;
    }
}