/**
 * function foo
 * @class iface FunctionAssignmentExpressionExportClass
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportClass.foo = function(x) {return 42;}

/**
 * function bar
 * @class iface FunctionAssignmentExpressionExportClass
 * @function {bar}
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportClass.foo2 = function(x) {return 42;}

/**
 * function foo3
 * @class {PrimeFaces.FunctionAssignmentExpressionExportClass2} iface FunctionAssignmentExpressionExportClass2
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportClass.foo3 = function(x) {return 42;}

/**
 * function bar2
 * @class {PrimeFaces.FunctionAssignmentExpressionExportClass3} iface FunctionAssignmentExpressionExportClass3
 * @function {bar2}
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpressionExportClass.bar2 = function(x) {return 42;}
