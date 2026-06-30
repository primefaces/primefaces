declare namespace PrimeFaces.widget {
    /**
     * Tests that a property accessed directly via `this` is included.
     * @validatedefinitiveprops json {"foo":{"name":"foo","location":[{"name":"ClassFieldsDirectThis#met1","nodes":[{"loc":{"endColumn":16,"endLine":7,"startColumn":8,"startLine":7},"range":[130,138]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsDirectThis/input.js"}]}}
     */
    export class ClassFieldsDirectThis {
        /**
         * Prop foo
         */
        foo: number;

        met1(): void;
    }
}