/**
 * Tests that a variable declaration can be exported as a namespace
 * @namespace {Test.VariableDeclarationNamespace}
 */
var instance = {
    /**
     * prop foo
     * @type {number}
     */
    foo: 0,
    /**
     * prop foo2
     * @type {number}
     * @readonly
     */
    foo2: 0,
    /**
     * prop foo3
     * @type {number}
     * @default
     */
    foo3: 0,
    /**
     * method bar
     * @param {string} x bar param x
     * @return {boolean} bar retval 
     */
    bar(x) {
        return x === "42";
    }
};