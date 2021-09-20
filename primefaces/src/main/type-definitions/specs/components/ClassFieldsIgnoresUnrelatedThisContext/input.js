/**
 * Tests that a property accessed via a different this context is ignored.
 */
({
    met1() {
        function bar() {
            this.foo = 9;
        }
        (function() {
            this.foo = 9;
        })();
        class Baz {
            met2() {
                this.foo = 9;
            }
        }
        new (class {
            met3() {
                this.foo = 9;
            }
        })();
    }
})