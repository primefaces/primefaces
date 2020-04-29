
declare namespace Test.ConstWithInterface {
    /**
     * iface
     */
    export interface Iface {
        /**
         * prop foo
         */
        foo: number;
    }
}
declare namespace Test.ConstWithInterface {
    /**
     * const foo
     */
    export const Foo: Test.ConstWithInterface.Iface;
}