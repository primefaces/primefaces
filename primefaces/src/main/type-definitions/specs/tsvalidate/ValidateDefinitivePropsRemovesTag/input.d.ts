declare namespace ValidateDefinitivePropsRemovesTags {
    /**
     * Class BarTag.
     * @interal
     * @validatedefinitiveprops json {"qux":{"name":"qux","location":[{"name":"BarTag","nodes":[{"loc":{"endColumn":4,"endLine":2,"startColumn":3,"startLine":1},"range":[127,128]}],"sourceFile":"QuxSource.js"}]}}
     */
    class BarTag {
        qux: string;
        foo(x: number): string;
    }
}