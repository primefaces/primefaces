/**
 * function foo
 * @function {Test.FunctionDeclaration.Foo.foo}
 * @param {string} x foo param x
 * @return  {number} foo retval
 */
function foo(x) {return 42;}

/**
 * function bar
 * @function {Test.FunctionDeclaration.Bar.bar2}
 * @param {string} x bar param x
 * @return  {number} bar retval
 */
function bar(x) {return 42;}

/**
 * function baz
 * @function {Test.FunctionDeclaration.Baz.baz}
 */
function baz() {}

/**
 * Should not be picked up
 */
function baz() {}

/**
 * Should not be picked up
 * @internal
 * @function
 */
function hoge() {}
