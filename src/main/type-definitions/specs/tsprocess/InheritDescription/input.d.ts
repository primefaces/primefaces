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
     * @inheritdoc
     */
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo(): string;
    }
}