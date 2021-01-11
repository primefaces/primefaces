/**
 * function foo
 * @interface iface foo
 * @function
 * @param {string} x foo param x
 * @return  {number} foo retval
 */
function foo(x) {return 42;}

/**
 * function bar
 * @interface iface foo
 * @function {bar}
 * @param {string} x foo param x
 * @return  {number} foo retval
 */
function foo2(x) {return 42;}

/**
 * function foo3
 * @interface {Test.FunctionDeclarationExportIface.Foo} iface Foo
 * @function
 * @param {string} x foo param x
 * @return  {number} foo retval
 */
function foo3(x) {return 42;}

/**
 * function bar
 * @interface {Test.FunctionDeclarationExportIface.Foo} iface Foo
 * @function {bar}
 * @param {string} x foo param x
 * @return  {number} foo retval
 */
function foo4(x) {return 42;}