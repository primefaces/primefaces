/**
 * Tests generics on a method
 * @method hoge method hoge
 * @template hoge.T hoge type T
 * @template {keyof T} hoge.K hoge type K
 * @template [hoge.R=T] hoge type R
 * @template {R} [hoge.S=R] hoge type S
 * @param {T} hoge.t hoge param t 
 * @param {K} hoge.k hoge param k 
 * @param {R} hoge.r hoge param r 
 * @param {S} hoge.s hoge param s 
 */
({
    /**
     * method foo
     * @template T foo type T
     * @template {keyof T} K foo type K
     * @template [R=T] foo type R
     * @template {R} [S=R] foo type S
     * @param {T} t foo param t 
     * @param {K} k foo param k 
     * @param {R} r foo param r 
     * @param {S} s foo param s 
     */
    foo(t, k, r, s) {}
})