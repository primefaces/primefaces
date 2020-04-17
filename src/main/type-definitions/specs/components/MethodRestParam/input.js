/**
 * Tests a rest param in a method
 *
 * @method bar method bar
 * @param {number} bar.x bar param x
 * @param {...string[]} bar.args bar param args
 */
({
    /**
     * method foo
     * @param {number} x foo param x
     * @param {string[]} args foo param args
     */
    foo(x, ...args) {}
})