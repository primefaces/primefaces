/**
 * Tests that a method with a return tag and no return statement is rejected.
 * 
 * @abstract
 * 
 * @abstract bar
 * @method bar method bar
 * @return {string} bar bar retval
 */
({
    /**
     * method foo
     * @return {string}
     */
    foo() {}
})