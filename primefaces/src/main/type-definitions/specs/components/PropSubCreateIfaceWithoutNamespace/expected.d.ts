declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create a type without namespace for a prop
     */
    export class PropSubCreateIfaceWithoutNamespace {
        /**
         * prop bar
         */
        bar: IfaceWithoutNamespace;
    }
}
/**
 * iface bar
 */
interface IfaceWithoutNamespace {
    /**
     * prop bar-x
     */
    x: string;
    /**
     * prop bar-y
     */
    y: number;
}
