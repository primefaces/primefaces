//@ts-check

const { recursive } = require("acorn-walk");
const { generate } = require("astring");
const { is } = require("./acorn-util");
const { newNodeErrorMessage } = require("./error");
const { assertNever } = require("./lang");

/**
 * Contrast this to `getArgumentInfo`, which only looks at top-level argument. These may be
 * named or unnamed. This method inspects all arguments and extract the symbols (variables) that
 * are actually put into the function block scope. It is these symbols that needs to be documented.
 * For example:
 * ```javascript
 * function foo(a = "z", {b = a, foo: [c,d="m",...e]} = {}, {[a+b]: f = "x"}, ...{42: g, bar: h}){}
 * ```
 * This function defintion puts the symbols `a`, `b`, `c`, `d`, `e`, `f`, `g`, and `h`
 * (and only these symbols) into the scope for the function body. 
 * @param {import("estree").Pattern} node 
 * @return {VariableInfo[]}
 * @see getArgumentInfo
 */
function getArgVariableInfo(node) {
    /** @type {{variables: VariableInfo[]}} */
    const state = { variables: [] };
    recursive(node, state, {
        // param
        Identifier(node, state) {
            //@ts-ignore
            const start = node.start;
            state.variables.push({
                name: node.name,
                initializer: "",
                rest: false,
                start: start,
            });
        },
        // [x = 1, ,,, y = 2, z = 3]
        ArrayPattern(node, state, callback) {
            for (const element of node.elements) {
                // may be an empty slot, so check
                if (element) {
                    callback(element, state);
                }
            }
        },
        // {a, b=9, c:d, e:f = 8, ...g}
        ObjectPattern(node, state, callback) {
            // typedefs are wrong...
            /** @type {(import("estree").Property | import("estree").RestElement)[]} */
            const properties = node.properties;
            for (const property of properties) {
                if (property.type === "Property") {
                    // only descend into value, don't care about how computed values are computed
                    callback(property.value, state);
                }
                else if (property.type === "RestElement") {
                    callback(property, state)
                }
            }
        },
        // ...args   ...[x,y]   ...{foo, bar}
        RestElement(node, state, callback) {
            if (is(node.argument, "Identifier")) {
                //@ts-ignore
                const start = node.start;
                state.variables.push({
                    name: node.argument.name,
                    initializer: "",
                    rest: true,
                    start: start,
                });
            }
            else {
                callback(node.argument, state);
                // do not descend on initializer, it does not contain new symbols
            }
        },
        // x=9    [x,y]=[1,2]     {x,y}={x: 2, y: 3}
        AssignmentPattern(node, state, callback) {
            if (is(node.left, "Identifier")) {
                //@ts-ignore
                const start = node.start;
                state.variables.push({
                    name: node.left.name,
                    initializer: generate(node.right),
                    rest: false,
                    start: start,
                });
            }
            else {
                // only descend on left, don't care about how destructured arguments are initialized
                callback(node.left, state);
            }
        },
        MemberExpression(node, state, callback) {
            // not sure, probably can't happen for method arguments
            throw new Error(newNodeErrorMessage("SyntaxError: MemberExpression cannot occur in function parameters", node, generate(node)));
        }
    });
    return state.variables;
}

/**
 * Given a main function parameter, extracts informations regarding that parameter. Please note that
 * this is not intended for analyzing destructured parameters. The rationale here is that typing
 * information is added only to top-level parameter. For example:
 * ```javascript
 * function foo({x,y}{}
    * ```
    * This is treated as one (unnamed) parameter with no default value. The type definition could be
    * ```typescript
    * function foo({x,y}: {x: number, y: string}){}
    * ```
    * 
    * Similiarly, 
    * ```javascript
    * function foo([x,{y,z=9}] = []){}
    * ```
    * is also treated as a single parameter, but with a default value. Typings could be
    * ```typescript
    * function foo([x,{y,z=9}]: [string, {y: number, z?: number}] = []){}
    * ```
    * 
    * Finally it is also not possible, in general, to derive these typing from
    * the types of the individual destructured arguments. The objects and arrays
    * that are passed to the function could have additional properties; and some may
    * required advanced types. Consider the following example:
    * ```javascript
    * function get(prop, {[prop]: value}) {
    *   return value;
    * }
    * // (The above is a fancy version of)
    * function get(property, object) {
    *   return object[property];
    * }
    * ```
    * The typing here cannot be derived in a straight-forward manner and could be
    * ```typescript
    * function get<T, K extends keyof T>(prop: K, {[prop]: value}: T): T[K];
    * ```
    * @param {import("estree").Pattern} pattern
    * @return {ArgumentInfo}
    * @see getArgVariableInfo
    */
function getArgumentInfo(pattern) {
    switch (pattern.type) {
        case "Identifier": {
            // function(foo) {}
            return {
                type: "param",
                typedef: "",
                initializer: "",
                name: pattern.name,
                required: true,
                rest: false,
            };
        }
        case "AssignmentPattern": {
            // function foo(x = 9){}
            // function foo([x,y] = []){}
            // function foo({x,y} = {}){}
            const left = getArgumentInfo(pattern.left);
            left.initializer = generate(pattern.right);
            left.required = false;
            return left;
        }
        case "RestElement": {
            // function(...args){}
            // function(...[x,y,z]){}
            // function(...{0:x0, 99: x99}){}
            const argument = getArgumentInfo(pattern.argument);
            argument.rest = true;
            return argument;
        }
        case "ArrayPattern": {
            // function([x = 1, ,,, y = 2, z = 3]){}
            return {
                type: "array",
                typedef: "",
                rest: false,
                required: true,
                initializer: "",
                node: pattern,
            };
        }
        // function({foo: moo = 1, bar: mar = 2}){}
        case "ObjectPattern": {
            return {
                type: "object",
                typedef: "",
                rest: false,
                required: true,
                initializer: "",
                node: pattern,
            }
        }
        case "MemberExpression": {
            // not sure, probably can't happen for method arguments
            throw new Error(newNodeErrorMessage("SyntaxError: MemberExpression cannot occur in function parameters", pattern, generate(pattern)));
        }
        default: return assertNever(pattern);
    }
}


module.exports = {
    getArgVariableInfo,
    getArgumentInfo,
}