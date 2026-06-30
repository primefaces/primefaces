/**
 * Tests a <AT> return with async method
 * @async bar
 * @method bar method bar
 * @return {string} bar bar retval
 */
({
    /**
     * method foo
     * @return {boolean} foo retval
     */
    async foo() {
        return false;
    }
})