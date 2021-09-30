declare namespace Test {
    /**
     * the type2
     */
    export type TheType2 = undefined | null;
}
declare namespace Test.ObjectAssignmentConstTypedef {
    /**
     * the type
     */
    export type TheType = "bar" | "baz";
    /**
     * const foo
     */
    export let Foo: TheType;
}