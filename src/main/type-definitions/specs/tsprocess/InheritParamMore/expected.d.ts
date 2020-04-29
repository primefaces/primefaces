declare namespace InheritParamMore {
    interface Foo {
        /**
         * @param x param x
         * @param y param y
         */
        foo(x, y): void;
    }
    interface Bar extends Foo {
        /**
         * @param a param x
         * @param b param y
         */
        foo(a, b, c?): void;
    }
    interface Baz extends Foo {
        /**
         * @param a param x
         * @param b param y
         * @param c param c
         */
        foo(a, b, c?): void;
    }
}