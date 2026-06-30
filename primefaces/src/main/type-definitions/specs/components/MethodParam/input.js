/**
 * Tests <AT>param for simple argument of a method of an object
 * @method bar method bar
 * @param {RegExp} bar.x param x
 * @param {[number]} bar.y param y
 * @param {{}} bar.z param z
 */
({
    /**
     * method foo
     * @param {number} x param x
     * @param {boolean} y param y
     * @param {string} z param z
     */
    foo(x, y, z) {
    }
})