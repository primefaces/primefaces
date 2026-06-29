/**
 * Tests methods with a this argument
 * 
 * @interface
 * 
 * @method . method sig
 * @this {string} .
 * @param {boolean} x param x
 * 
 * @method bar method bar
 * @this {Float32Array} bar
 * @param {number} bar.x bar param x
 */
({
    /**
     * method foo
     * @this {RegExp}
     * @param {string} x foo param x
     */
    foo(x) {}
})