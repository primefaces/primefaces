/**
 * Tests an initializer on a parameter of a method
 * 
 * @method hoge method hoge
 * @structure {[x]} hoge.0
 * @structure {{y}} hoge.1
 * @pattern {number[]} [hoge.0=[]] hoge pattern 0
 * @pattern {Record<string, string>} [hoge.1={}] hoge pattern 1
 * @param hoge.x hoge param x
 * @param hoge.y hoge param y
 */
({
    /**
     * method foo
     * @pattern {number[]} [0=[]] foo pattern 0
     * @pattern {Record<string, string>} [1={}] foo pattern 1
     * @param x foo param x
     * @param y foo param y
     */
    foo([x], {y}) {}
})