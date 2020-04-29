/**
 * Tests exporting a property as an interface 
 * @class
 */
({
    /**
     * prop request
     * @interface {PrimeFaces.propAsIface.ThePropAsIface} . the iface 
     * @type {PrimeFaces.propAsIface.ThePropAsIface}
     * @author . John Doe
     */
    IfaceAsProp: {
        /**
         * method send
         * @param {string} x param x
         * @return {number} send retval
         */
        send(x){
            return 0;
        }
    }
})