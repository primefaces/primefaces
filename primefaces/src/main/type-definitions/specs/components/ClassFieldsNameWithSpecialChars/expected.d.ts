declare namespace PrimeFaces.widget {
    /**
     * Tests that special characters in the property name are escaped.
     * @validatedefinitiveprops json {"a,b":{"name":"a,b","location":[{"name":"ClassFieldsNameWithSpecialChars#a,b","nodes":[{"loc":{"endColumn":12,"endLine":8,"startColumn":11,"startLine":8},"range":[127,128]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsNameWithSpecialChars/input.js"}]},"a\"b":{"name":"a\"b","location":[{"name":"ClassFieldsNameWithSpecialChars#a\"b","nodes":[{"loc":{"endColumn":13,"endLine":13,"startColumn":12,"startLine":13},"range":[181,182]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsNameWithSpecialChars/input.js"}]},"a\\b":{"name":"a\\b","location":[{"name":"ClassFieldsNameWithSpecialChars#a\\b","nodes":[{"loc":{"endColumn":13,"endLine":18,"startColumn":12,"startLine":18},"range":[235,236]}],"sourceFile":"${PROJECT_BASEDIR}/src/main/type-definitions/specs/components/ClassFieldsNameWithSpecialChars/input.js"}]}}
     */
    export class ClassFieldsNameWithSpecialChars {
        "a\"b": number;
        "a,b": number;
        "a\\b": number;
    }
}