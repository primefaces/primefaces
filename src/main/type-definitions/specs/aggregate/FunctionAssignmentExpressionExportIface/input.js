/**
 * function foo
 * @interface iface FunctionAssignmentExpressionExportIface
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportIface.foo = function(x) {return 42;}

/**
 * function bar
 * @interface iface FunctionAssignmentExpressionExportIface
 * @function {bar}
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportIface.foo2 = function(x) {return 42;}

/**
 * function foo3
 * @interface {PrimeFaces.FunctionAssignmentExpressionExportIface2} iface FunctionAssignmentExpressionExportIface2
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportIface.foo3 = function(x) {return 42;}

/**
 * function bar2
 * @interface {PrimeFaces.FunctionAssignmentExpressionExportIface3} iface FunctionAssignmentExpressionExportIface3
 * @function {bar2}
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportIface.bar2 = function(x) {return 42;}

/**
 * function foo
 * @interface iface FunctionAssignmentExpressionExportIface
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
FunctionAssignmentExpressionExportIface.foo = function(x) {return 42;}

/**
 * function FunctionAssignmentExpressionExportIface
 * @interface iface FunctionAssignmentExpressionExportIface
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
FunctionAssignmentExpressionExportIface = function(x) {return 42;}