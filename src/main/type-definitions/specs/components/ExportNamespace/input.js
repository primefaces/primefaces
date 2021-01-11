/**
 * Tests exporting an object as a namespace
 * @namespace {Test.ExportNamespace}
 */
({
    /**
     * prop foo
     * @type {number}
     */
    foo: 9,
    /**
     * method bar
     * @param {string} x bar param x
     * @return {boolean} bar retval
     */
    bar(x) {
        return x === "foo";
    },
})