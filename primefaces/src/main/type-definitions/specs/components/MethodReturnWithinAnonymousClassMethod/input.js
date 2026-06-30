/**
 * Tests that no return tag is required when a method of an anonymous class inside the method returns.
 */
({
    /**
     * method foo
     */
    foo() {
        const x = new class {
            bar() {
                return 0;
            }
        }();
    }
})