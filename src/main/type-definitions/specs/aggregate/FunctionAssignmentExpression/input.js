/**
 * function foo
 * @function
 * @param {string} x foo param x
 * @return {number} foo retval
 */
PrimeFaces.FunctionAssignmentExpression.foo = function(x) {return 42;}

/**
 * function bar
 * @function {PrimeFaces.FunctionAssignmentExpression.Other.bar2}
 * @param {string} x bar param x
 * @return {number} bar retval
 */
PrimeFaces.FunctionAssignmentExpression.bar = function(x) {return 42;}

/**
 * function baz
 * @function
 */
PrimeFaces.FunctionAssignmentExpression.baz = function() {}

/**
 * Should not be picked up
 */
PrimeFaces.FunctionAssignmentExpression.hoge = function() {}

/**
 * Should not be picked up
 * @internal
 * @function
 */
PrimeFaces.FunctionAssignmentExpression.hoge = function() {}