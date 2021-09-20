/**
 * Tests that a property accessed via `this` in an arrow function is included.
 * @prop {number} foo Prop foo
 * @prop {number} bar Prop bar
 */
({
    met1() {
        const fn = () => {
            this.foo = 9;
            const fn2 = () => {
                this.bar = 9;
            };
        };
    }
})