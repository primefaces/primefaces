declare namespace PrimeFaces.widget {
    /**
     * Tests exporting a property as an interface
     */
    export class PropAsIface {
        /**
         * prop request
         * @author John Doe
         */
        IfaceAsProp: PrimeFaces.propAsIface.ThePropAsIface;
    }
}
declare namespace PrimeFaces.propAsIface {
    /**
     * the iface
     * @author John Doe
     */
    export interface ThePropAsIface {
        /**
         * method send
         * @param x param x
         * @return send retval
         */
        send(x: string): number;
    }
}