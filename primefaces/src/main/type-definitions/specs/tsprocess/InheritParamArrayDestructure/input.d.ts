declare namespace InheritParamArrayDestructure {
    interface Foo {
        /**
         * @param x param x
         * @param param1 array destructure 1
         * @param y param y
         * @param z param z
         * @param k param k
         */
        foo(x, [y, z, ...k]): void;
    }
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo(a, [b, c, ...d]): void;
    }
}