declare namespace PrimeFaces.widget {
    /**
     * Tests <AT>param for simple argument of a method of an object
     */
    export class MethodParam {
        /**
         * method bar
         * @param x param x
         * @param y param y
         * @param z param z
         */
        bar(x: RegExp, y: [number], z: {}): void;
        /**
         * method foo
         * @param x param x
         * @param y param y
         * @param z param z
         */
        foo(x: number, y: boolean, z: string): void;
    }
}