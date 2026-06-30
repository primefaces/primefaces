declare namespace ValidateDefinitivePropsClassSuccess {
    /**
     * @validatedefinitiveprops json {"baz":{"name":"baz","location":[{"name":"FooClass","nodes":[{"loc":{"endColumn":4,"endLine":2,"startColumn":3,"startLine":1},"range":[127,128]}],"sourceFile":"FooSource.js"}]}}
     */
     class FooClass {
        baz: number;
        bar(x: number): string[];
    }
    /**
     * @validatedefinitiveprops json {"bar":{"name":"bar","location":[{"name":"BarName","nodes":[{"loc":{"endColumn":4,"endLine":2,"startColumn":3,"startLine":1},"range":[127,128]}],"sourceFile":"BarSource"}]},"qux":{"name":"qux","location":[{"name":"QuxName","nodes":[{"loc":{"endColumn":8,"endLine":6,"startColumn":7,"startLine":5},"range":[127,128]}],"sourceFile":"QuxSource"}]}}
     */
    class BarClass extends FooClass {
        qux: string;
        foo(x: number): string;
    }
}