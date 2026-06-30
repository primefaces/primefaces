/**
 * Tests whether appropriate errors are throw when code does not have yield
 */
({
    /**
     * method foo
     * @next {undefined} foo next
     * @yield {boolean} foo yield
     */
    foo: function* () {
        yield;
    },
})