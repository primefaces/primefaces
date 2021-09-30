// @ts-check

const { recursive, make } = require("acorn-walk");
const { is, makeVisitorsBuiltin } = require("./acorn-util");
const { analyze } = require("eslint-scope");

/**
 * @typedef {{
 * thisScopeStack: import("eslint-scope").Scope[];
 * scopeMap: Map<import("estree").ThisExpression, import("eslint-scope").Scope>;
 * }} CreateThisScopeMapWalkState
 */
undefined;

/**
 * @typedef {{
 * objectPatternStack: import("estree").ObjectPattern[];
 * objectPatternMap: Map<import("estree").Identifier, import("estree").ObjectPattern>;
 * }} CreateObjectPatternByIdWalkState
 */
undefined;

const PseudoProperties = [
    // Used by the class hacks such as base2 and Prototype
    // Or https://johnresig.com/blog/simple-javascript-inheritance
    // which is used by PrimeFaces.
    "_super",
];

/**
 * @param {import("estree").Node} node 
 * @param {import("eslint-scope").ScopeManager} scopeManager
 * @returns {Map<import("estree").ThisExpression, import("eslint-scope").Scope>}
 */
function createScopeByThisMap(node, scopeManager) {
    /** @type {CreateThisScopeMapWalkState} */
    const walkState = {
        thisScopeStack: [],
        scopeMap: new Map(),
    };

    /** @type {import("acorn-walk").RecursiveVisitors<import("acorn-walk").NodeType, CreateThisScopeMapWalkState>} */
    const defaultVisitors = make({});
    recursive(node, walkState, {
        ...defaultVisitors,
        ClassDeclaration(node, state, callback) {
            const scope = scopeManager.acquire(node);
            if (scope !== null) {
                state.thisScopeStack.push(scope);
            }
            defaultVisitors.ClassDeclaration(node, state, callback);
            if (scope !== null) {
                state.thisScopeStack.pop();
            }
        },
        FunctionDeclaration(node, state, callback) {
            const scope = scopeManager.acquire(node);
            if (scope !== null) {
                state.thisScopeStack.push(scope);
            }
            defaultVisitors.FunctionDeclaration(node, state, callback);
            if (scope !== null) {
                state.thisScopeStack.pop();
            }
        },
        FunctionExpression(node, state, callback) {
            const scope = scopeManager.acquire(node);
            if (scope !== null) {
                state.thisScopeStack.push(scope);
            }
            defaultVisitors.FunctionExpression(node, state, callback);
            if (scope !== null) {
                state.thisScopeStack.pop();
            }
        },
        ThisExpression(node, state, callback) {
            const scope = state.thisScopeStack[state.thisScopeStack.length - 1];
            if (scope !== undefined) {
                state.scopeMap.set(node, scope);
            }
            defaultVisitors.ThisExpression(node, state, callback);
        }
    });

    return walkState.scopeMap;
}

/**
 * @param {import("estree").Node} node 
 * @returns {Map<import("estree").Identifier, import("estree").ObjectPattern>}
 */
function createObjectPatternByIdMap(node) {
    /** @type {CreateObjectPatternByIdWalkState} */
    const walkState = {
        objectPatternStack: [],
        objectPatternMap: new Map(),
    };

    const defaultVisitors = makeVisitorsBuiltin(walkState);
    recursive(node, walkState, {
        ...defaultVisitors,
        ObjectPattern(node, state, callback) {
            state.objectPatternStack.push(node);
            defaultVisitors.ObjectPattern(node, state, callback);
            state.objectPatternStack.pop();
        },
        Identifier(node, state, callback) {
            const objectPattern = state.objectPatternStack[state.objectPatternStack.length - 1];
            if (objectPattern !== undefined) {
                state.objectPatternMap.set(node, objectPattern);
            }
            defaultVisitors.Identifier(node, state, callback);
        }
    });

    return walkState.objectPatternMap;
}

/**
 * @param {import("eslint-scope").ScopeManager} scopeManager
 * @returns {Map<import("estree").Identifier, import("eslint-scope").Reference>}
 */
