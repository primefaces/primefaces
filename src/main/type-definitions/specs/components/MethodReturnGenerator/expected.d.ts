declare namespace PrimeFaces.widget {
    /**
     * Tests a <AT>return and <AT>yield with generator method
     */
    export class MethodReturnGenerator {
        /**
         * method hoge
         * @yield hoge yield
         */
        hoge(): Generator<string, void>;
        /**
         * method hogehoge
         * @yield hogehoge yield
         * @return hogehoge retval
         */
        hogehoge(): Generator<string, RegExp>;
        /**
         * method hogehogera
         * @yield hogehogera yield
         * @next hogehogera next
         * @return hogehogera retval
         */
        hogehogera(): Generator<string, RegExp, number>;
        /**
         * method hogehogerara
         * @yield hogehogerara yield
         * @next hogehogerara next
         */
        hogehogerara(): Generator<string, void, number>;
        /**
         * method hogera
         * @return hogera retval
         */
        hogera(): Generator<never, RegExp>;
        /**
         * method bar
         * @return bar retval
         */
        bar(): Generator<never, number>;
        /**
         * method baz
         * @yield baz yield
         * @return baz retval
         */
        baz(): Generator<boolean, number>;
        /**
         * method foo
         * @yield foo yield
         */
        foo(): Generator<boolean, void>;
        /**
         * method foobar
         * @yield foobar yield
         * @next foobar next
         * @return foobar retval
         */
        foobar(): Generator<boolean, number, string>;
        /**
         * method foobaz
         * @yield foobaz yield
         * @next foobaz next
         */
        foobaz(): Generator<boolean, void, string>;
        /**
         * method foobazbar
         * @yield foobazbar yield
         * @next foobazbar next
         * @return foobazbar retval
         */
        foobazbar(): Generator<undefined, number, string>;
    }
}