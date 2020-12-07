declare namespace InheritTypeparamOnType {
    /**
     * Type Foo
     * @typeparam T The type T
     * @typeparam K The type K
     */
    interface Foo<T, K> {
    }
    /**
     * Type Foo
     * @typeparam T The type T
     * @typeparam K The type K
     */
    interface Bar<T, K> extends Foo<T, K> {
    }
    /**
     * Type Foo
     * @typeparam T The type T
     */
    interface Foobar<T> extends Foo<T, number> {
    }
    /**
     * Type Foo
     */
    interface Foobaz extends Foo<string, number> {
    }
}