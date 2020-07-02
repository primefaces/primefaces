/**
 * Tests an initializer on a parameter of a method
 * 
 * @method hoge method hoge
 * @param {number} [hoge.x=42] hoge param x
 */
({
    /**
     * method foo
     * @param {number} x foo param x
     */
    foo(x = 42) {},
    /**
     * method bar
     * @param {string} [x='quux'] bar param x
     */
    bar(x) {},
})