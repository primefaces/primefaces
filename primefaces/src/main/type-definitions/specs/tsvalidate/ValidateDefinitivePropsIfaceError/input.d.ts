declare namespace ValidateDefinitiveProps {
    interface FooIface {
    }
    /**
     * @validatedefinitiveprops json {"bar":{"name":"bar","location":[{"name":"BarIface","nodes":[{"loc":{"endColumn":4,"endLine":2,"startColumn":3,"startLine":1},"range":[127,128]}],"sourceFile":"BarSource.js"}]},"qux":{"name":"qux","location":[{"name":"FooIface","nodes":[{"loc":{"endColumn":8,"endLine":6,"startColumn":7,"startLine":5},"range":[127,128]}],"sourceFile":"FooSource.js"}]}}
     */
    interface BarIface extends FooIface {
    }
}