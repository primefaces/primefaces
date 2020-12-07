declare namespace PrimeFaces.widget {
    /**
     * Tests a <AT> return with async method
     */
    export class MethodReturnAsync {
        /**
         * method bar
         * @return bar retval
         */
        bar(): Promise<string>;
        /**
         * method foo
         * @return foo retval
         */
        foo(): Promise<boolean>;
    }
}