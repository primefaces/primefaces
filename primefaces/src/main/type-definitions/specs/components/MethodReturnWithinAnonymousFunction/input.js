/**
 * Tests that no return tag is required when an anonymous function inside the method returns.
 */
({
    /**
     * method foo
     */
    foo() {
        const f = function() {
            return 0;
        };
    }
})