/**
 * Tests that a property accessed directly via `this` is included.
 * @prop {number} foo Prop foo
 */
({
    met1() {
        this.foo = 9;
    }
})