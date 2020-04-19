/**
 * const foo
 * @type {number}
 */
Test.ObjectAssignmentConst.Foo = 9;

/**
 * const bar
 * @constant {Test.ObjectAssignmentConst2.Baz}
 * @type {string}
 */
Test.ObjectAssignmentConst.Bar = "9";

/**
 * const foobar
 * @type {boolean}
 * @author John Doe
 */
Test.ObjectAssignmentConst.Foobar = true;

/**
 * Should not be exported
 */
Test.ObjectAssignmentConst.Hoge = 9;

/**
 * Should not be exported
 * @type {number}
 * @internal
 */
Test.ObjectAssignmentConst.HogeHoge = 9;