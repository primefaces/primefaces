declare namespace PrimeFaces.widget {
    /**
     * Tests methods with a this argument
     */
    export interface MethodThis {
        /**
         * method sig
         * @param x param x
         */
        (this: string, x: boolean): void;
        /**
         * method bar
         * @param x bar param x
         */
        bar(this: Float32Array, x: number): void;
        /**
         * method foo
         * @param x foo param x
         */
        foo(this: RegExp, x: string): void;
    }
}