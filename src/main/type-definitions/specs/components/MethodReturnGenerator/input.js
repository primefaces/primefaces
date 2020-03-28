/**
 * Tests a <AT>return and <AT>yield with generator method
 * @generator hoge
 * @method hoge method hoge
 * @yield {string} hoge hoge yield
 * 
 * @generator hogera
 * @method hogera method hogera
 * @return {RegExp} hogera hogera retval
 * 
 * @generator hogehoge
 * @method hogehoge method hogehoge
 * @yield {string} hogehoge hogehoge yield
 * @return {RegExp} hogehoge hogehoge retval
 */
({
    /**
     * method foo
     * @yield {boolean} foo yield
     */
    foo: function* () {
        yield false;
    },
    /**
     * method bar
     * @return {number} bar retval
     */
    bar: function*() {
        return 0;
    },
    /**
     * method baz
     * @yield {boolean} baz yield
     * @return {number} baz retval
     */
    baz: function*() {
        yield false;
        return 0;
    }
})