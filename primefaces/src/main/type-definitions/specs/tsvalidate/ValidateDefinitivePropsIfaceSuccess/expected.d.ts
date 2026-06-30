declare namespace ValidateDefinitivePropsIfaceSuccess {
    interface FooIface {
        baz: number;
        bar(x: number): string[];
    }
    interface BarIface extends FooIface {
        qux: string;
        foo(x: number): string;
    }
}