declare namespace InheritDescriptionMultipleReverse {
    /**
     * @inheritdoc
     */
    interface Baz extends Bar {
        /**
         * @inheritdoc
         */
        foo(): string;
    }
    /**
     * @inheritdoc
     */
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo(): string;
    }
    /**
     * interface foo
     */
    interface Foo {
        /**
         * method foo
         */
        foo(): string;
    }
}