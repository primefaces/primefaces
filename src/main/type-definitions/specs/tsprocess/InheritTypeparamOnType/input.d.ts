declare namespace InheritTypeparamOnType {
    /**
     * Type Foo
     * @typeparam T The type T
     * @typeparam K The type K
     */
    interface Foo<T, K> {
    }
    /**
     * @inheritdoc
     */
    interface Bar<T, K> extends Foo<T, K> {
    }
    /**
     * @inheritdoc
     */
    interface Foobar<T> extends Foo<T, number> {
    }
    /**
     * @inheritdoc
     */
    interface Foobaz extends Foo<string, number> {
    }
}