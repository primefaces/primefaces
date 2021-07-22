declare namespace PrimeFaces.widget {
    /**
     * Tests that `*` is interpreted as `any`
     */
    export class TypeStarIsAny {
        /**
         * prop hoge
         */
        hoge: any;
        /**
         * method hogera
         * @param x hogera param x
         * @return hogera retval
         */
        hogera(x: any): any;
        /**
         * prop bar
         */
        bar: any;
        /**
         * method foo
         * @param x foo param x
         * @return foo retval
         */
        foo(x: any): any;
    }
}