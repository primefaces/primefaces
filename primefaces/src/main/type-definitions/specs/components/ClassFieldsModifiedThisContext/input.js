/**
 * Tests that methods with an explicit this context are not scanned.
 * @prop {number} bar Prop bar
 */
({
    /**
     * @this {Window}
     */
    met1() {
        this.foo = 9;
    },
    met2() {
        this.bar = 9;
    },
})