declare namespace PrimeFaces.widget {
    /**
     * Tests exporting a property as a namespace
     */
    export class PropAsNamespace {
        /**
         * prop request
         * @author John Doe
         */
        NamespaceAsProp: typeof PrimeFaces.propAsNamespace.ThePropAsNamespace;
    }
}
/**
 * the namespace
 * @author John Doe
 */
declare namespace PrimeFaces.propAsNamespace.ThePropAsNamespace {
    /**
     * method send
     * @param x param x
     * @return send retval
     */
    export function send(x: string): number;
}