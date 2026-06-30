declare namespace PrimeFaces.widget {
    /**
     * Tests initializers in destructured parameter
     */
    export class MethodDestructureInitializer {
        /**
         * method bar
         * @param param0 bar pattern 0
         * @param param1 bar pattern 1
         * @param x Defaults to `42`. bar param x
         * @param y Defaults to `true`. bar param y
         */
        bar([x]: number[], {y}: Record<string, string>): void;
        /**
         * method foo
         * @param param0 foo pattern 0
         * @param param1 foo pattern 1
         * @param x Defaults to `42`. foo param x
         * @param y Defaults to `true`. foo param y
         */
        foo([x]: number[], {y}: Record<string, boolean>): void;
    }
}