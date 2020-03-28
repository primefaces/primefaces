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
     * @inheritdoc
     */
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo(): string;
    }
    /**
     * @inheritdoc
     */
    interface Baz extends Bar {
        /**
         * @inheritdoc
         */
        foo(): string;
    }
}