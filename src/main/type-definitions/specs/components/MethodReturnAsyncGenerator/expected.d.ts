declare namespace PrimeFaces.widget {
    /**
     * Tests a <AT>return and <AT>yield with an async generator method
     */
    export class MethodReturnAsyncGenerator {
        /**
         * method hoge
         * 
         * @yield hoge yield
         */
        hoge(): AsyncIterableIterator<string>;
        /**
         * method hogehoge
         * 
         * @yield hogehoge yield
         * @return hogehoge retval
         */
        hogehoge(): PrimeFaces.AsyncGeneratorResult<string, RegExp>;
        /**
         * method hogera
         * 
         * @return hogera retval
         */
        hogera(): AsyncIterableIterator<RegExp>;
        /**
         * method bar
         * 
         * @return bar retval
         */
        bar(): AsyncIterableIterator<number>;
        /**
         * method baz
         * 
         * @yield baz yield
         * @return baz retval
         */
        baz(): PrimeFaces.AsyncGeneratorResult<boolean, number>;
        /**
         * method foo
         * 
         * @yield foo yield
         */
        foo(): AsyncIterableIterator<boolean>;
    }
}