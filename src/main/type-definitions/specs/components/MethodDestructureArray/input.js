/**
 * Tests destructuring arrays in methods
 * 
 * @typedef {[boolean, boolean]} DesArr typedef arr 
 * @method bar method bar
 * @structure {[a, b, [c1, c2]]} bar.1
 * @structure {[r, s]} bar.3
 * @pattern {[number, string, [boolean, boolean]]} bar.1 bar param1
 * @pattern {DesArr} bar.3 bar param3
 * @param {number} bar.x bar param x
 * @param bar.a bar param a
 * @param bar.b bar param b
 * @param bar.c1 bar param c1
 * @param bar.c2 bar param c2
 * @param {RegExp} bar.y bar param y
 * @param bar.r bar param r
 * @param bar.s bar param s
 */
({
    /**
     * method foo
     * @pattern {[number, string, [boolean, boolean]]} 1 foo param1
     * @pattern {DesArr} 3 foo param3
     * @param {number} x foo param x
     * @param a foo param a
     * @param b foo param b
     * @param c1 foo param c1
     * @param c2 foo param c2
     * @param {RegExp} y foo param y
     * @param r foo param r
     * @param s foo param s
     */
    foo(x, [a, b, [c1, c2]], y, [r, s]) {}
})