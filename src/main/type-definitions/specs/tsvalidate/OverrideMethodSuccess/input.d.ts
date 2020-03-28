// Tests whether correctly overridden methods are not rejected
declare namespace OverrideMethodSuccess {
    interface Foo<T> {
        item: T;
    }
    interface Middle<T> extends Foo<T> {
        middle: string;
    }
    interface Bar {
        bar(): string;
    }
    interface Bar extends Middle<number> {
    }
    interface Bar {
        /**
         * @override
         */
        foo(): number;
    }
}
declare namespace OverrideMethodSuccess {
    interface Bar {
        /**
         * @override
         */
        foo(x?: string): number;
    }
}
declare namespace OverrideMethodSuccess {
    interface Foo<T> {
        foo(): number;
    }
}
