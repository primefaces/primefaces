/**
 * Tests exporting an object as a namespace
 */
declare namespace Test.ExportNamespace {
    /**
     * prop foo
     */
    export let foo: number;
    /**
     * method bar
     * @param x bar param x
     * @return bar retval
     */
    export function bar(x: string): boolean;
}