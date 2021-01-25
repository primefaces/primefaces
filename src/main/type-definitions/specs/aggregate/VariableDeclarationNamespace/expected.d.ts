/**
 * Tests that a variable declaration can be exported as a namespace
 */
declare namespace Test.VariableDeclarationNamespace {
    /**
     * prop foo
     */
    export let foo: number;
    /**
     * prop foo2
     */
    export const foo2: number;
    /**
     * prop foo3
     */
    export let foo3: (number)|undefined;
    /**
     * method bar
     * @param x bar param x
     * @return bar retval
     */
    export function bar(x: string): boolean;
}