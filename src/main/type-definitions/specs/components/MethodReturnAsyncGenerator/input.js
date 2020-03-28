/**
 * Tests a <AT>return and <AT>yield with an async generator method
 * @async hoge
 * @generator hoge
 * @method hoge method hoge
 * @yield {string} hoge hoge yield
 * 
 * @async hogera
 * @generator hogera
 * @method hogera method hogera
 * @return {RegExp} hogera hogera retval
 * 
 * @async hogehoge
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
    foo: async function* () {
        yield false;
    },
    /**
     * method bar
     * @return {number} bar retval
     */
    bar: async function*() {
        return 0;
    },
    /**
     * method baz
     * @yield {boolean} baz yield
     * @return {number} baz retval
     */
    baz: async function*() {
        yield false;
        return 0;
    }
})