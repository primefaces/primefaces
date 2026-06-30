/**
 * Tests that `*` is interpreted as `any`
 * 
 * @prop {*} hoge prop hoge
 * 
 * @method hogera method hogera
 * @param {*} hogera.x hogera param x
 * @return {*} hogera hogera retval
 */
({
    /**
     * prop bar
     * @type {*}
     */
    bar: 0,
    /**
     * method foo
     * @param {*} x foo param x
     * @return {*} foo retval
     */
    foo(x) {
        return 0;
    }
})