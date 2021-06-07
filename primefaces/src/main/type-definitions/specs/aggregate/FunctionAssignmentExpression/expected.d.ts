declare namespace PrimeFaces.FunctionAssignmentExpression {
    /**
     * function foo
     * @param x foo param x
     * @return foo retval
     */
    export function foo(x: string): number;
}

declare namespace PrimeFaces.FunctionAssignmentExpression.Other {
    /**
     * function bar
     * @param x bar param x
     * @return bar retval
     */
    export function bar2(x: string): number;
}

declare namespace PrimeFaces.FunctionAssignmentExpression {
    /**
     * function baz
     */
    export function baz(): void;
}
