declare namespace InheritDescriptionSplit {
    /**
     * interface foo 1
     */
    interface Foo {
        /**
         * method foo 1
         */
        foo(): string;
    }
    /**
     * interface foo 2
     */
    interface Foo {
        /**
         * method foo 2
         */
        foo(): string;
    }

    /**
     * interface bar
     * @inheritdoc
     */
    interface Bar extends Foo, Baz {
        /**
         * method bar
         * @inheritdoc
         */
        foo(): string;
    }

    /**
     * interface baz
     */
    interface Baz {
        /**
         * method baz
         */
        foo(): string;
    }
}