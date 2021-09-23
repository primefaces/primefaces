declare namespace PrimeFaces.widget {
    /**
     * Tests that forced properties are added.
     * @validateforcedprops json ["bar","foo"]
     */
    export class ClassFieldsForcedProps {
        /**
         * Prop bar
         */
        bar: string;

        /**
        * Prop foo
        */
        foo: number;
    }
}