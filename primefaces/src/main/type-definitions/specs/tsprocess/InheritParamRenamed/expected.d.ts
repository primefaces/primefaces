declare namespace InheritParamRenamed {
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
         * @param x2 param x
         * @param y2 param y
         * @param z2 param z
         */
        foo(x2, y2, z2): void;
    }
}