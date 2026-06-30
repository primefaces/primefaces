declare namespace PrimeFaces.widget {
    /**
     * method typedef
     */
    export type MethodTypedef = string | number;
    /**
     * prop typedef
     */
    export type PropTypedef = boolean | number;
    /**
     * top level typedef
     */
    export type TopLevelTypedef = RegExp | number;
    /**
     * Tests typedef on object, property and method
     */
    export class DocCommentTypedef {
        /**
         * prop bar
         */
        bar: number;
        /**
         * method foo
         */
        foo(): void;
    }
}