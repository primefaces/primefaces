/**
 * iface foo
 */
interface foo {
    /**
     * function foo
     * @param x foo param x
     * @return foo retval
     */
    foo(x: string): number;
}

/**
 * iface foo
 */
interface bar {
    /**
     * function bar
     * @param x foo param x
     * @return foo retval
     */
    bar(x: string): number;
}

declare namespace Test.FunctionDeclarationExportIface {
    /**
     * iface Foo
     */
    export interface Foo {
        /**
         * function foo3
         * @param x foo param x
         * @return foo retval
         */
        foo3(x: string): number;
    }
}

declare namespace Test.FunctionDeclarationExportIface {
    /**
     * iface Foo
     */
    export interface Foo {
        /**
         * function bar
         * @param x foo param x
         * @return foo retval
         */
        bar(x: string): number;
    }
}