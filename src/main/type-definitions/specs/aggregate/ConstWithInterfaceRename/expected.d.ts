
declare namespace Test.ConstWithInterfaceRename {
    /**
     * iface
     */
    export interface Bar {
        /**
         * prop foo
         */
        foo: number;
    }
}
declare namespace Test.ConstWithInterfaceRename {
    /**
     * const foo
     */
    export const Foo: Test.ConstWithInterfaceRename.Bar;
}