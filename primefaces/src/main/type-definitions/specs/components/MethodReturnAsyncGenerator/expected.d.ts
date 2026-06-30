declare namespace PrimeFaces.widget {
    /**
     * Tests a <AT>return and <AT>yield with an async generator method
     */
    export class MethodReturnAsyncGenerator {
        /**
         * method hoge
         * @yield hoge yield
         */
        hoge(): AsyncGenerator<string, void>;
        /**
         * method hogehoge
         * @yield hogehoge yield
         * @return hogehoge retval
         */
        hogehoge(): AsyncGenerator<string, RegExp>;
        /**
         * method hogehogera
         * @yield hogehogera yield
         * @next hogehogera next
         * @return hogehogera retval
         */
        hogehogera(): AsyncGenerator<string, RegExp, number>;
        /**
         * method hogehogerara
         * @yield hogehogerara yield
         * @next hogehogerara next
         */
        hogehogerara(): AsyncGenerator<string, void, number>;
        /**
         * method hogera
         * @return hogera retval
         */
        hogera(): AsyncGenerator<never, RegExp>;
        /**
         * method bar
         * @return bar retval
         */
        bar(): AsyncGenerator<never, number>;
        /**
         * method baz
         * @yield baz yield
         * @return baz retval
         */
        baz(): AsyncGenerator<boolean, number>;
        /**
         * method foo
         * @yield foo yield
         */
        foo(): AsyncGenerator<boolean, void>;
        /**
         * method foobar
         * @yield foobar yield
         * @next foobar next
         * @return foobar retval
         */
        foobar(): AsyncGenerator<boolean, number, string>;
        /**
         * method foobaz
         * @yield foobaz yield
         * @next foobaz next
         */
        foobaz(): AsyncGenerator<boolean, void, string>;
        /**
         * method foobazbar
         * @yield foobazbar yield
         * @next foobazbar next
         * @return foobazbar retval
         */
        foobazbar(): AsyncGenerator<undefined, number, string>;
    }
}