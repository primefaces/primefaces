/**
 * Tests <AT>return for the method of an object
 * @method bar method bar
 * @return {string} bar bar retval
 */
({
    /**
     * method foo
     * @return {RegExp} foo retval
     */
    foo() {
        return /foo/;
    }
})