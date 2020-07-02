declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create a namespace for sub props
     */
    export class PropSubCreateNamespace {
        /**
         * prop bar
         */
        bar: typeof Prop.Sub.CreateNamespace.Bar;
    }
}

/**
 * namespace bar
 */
declare namespace Prop.Sub.CreateNamespace.Bar {
    /**
     * prop bar-x
     */
    export let x: string;
    /**
     * prop bar-y
     */
    export let y: number;
    /**
     * prop bar-z
     */
    export let z: typeof Prop.Sub.CreateNamespace.Z;
}
/**
 * namespace z
 */
declare namespace Prop.Sub.CreateNamespace.Z {
    /**
     * prop bar-z-a1
     */
    export let a1: boolean;
    /**
     * prop bar-z-a2
     */
    export let a2: boolean;
}