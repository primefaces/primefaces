/**
 * Tests exporting an object as a namespace with an optional prop
 */
declare namespace Test.ExportNamespaceOptionalProp {
    /**
     * prop bar
     */
    export let bar: (string)|undefined;
    /**
     * prop baz
     */
    export let baz: (boolean)|undefined;
    /**
     * prop foo
     */
    export let foo: (number)|undefined;
}