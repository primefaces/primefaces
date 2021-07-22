/**
 * Tests whether appropriate errors are thrown when docs have no yield
 */
({
    /**
     * method foo
     */
    foo: function* () {
        yield;
    },
})