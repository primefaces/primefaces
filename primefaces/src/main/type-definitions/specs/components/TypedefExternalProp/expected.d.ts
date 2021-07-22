declare namespace Test.TypedefExternalProp.External {
    /**
     * type foobar
     */
    export type Foobar = string | number;
}

declare namespace PrimeFaces.widget {
    /**
     * Tests external typedef on a property
     */
    export class TypedefExternalProp {
        /**
         * prop foo
         */
        foo: boolean;
    }
}