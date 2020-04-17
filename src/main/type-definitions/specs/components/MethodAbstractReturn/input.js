/**
 * Tests that an abstract method body does not require a return statement.
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
     * @abstract
     * @return {string} foo retval
     */
    foo() {}
})