// Tests whether incorrectly overridden methods are rejected
declare namespace OverrideMethodSuperfluous {
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
declare namespace OverrideMethodError {
    interface Bar {
        /**
         * @override
         */
        foo(x?: string): number;
    }
}
declare namespace OverrideMethodError {
    interface Foo<T> {
        foo2(): number;
    }
}
