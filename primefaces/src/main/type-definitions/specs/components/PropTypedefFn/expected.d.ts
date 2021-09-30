declare namespace PrimeFaces.widget {
    /**
     * the type
     */
    export type TheType = 
    /**
     * @param x param x
     * @return return value
     */
    (x: boolean) => RegExp;
    /**
     * Tests a property with typedef
     */
    export class PropTypedefFn {
        /**
         * prop foo
         */
        foo: number;
    }
}