declare namespace PrimeFaces.widget {
    /**
     * Tests an optional parameter on a method
     */
    export class MethodParamOptional {
        /**
         * method bar
         * @param x bar param x
         */
        bar(x?: string): void;
        /**
         * method foo
         * @param x foo param x
         */
        foo(x?: number): void;
    }
}