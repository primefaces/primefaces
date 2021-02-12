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
         * Type Foo
         * @typeparam T The type T
         * @typeparam K The type K
         */
        foo<T, K>(): void;
    }
    interface Foobar extends Foo {
        /**
         * Type Foo
         * @typeparam T The type T
         */
        foo<T>(): void;
    }
    interface Foobaz extends Foo {
        /**
         * Type Foo
         */
        foo(): void;
    }
}