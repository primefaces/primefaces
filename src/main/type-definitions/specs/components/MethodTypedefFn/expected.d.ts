declare namespace PrimeFaces.widget {
    export type MethodTypedefTypeFn =
    /**
     * @param x param x
     * @return return value
     */
    (x: string) => boolean;
    /**
     * Tests a method with typedef
     */
    export class MethodTypedefFn {
        /**
         * method bar
         */
        bar(): void;
        /**
         * method foo
         */
        foo(): void;
    }
}