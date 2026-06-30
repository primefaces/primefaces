declare namespace InheritDescription {
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
}