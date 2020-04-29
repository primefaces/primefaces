declare namespace Test.ObjectAssignmentConstTypedefFn {
    /**
     * the type
     */
    export type TheType =
    /**
     * @param x param x
     * @return return value
     */
    (x: string) => boolean;

    /**
     * const foo
     */
    export let Foo: TheType;
}