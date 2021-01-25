declare namespace PrimeFaces.widget {
    /**
 * Tests that an abstract method body does not require a return statement.
     */
    export abstract class MethodAbstractReturn {
        /**
         * method bar
         * @return bar retval
         */
        abstract bar(): string;
        /**
         * method foo
         * @return foo retval
         */
        abstract foo(): string;
    }
}