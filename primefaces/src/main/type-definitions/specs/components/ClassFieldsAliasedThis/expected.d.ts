declare namespace PrimeFaces.widget {
    /**
     * Tests that a property accessed via an aliased `this` is included.
     * @validatedefinitiveprops json {"foo":{"name":"foo","location":[{"name":"ClassFieldsAliasedThis#met1","nodes":[{"loc":{"endColumn":18,"endLine":8,"startColumn":8,"startLine":8},"range":[161,171]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsAliasedThis/input.js"}]}}
     */
    export class ClassFieldsAliasedThis {
        /**
         * Prop foo
         */
        foo: number;

        met1(): void;
    }
}