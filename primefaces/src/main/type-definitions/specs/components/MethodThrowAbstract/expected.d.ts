declare namespace PrimeFaces.widget {
    /**
     * Tests that a method with a return tag and no return statement but a singular throw is accepted.
     */
    export abstract class MethodThrowAbstract {
        /**
         * method foo
         * @return foo return
         */
        foo(): string;
    }
}