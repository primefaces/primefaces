declare namespace InheritParamMultiple {
    interface Baz extends Bar, Foo {
        /**
         * @param u param Bar#a
         * 
         * (from super type Foo) param Foo#x
         * @param v param Bar#b
         * 
         * (from super type Foo) param Foo#y
         */
        foo(u, v): void;
    }
    interface Foo {
        /**
         * @param x param Foo#x
         * @param y param Foo#y
         */
        foo(x, y): void;
    }
    interface Bar {
        /**
         * @param a param Bar#a
         * @param b param Bar#b
         */
        foo(a, b): void;
    }
}