/**
 * Tests that a property accessed via an aliased `this` is included.
 * @prop {number} foo Prop foo
 */
({
    met1() {
        const random = this;
        random.foo = 9;
    }
})