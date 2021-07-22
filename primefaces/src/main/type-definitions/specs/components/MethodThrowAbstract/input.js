/**
 * Tests that a method with a return tag and no return statement but a singular throw is accepted.
 * 
 * @abstract
 * 
 */
({
    /**
     * method foo
     * @return {string} foo return
     */
    foo() { throw new Error("I am an abstract-like method, you must override me"); }
})