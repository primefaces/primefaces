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
 * 
 * @generator hogehogera
 * @method hogehogera method hogehogera
 * @next {number} hogehogera hogehogera next
 * @yield {string} hogehogera hogehogera yield
 * @return {RegExp} hogehogera hogehogera retval
 * 
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
    },
    /**
     * method foobar
     * @next {string} foobar next
     * @yield {boolean} foobar yield
     * @return {number} foobar retval
     */
    foobar: function*() {
        const x = yield false;
        return 0;
    },
    /**
     * method foobaz
     * @next {string} foobaz next
     * @yield {boolean} foobaz yield
     */
    foobaz: function*() {
        const x = yield false;
    },
    /**
     * method foobazbar
     * @yield {undefined} foobazbar yield
     * @next {string} foobazbar next
     * @return {number} foobazbar retval
     */
    foobazbar: function*() {
        const x = yield;
        return 0;
    },
})