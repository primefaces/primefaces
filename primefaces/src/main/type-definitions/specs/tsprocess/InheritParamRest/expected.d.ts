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
         * @param a param x
         * @param b param y
         * @param more param args
         */
        foo(a: string, b: string, ...more: string[]): void;
    }
    interface Foobar extends Foo {
        /**
         * @param a param x
         * @param b param y
         */
        foo(a: string, b: string, c: string): void;
    }
    interface Foobaz extends Foo {
        /**
         * @param a param x
         * @param b param y
         * @param c param c
         */
        foo(a: string, b: string, c: string): void;
    }
}