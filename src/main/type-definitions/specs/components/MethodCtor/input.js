/**
 * Tests the constructor modifier on a method
 * 
 * @constructor bar
 * @method bar ctor bar
 * @template {number} bar.S bar template
 * @param {number} bar.arg1 bar arg1
 * @param {Record<string, S>} bar.arg2 bar arg2
 */
({
    /**
     * ctor foo
     * @constructor
     * @template {string} T foo template
     * @param {number} arg1 foo arg1
     * @param {T[]} arg2 foo arg2
     */
    foo(arg1, arg2) {}
})