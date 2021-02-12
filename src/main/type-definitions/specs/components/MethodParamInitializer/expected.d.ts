declare namespace PrimeFaces.widget {
    /**
     * Tests an initializer on a parameter of a method
     */
    export class MethodParamInitializer {
        /**
         * method hoge
         * @param x Defaults to `42`. hoge param x
         */
        hoge(x?: number): void;
        /**
         * method bar
         * @param x Defaults to `'quux'`. bar param x
         */
        bar(x?: string): void;
        /**
         * method foo
         * @param x Defaults to `42`. foo param x
         */
        foo(x?: number): void;
    }
}