/**
 * Tests exporting a property as a namespace 
 * @class
 */
({
    /**
     * prop request
     * @namespace {PrimeFaces.propAsNamespace.ThePropAsNamespace} . the namespace
     * @type {typeof PrimeFaces.propAsNamespace.ThePropAsNamespace}
     * @author . John Doe
     */
    NamespaceAsProp: {
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