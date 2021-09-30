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
     * interface Foo
     */
    interface Baz extends Bar {
    }
}