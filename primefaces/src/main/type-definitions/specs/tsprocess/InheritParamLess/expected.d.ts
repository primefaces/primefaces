declare namespace InheritParamLess {
    interface Foo {
        /**
         * @param x1 param x
         * @param y1 param y
         * @param z1 param z
         */
        foo(x1, y1, z1): void;
    }
    interface Bar extends Foo {
        /**
         * @param hoge param x
         * @param quux param y
         */
        foo(hoge, quux): void;
    }
}