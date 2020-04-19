declare namespace InheritDocMissing {
    interface Foo {
        foo(): void;
    }
    /**
     * @inheritdoc
     */
    interface Bar extends Foo {
        /**
         * @override
         * @inheritdoc
         */
        foo(): void;
    }
}