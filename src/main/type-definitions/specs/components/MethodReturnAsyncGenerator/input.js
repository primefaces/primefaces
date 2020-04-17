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
 * 
 * @async hogehogera
 * @generator hogehogera
 * @method hogehogera method hogehogera
 * @next {number} hogehogera hogehogera next
 * @yield {string} hogehogera hogehogera yield
 * @return {RegExp} hogehogera hogehogera retval
 * 
 * @async hogehogerara
 * @generator hogehogerara
 * @method hogehogerara method hogehogerara
 * @next {number} hogehogerara hogehogerara next
 * @yield {string} hogehogerara hogehogerara yield
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
    },
    /**
     * method foobar
     * @next {string} foobar next
     * @yield {boolean} foobar yield
     * @return {number} foobar retval
     */
    foobar: async function*() {
        const x = yield false;
        return 0;
    },
    /**
     * method foobaz
     * @next {string} foobaz next
     * @yield {boolean} foobaz yield
     */
    foobaz: async function*() {
        const x = yield false;
    },
    /**
     * method foobazbar
     * @yield {undefined} foobazbar yield
     * @next {string} foobazbar next
     * @return {number} foobazbar retval
     */
    foobazbar: async function*() {
        const x = yield;
        return 0;
    },
})