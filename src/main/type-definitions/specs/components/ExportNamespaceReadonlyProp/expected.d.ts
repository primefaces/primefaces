/**
 * Tests exporting an object as a namespace with a readonly prop
 */
declare namespace Test.ExportNamespaceReadonlyProp {
    /**
     * prop bar
     */
    export const bar: string;
    /**
     * prop foo
     */
    export const foo: number;
}