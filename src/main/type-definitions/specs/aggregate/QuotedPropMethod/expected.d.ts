
declare namespace Test.QuotedPropMethod {
    /**
     * Test that method names are properly escaped.
     */
    export interface Iface {
        /**
         * method foo-bar
         * @param x param x
         * @return return value
         */
        "foo-bar"(x: number): number;

        /**
         * method quote-"
         * @param a param a
         * @return return value
         */
        "quote-\""(a: boolean): boolean;
    }
}