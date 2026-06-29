declare namespace PrimeFaces.widget {
    /**
     * Tests exporting an iface as a callable
     */
    export interface ExportIfaceCallable {
        /**
         * callable
         * @param x param x
         * @return retval
         */
        (this: string, x: number): boolean;
    }
}