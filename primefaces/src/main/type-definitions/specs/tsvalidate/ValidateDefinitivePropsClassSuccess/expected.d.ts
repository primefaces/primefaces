declare namespace ValidateDefinitivePropsClassSuccess {
    class FooClass {
        baz: number;
        bar(x: number): string[];
    }
    class BarClass extends FooClass {
        qux: string;
        foo(x: number): string;
    }
}