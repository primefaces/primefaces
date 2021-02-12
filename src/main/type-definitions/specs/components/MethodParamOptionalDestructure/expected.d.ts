declare namespace PrimeFaces.widget {
    /**
     * Tests an optional destructured parameter on a method
     */
    export class MethodParamOptionalDestructure {
        /**
         * method hoge
         * @param param0 hoge pattern 0
         * @param param1 hoge pattern 1
         * @param x hoge param x
         * @param y hoge param y
         */
        hoge([x]?: number[], {y}?: Record<string, string>): void;
        /**
         * method foo
         * @param param0 foo pattern 0
         * @param param1 foo pattern 1
         * @param x foo param x
         * @param y foo param y
         */
        foo([x]?: number[], {y}?: Record<string, string>): void;
    }
}