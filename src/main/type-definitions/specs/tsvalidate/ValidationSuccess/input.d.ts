declare namespace ValidationSuccess {
    interface Foo {
        foo(x?: number): string[];
    }
    interface Bar extends Foo {
        /**
         * @override
         * @param x 
         */
        foo(x: number): any[];
    }
}