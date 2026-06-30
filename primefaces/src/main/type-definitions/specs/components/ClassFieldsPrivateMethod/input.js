/**
 * Tests that private methods are recognized.
 */
({
    met1() {
        this._zing();
    },

    /**
     * @private
     */
    _zing() {

    },
})