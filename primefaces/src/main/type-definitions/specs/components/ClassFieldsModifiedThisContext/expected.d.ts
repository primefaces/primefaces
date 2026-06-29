declare namespace PrimeFaces.widget {
    /**
     * Tests that methods with an explicit this context are not scanned.
     * @validatedefinitiveprops json {"bar":{"name":"bar","location":[{"name":"ClassFieldsModifiedThisContext#met2","nodes":[{"loc":{"endColumn":16,"endLine":13,"startColumn":8,"startLine":13},"range":[212,220]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsModifiedThisContext/input.js"}]}}
     */
    export class ClassFieldsModifiedThisContext {
        /**
         * Prop bar
         */
        bar: number;

        met1(this: Window): void;

        met2(): void;
    }
}