/**
 * Tests whether appropriate errors are throw when code does not have yield
 */
({
    /**
     * method foo
     * @yield {boolean} foo yield
     */
    foo: function* () {
    },
})