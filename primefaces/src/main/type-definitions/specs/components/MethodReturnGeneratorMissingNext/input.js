/**
 * Tests whether appropriate errors are thrown when docs have no next
 */
({
    /**
     * method foo
     * @yield {undefined} foo yield
     */
    foo: function* () {
        const x = yield;
    },
})