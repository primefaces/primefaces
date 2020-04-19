
declare namespace Test.QuotedPropClass {
    /**
     * Test that property names are properly escaped.
     */
    export class Clazz {
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