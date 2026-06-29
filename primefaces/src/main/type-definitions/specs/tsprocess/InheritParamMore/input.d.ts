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
         * @inheritdoc
         */
        foo(a, b, c?): void;
    }
    interface Baz extends Foo {
        /**
         * @inheritdoc
         * @param c param c
         */
        foo(a, b, c?): void;
    }
}