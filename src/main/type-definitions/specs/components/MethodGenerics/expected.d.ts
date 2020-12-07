declare namespace PrimeFaces.widget {
    /**
     * Tests generics on a method
     */
    export class MethodGenerics {
        /**
         * method hoge
         * @typeparam T hoge type T
         * @typeparam K hoge type K
         * @typeparam R Defaults to `T`. hoge type R
         * @typeparam S Defaults to `R`. hoge type S
         * @param t hoge param t
         * @param k hoge param k
         * @param r hoge param r
         * @param s hoge param s
         */
        hoge<T, K extends keyof T, R = T, S extends R = R>(t: T, k: K, r: R, s: S): void;
        /**
         * method foo
         * @typeparam T foo type T
         * @typeparam K foo type K
         * @typeparam R Defaults to `T`. foo type R
         * @typeparam S Defaults to `R`. foo type S
         * @param t foo param t
         * @param k foo param k
         * @param r foo param r
         * @param s foo param s
         */
        foo<T, K extends keyof T, R = T, S extends R = R>(t: T, k: K, r: R, s: S): void;
    }
}