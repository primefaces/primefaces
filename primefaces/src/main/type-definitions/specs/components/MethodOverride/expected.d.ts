declare namespace PrimeFaces.widget {
    /**
     * Tests the override modifier on a method and on a class
     */
    export class MethodOverride {
        /**
         * method bar
         * @override
         */
        override bar(): void;
        /**
         * method foo
         * @override
         */
        override foo(): void;
    }
}