declare namespace PrimeFaces.widget {
    /**
     * Tests an initializer on a parameter of a method
     */
    export class MethodParamInitializerDestructure {
        /**
         * method hoge
         * @param param0 Defaults to `[]`. hoge pattern 0
         * @param param1 Defaults to `{}`. hoge pattern 1
         * @param x hoge param x
         * @param y hoge param y
         */
        hoge([x]?: number[], {y}?: Record<string, string>): void;
        /**
         * method foo
         * @param param0 Defaults to `[]`. foo pattern 0
         * @param param1 Defaults to `{}`. foo pattern 1
         * @param x foo param x
         * @param y foo param y
         */
        foo([x]?: number[], {y}?: Record<string, string>): void;
    }
}