declare namespace InheritDescriptionMultipleForward {
    /**
     * interface foo
     */
    interface Foo {
        /**
         * method foo
         */
        foo(): string;
    }
    /**
     * interface foo
     */
    interface Bar extends Foo {
        /**
         * method foo
         */
        foo(): string;
    }
    /**
     * interface foo
     */
    interface Baz extends Bar {
        /**
         * method foo
         */
        foo(): string;
    }
}