function createReferencesByIdMap(scopeManager) {
    /** @type {Map<import("estree").Identifier, import("eslint-scope").Reference>} */
    const referencesById = new Map();
    for (const scope of scopeManager.scopes) {
        for (const variable of scope.variables) {
            for (const ref of variable.references) {
                referencesById.set(ref.identifier, ref);
            }
        }
    }
    return referencesById;
}

/**
 * @param {import("eslint-scope").ScopeManager} scopeManager
 * @returns {Map<import("estree").Identifier, import("eslint-scope").Variable>}
 */
function createVariablesByIdMap(scopeManager) {
    /** @type {Map<import("estree").Identifier, import("eslint-scope").Variable>} */
    const variablesById = new Map();
    for (const scope of scopeManager.scopes) {
        for (const variable of scope.variables) {
            for (const id of variable.identifiers) {
                variablesById.set(id, variable);
            }
            for (const ref of variable.references) {
                variablesById.set(ref.identifier, variable);
            }
        }
    }
    return variablesById;
}

/**
 * @param {import("estree").Node} node 
 * @returns {ExtendedScopeManager}
 */
function analyzeScope(node) {
    const scopeManager = analyze(node, {
        ignoreEval: true,
        impliedStrict: true,
        nodejsScope: false,
        sourceType: "module",
    });
    const topScope = scopeManager.acquire(node);
    if (topScope === null) {
        throw new Error("No scope exists for given node");
    }
    return {
        objectPatternById: createObjectPatternByIdMap(node),
        referencesById: createReferencesByIdMap(scopeManager),
        scopeByThis: createScopeByThisMap(node, scopeManager),
        topScope: topScope,
        variablesById: createVariablesByIdMap(scopeManager),
        wrapped: scopeManager,
    };
}

/**
 * @param {ExtendedScopeManager} scopeManager 
 * @returns {Map<import("eslint-scope").Variable, Set<import("eslint-scope").Scope>>}
 */
function resolveThisScopeAliasing(scopeManager) {
    /** @type {Map<import("eslint-scope").Variable, Set<import("eslint-scope").Scope>>} */
    const thisScopesByVariable = new Map([...scopeManager.variablesById.values()].map(v => [v, new Set()]));
    let changed = false;
    // Compute transitive closure of "const someVar = otherVar" statements that eventually lead to a `const myVar = this` assignment
    do {
        changed = false;
        for (const [variable, scopes] of thisScopesByVariable.entries()) {
            for (const ref of variable.references) {
                if (scopeManager.objectPatternById.has(ref.identifier)) {
                    continue;
                }
                /** @type {import("eslint-scope").Scope[] | undefined} */
                let newScopes;
                if (is(ref.writeExpr, "ThisExpression")) {
                    const assignedScope = scopeManager.scopeByThis.get(ref.writeExpr);
                    newScopes = assignedScope !== undefined ? [assignedScope] : undefined;
                }
                else if (is(ref.writeExpr, "Identifier")) {
                    const assignedVariable = scopeManager.variablesById.get(ref.writeExpr);
                    const assignedScopes = assignedVariable !== undefined ? thisScopesByVariable.get(assignedVariable) : undefined;
                    newScopes = assignedScopes !== undefined ? [...assignedScopes] : undefined;
                }
                for (const assignedScope of newScopes ?? []) {
                    if (assignedScope !== undefined && !scopes.has(assignedScope)) {
                        scopes.add(assignedScope);
                        changed = true;
                    }
                }
            }
        }
    } while (changed);
    return thisScopesByVariable;
}

/**
 * 
 * @param {import("estree").Node} node 
 * @param {ExtendedScopeManager} scopeManager 
 * @param {Map<import("eslint-scope").Variable, Set<import("eslint-scope").Scope>>} thisScopesByVariable 
 * @param {string} location
 * @returns {Map<string, ObjectCodePropertyData>}
 */
