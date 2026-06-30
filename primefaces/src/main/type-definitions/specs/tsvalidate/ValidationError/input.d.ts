declare namespace ValidationError {
    interface Foo {
        foo(x: number): boolean[];
    }
    interface Bar extends Foo {
        foo(x: number, y: string): string[];
    }
}