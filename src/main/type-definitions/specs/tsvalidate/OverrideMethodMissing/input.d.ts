// Tests whether methods not annotated with override are rejected
declare namespace OverrideMethodMissing {
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
        foo(): number;
    }
}
declare namespace OverrideMethodMissing {
    interface Bar {
        foo(x?: string): number;
    }
}
declare namespace OverrideMethodMissing {
    interface Foo<T> {
        foo(): number;
    }
}