function scanForPropertiesOnTopThisScope(node, scopeManager, thisScopesByVariable, location) {
    /** @type {Map<string, ObjectCodePropertyData>} */
    const walkState = new Map();
    const defaultVisitors = makeVisitorsBuiltin(walkState);
    recursive(node, walkState, {
        ...defaultVisitors,
        MemberExpression(node, state, callback) {
            /** @type {boolean} */
            let isTopThisScope;
            if (is(node.object, "ThisExpression")) {
                const scope = scopeManager.scopeByThis.get(node.object);
                isTopThisScope = scope === scopeManager.topScope;
            }
            else if (is(node.object, "Identifier")) {
                const variable = scopeManager.variablesById.get(node.object);
                const scopes = variable !== undefined ? thisScopesByVariable.get(variable) : undefined;
                isTopThisScope = scopes !== undefined && scopes.has(scopeManager.topScope);
            }
            else {
                isTopThisScope = false;
            }
            if (isTopThisScope && is(node.property, "Identifier")) {
                let data = state.get(node.property.name);
                if (data === undefined) {
                    state.set(node.property.name, data = {
                        location: new Map(),
                        name: node.property.name,
                    })
                }
                let nodes = data.location.get(location);
                if (nodes === undefined) {
                    data.location.set(location, nodes = new Set());
                }
                nodes.add(node);
            }
            defaultVisitors.MemberExpression(node, state, callback);
        },
    });
    return walkState;
}

/**
 * @param {string} location
 * @param {import("estree").FunctionExpression | import("estree").FunctionDeclaration | import("estree").MethodDefinition} method A node that represents a method of some class.
 * @return {ObjectCodePropertyInfo}
 */
function scanMethodForThisProperties(location, method) {
    const fn = is(method, "MethodDefinition") ? method.value : method;
    const scopeManager = analyzeScope(fn);
    const thisScopesByVariable = resolveThisScopeAliasing(scopeManager);
    const properties = scanForPropertiesOnTopThisScope(fn, scopeManager, thisScopesByVariable, location);

    for (const pseudoProp of PseudoProperties) {
        properties.delete(pseudoProp)
    }

    return {
        definitive: properties,
    };
}

/**
 * @returns {Reducer<ObjectCodePropertyInfo, ObjectCodePropertyInfo>}
 */
function combiningObjectCodePropertyInfo() {
    return [
        (accumulated, item) => {
            for (const prop of item.definitive.values()) {
                let accProp = accumulated.definitive.get(prop.name);
                if (accProp === undefined) {
                    accumulated.definitive.set(prop.name, accProp = {
                        name: prop.name,
                        location: new Map(),
                    });
                }
                for (const [name, nodes] of prop.location.entries()) {
                    let accNodes = accProp.location.get(name);
                    if (accNodes === undefined) {
                        accProp.location.set(name, accNodes = new Set());
                    }
                    for (const node of nodes) {
                        accNodes.add(node);
                    }
                }
            }
            return accumulated;
        },
        {
            definitive: new Map(),
        }
    ];
}

/**
 * @returns {Reducer<ObjectDocPropertyData, ObjectDocPropertyData>}
 */
function combiningObjectDocPropertyData() {
    return [
        (accumulated, item) => {
            accumulated.name = item.name;
            accumulated.location.push(...item.location);
            return accumulated;
        },
        {
            location: [],
            name: "",
        }
    ];
}

/**
 * @returns {Reducer<ObjectCodePropertyInfo, ObjectCodePropertyInfo>}
 */
function combiningObjectCodePropertyInfo() {
    return [
        (accumulated, item) => {
            for (const prop of item.definitive.values()) {
                let accProp = accumulated.definitive.get(prop.name);
                if (accProp === undefined) {
                    accumulated.definitive.set(prop.name, accProp = {
                        name: prop.name,
                        location: new Map(),
                    });
                }
                for (const [name, nodes] of prop.location.entries()) {
                    let accNodes = accProp.location.get(name);
                    if (accNodes === undefined) {
                        accProp.location.set(name, accNodes = new Set());
                    }
                    for (const node of nodes) {
                        accNodes.add(node);
                    }
                }
            }
            return accumulated;
        },
        {
            definitive: new Map(),
        }
    ];
}
module.exports = {
    combiningObjectCodePropertyInfo,
    combiningObjectDocPropertyData,
    scanMethodForThisProperties,
};