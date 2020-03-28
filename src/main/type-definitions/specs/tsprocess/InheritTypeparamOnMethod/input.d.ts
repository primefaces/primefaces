declare namespace InheritTypeparamOnMethod {
    interface Foo {
        /**
         * Type Foo
         * @typeparam T The type T
         * @typeparam K The type K
         */
        foo<T, K>(): void;
    }
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo<T, K>(): void;
    }
    interface Foobar extends Foo {
        /**
         * @inheritdoc
         */
        foo<T>(): void;
    }
    interface Foobaz extends Foo {
        /**
         * @inheritdoc
         */
        foo(): void;
    }
}