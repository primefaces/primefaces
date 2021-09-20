declare namespace PrimeFaces.widget {
    /**
     * Tests that multiple source locations are passed through.
     * @validatedefinitiveprops json {"foo":{"name":"foo","location":[{"name":"ClassFieldsMultipleLocations#met1","nodes":[{"loc":{"endColumn":16,"endLine":7,"startColumn":8,"startLine":7},"range":[123,131]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsMultipleLocations/input.js"},{"name":"ClassFieldsMultipleLocations#met2","nodes":[{"loc":{"endColumn":16,"endLine":10,"startColumn":8,"startLine":10},"range":[165,173]},{"loc":{"endColumn":20,"endLine":12,"startColumn":12,"startLine":12},"range":[222,230]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsMultipleLocations/input.js"},{"name":"ClassFieldsMultipleLocations#met3","nodes":[{"loc":{"endColumn":20,"endLine":17,"startColumn":12,"startLine":17},"range":[306,314]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsMultipleLocations/input.js"}]}}
     */
    export class ClassFieldsMultipleLocations {
        /**
         * Prop foo
         */
        foo: number;

        met1(): void;

        met2(): void;

        met3(): void;
    }
}