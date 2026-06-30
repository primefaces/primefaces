declare namespace ValidateDefinitiveProps {
    class FooClass {
    }
    /**
     * @validatedefinitiveprops json {"bar":{"name":"bar","location":[{"name":"BarClass","nodes":[{"loc":{"endColumn":4,"endLine":2,"startColumn":3,"startLine":1},"range":[127,128]}],"sourceFile":"BarSource.js"}]},"qux":{"name":"qux","location":[{"name":"FooClass","nodes":[{"loc":{"endColumn":8,"endLine":6,"startColumn":7,"startLine":5},"range":[127,128]}],"sourceFile":"FooSource.js"}]}}
     */
    class BarClass extends FooClass {
    }
}