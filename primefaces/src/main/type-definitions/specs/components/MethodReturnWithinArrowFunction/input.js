/**
 * Tests that no return tag is required when an arrow function inside the method returns.
 */
({
    /**
     * method foo
     */
    foo() {
        const f = () => {
            return 0;
        };
    }
})