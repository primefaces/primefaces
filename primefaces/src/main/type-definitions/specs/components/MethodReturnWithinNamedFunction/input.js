/**
 * Tests that no return tag is required when a named function inside the method returns.
 */
({
    /**
     * method foo
     */
    foo() {
        function f() {
            return 0;
        }
    }
})