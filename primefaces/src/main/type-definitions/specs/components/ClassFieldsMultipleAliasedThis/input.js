/**
 * Tests that a property accessed via a `this` aliased multiple times is included.
 * 
 * @prop {number} foo
 */
({
    met1() {
        let firstAlias = this;
        (function (secondAlias = firstAlias) {
            var thirdAlias  = secondAlias;
            class Bar {
                baz() {
                    var fourthAlias = thirdAlias;
                    setTimeout(() => {
                        var fifthAlias = fourthAlias;
                        fifthAlias.foo = 1;
                    }, 0);
                }
            }
        })();
    }
})