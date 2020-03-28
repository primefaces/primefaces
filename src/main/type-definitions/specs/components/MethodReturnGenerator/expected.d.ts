declare namespace PrimeFaces.widget {
    /**
     * Tests a <AT>return and <AT>yield with generator method
     */
    export class MethodReturnGenerator {
        /**
         * method hoge
         * 
         * @yield hoge yield
         */
        hoge(): IterableIterator<string>;
        /**
         * method hogehoge
         * 
         * @yield hogehoge yield
         * @return hogehoge retval
         */
        hogehoge(): PrimeFaces.GeneratorResult<string, RegExp>;
        /**
         * method hogera
         * 
         * @return hogera retval
         */
        hogera(): IterableIterator<RegExp>;
        /**
         * method bar
         * 
         * @return bar retval
         */
        bar(): IterableIterator<number>;
        /**
         * method baz
         * 
         * @yield baz yield
         * @return baz retval
         */
        baz(): PrimeFaces.GeneratorResult<boolean, number>;
        /**
         * method foo
         * 
         * @yield foo yield
         */
        foo(): IterableIterator<boolean>;
    }
}