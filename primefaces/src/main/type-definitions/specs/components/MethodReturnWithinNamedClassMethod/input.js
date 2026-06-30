/**
 * Tests that no return tag is required when a method of a named class inside the method returns.
 */
({
    /**
     * method foo
     */
    foo() {
        class X {
            bar() {
                return 0;
            }
        }
    }
})