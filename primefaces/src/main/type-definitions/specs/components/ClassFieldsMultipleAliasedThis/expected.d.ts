declare namespace PrimeFaces.widget {
    /**
     * Tests that a property accessed via a `this` aliased multiple times is included.
     * @validatedefinitiveprops json {"foo":{"name":"foo","location":[{"name":"ClassFieldsMultipleAliasedThis#met1","nodes":[{"loc":{"endColumn":38,"endLine":16,"startColumn":24,"startLine":16},"range":[469,483]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsMultipleAliasedThis/input.js"}]}}
     */
    export class ClassFieldsMultipleAliasedThis {
        foo: number;

        met1(): void;
    }
}