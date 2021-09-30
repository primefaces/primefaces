
declare namespace Test.ConstWithClass {
    /**
     * class
     */
    export class Clazz {
        /**
         * prop foo
         */
        foo: number;
    }
}
declare namespace Test.ConstWithClass {
    /**
     * const foo
     */
    export const Foo: Test.ConstWithClass.Clazz;
}