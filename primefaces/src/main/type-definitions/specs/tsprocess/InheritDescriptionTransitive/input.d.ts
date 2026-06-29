declare namespace InheritDescriptionTransitive {
    /**
     * interface Base
     */
    interface Base {
    }
    /**
     * interface Foo
     */
    interface Foo {
    }
    interface Bar extends Foo {
    }
    /**
     * @inheritdoc
     */
    interface Baz extends Bar {
    }
}