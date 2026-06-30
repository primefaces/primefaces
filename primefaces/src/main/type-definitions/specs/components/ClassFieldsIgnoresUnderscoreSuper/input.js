/**
 * Tests that the _super property from class hacks is ignored.
 */
({
    met1() {
        this._super();
    }
})