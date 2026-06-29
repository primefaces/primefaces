/**
 * Tests that multiple source locations are passed through.
 * @prop {number} foo Prop foo
 */
({
    met1() {
        this.foo = 9;
    },
    met2() {
        this.foo = 10;
        for (const x of []) {
            this.foo =+ 1;
        }
    },
    met3() {
        const fn = () => {
            this.foo = 11;
        };
    },

})