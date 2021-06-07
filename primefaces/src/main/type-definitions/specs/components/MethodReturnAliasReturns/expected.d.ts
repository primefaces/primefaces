declare namespace PrimeFaces.widget {
    /**
     * Tests that <AT>returns works as well
     */
    export class MethodReturnAliasReturns {
        /**
         * method bar
         * @return bar retval
         */
        bar(): string;
        /**
         * method foo
         * @return foo retval
         */
        foo(): RegExp;
    }
}