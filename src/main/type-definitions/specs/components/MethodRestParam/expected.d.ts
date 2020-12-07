declare namespace PrimeFaces.widget {
    /**
     * Tests a rest param in a method
     */
    export class MethodRestParam {
        /**
         * method bar
         * @param x bar param x
         * @param args bar param args
         */
        bar(x: number, ...args: string[]): void;
        /**
         * method foo
         * @param x foo param x
         * @param args foo param args
         */
        foo(x: number, ...args: string[]): void;
    }
}