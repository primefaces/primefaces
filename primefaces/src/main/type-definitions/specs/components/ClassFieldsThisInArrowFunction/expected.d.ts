declare namespace PrimeFaces.widget {
    /**
     * Tests that a property accessed via `this` in an arrow function is included.
     * @validatedefinitiveprops json {"foo":{"name":"foo","location":[{"name":"ClassFieldsThisInArrowFunction#met1","nodes":[{"loc":{"endColumn":20,"endLine":9,"startColumn":12,"startLine":9},"range":[204,212]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsThisInArrowFunction/input.js"}]},"bar":{"name":"bar","location":[{"name":"ClassFieldsThisInArrowFunction#met1","nodes":[{"loc":{"endColumn":24,"endLine":11,"startColumn":16,"startLine":11},"range":[266,274]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsThisInArrowFunction/input.js"}]}}
     */
    export class ClassFieldsThisInArrowFunction {
        /**
         * Prop bar
         */
        bar: number;

        /**
        * Prop foo
        */
        foo: number;

        met1(): void;
    }
}