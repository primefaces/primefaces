declare namespace InheritParamRest {
    interface Foo {
        /**
         * @param x param x
         * @param y param y
         * @param args param args
         */
        foo(x: string, y: string, ...args: string[]): void;
    }
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        foo(a: string, b: string, ...more: string[]): void;
    }
    interface Foobar extends Foo {
        /**
         * @inheritdoc
         */
        foo(a: string, b: string, c: string): void;
    }
    interface Foobaz extends Foo {
        /**
         * @inheritdoc
         * @param c param c
         */
        foo(a: string, b: string, c: string): void;
    }
}