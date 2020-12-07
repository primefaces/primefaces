declare namespace PrimeFaces.widget {
    /**
     * Tests <AT>return for the method of an object
     */
    export class MethodReturn {
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