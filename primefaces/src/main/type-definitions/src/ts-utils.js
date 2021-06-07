//@ts-check

const ts = require("typescript");
const { collectToMap, curry1, isNotUndefined, mergeIntoMap } = require("./lang");

/**
 * Regarding the various different function named `getHeritage...`, consider the following code:
 * 
 * ```typescript
 * // base
 * interface Base {}
 * 
 * // c1_1
 * interface C1 extends Base{}
 * 
 * // c2_1
 * interface C2 extends C1{}
 * 
 * // c3_1
 * interface C3 extends C2{}
 * 
 * // c1_2
 * interface C1 {
 *     foo(): void;
 * }
 * 
 * // c3_2
 * interface C3 {
 *     bar(): void;
 * }
 * ```
 * 
 * Then the functions will return the following:
 * 
 * ```javascript
 * getHeritageNodes(node_c3_1) // => [ node_c2_1 ]
 * getHeritageNodesRecursive(node_c3_1) // => [ node_c2_1, node_c1_1, node_c1_2, base ]
 * getAllHeritageNodes(node_c3_1) // => [ node_c2_ 1 ] 
 * getAllHeritageNodesRecursive(node_c3_1) // => [ node_c2_1, node_c1_1, node_c1_2, base ]
 * 
 * getHeritageNodes(node_c3_2) // => []
 * getHeritageNodesRecursive(node_c3_2) // => []
 * getAllHeritageNodes(node_c3_2) // => [ node_c2_ 1 ] 
 * getAllHeritageNodesRecursive(node_c3_2) // => [ node_c2_1, node_c1_1, node_c1_2, base ]
 * 
 * getHeritageTypes(c3_1) // => [type C2]
 * getHeritageTypes(c3_2) // => [type C2]
 * 
 * getHeritageTypesRecursive(c3_1) // => [type C2, type C1, type Base]
 * getHeritageTypesRecursive(c3_2) // => [type C2, type C1, type Base]
 * ```
 */
undefined;

/**
 * @param {TsType} type
 * @return {boolean}
 */
function hasBaseTypes(type) {
    const baseType = getBaseTypes(type);
    return baseType.size > 0;
}

/**
 * @param {TsNode} node 
 * @return {node is TypeLikeNode}
 */
function isTypeLike(node) {
    return ts.isClassLike(node) || ts.isInterfaceDeclaration(node);
}

/**
 * @param {TsNode} node 
 * @return {boolean}
 */
function isTokenNode(node) {
    return node.kind >= ts.SyntaxKind.FirstToken && node.kind <= ts.SyntaxKind.LastToken;
}

/**
 * @param {TsNode} node 
 * @return {node is MethodLikeNode}
 */
function isMethodLike(node) {
    return ts.isMethodSignature(node) || ts.isMethodDeclaration(node);
}

/**
 * @param {TsNode} node 
 * @return {node is CallLikeNode}
 */
function isCallLike(node) {
    return ts.isCallSignatureDeclaration(node);
}

/**
 * @param {TsNode} node 
 * @return {node is CallLikeNode | MethodLikeNode}
 */
function isCallOrMethodLike(node) {
    return isCallLike(node) || isMethodLike(node);
}

/**
 * @param {TsNode} node 
 * @return {node is PropertyLikeNode}
 */
function isPropertyLike(node) {
    return ts.isPropertyDeclaration(node) || ts.isPropertySignature(node)
}

/**
 * @param {TsNode} node 
 * @return {node is ConstructorLikeNode}
 */
function isConstructorLike(node) {
    return ts.isConstructSignatureDeclaration(node) || ts.isConstructorDeclaration(node)
}

/**
 * @param {TsType} type
 * @return {boolean}
 */
function hasCallSignature(type) {
    return type.getCallSignatures().length > 0;
}

/**
 * @param {TsType} type 
 * @return {TsNode[]}
 */
function getDeclarationsOfType(type) {
    const symbol = type.getSymbol();
    if (symbol) {
        const declarations = symbol.getDeclarations();
        return declarations !== undefined ? declarations : [];
    }
    else {
        return [];
    }
}

/**
 * @param {TsType} type 
 * @return {type is import("typescript").LiteralType}
 */
function isBooleanType(type) {
    return (type.flags & ts.TypeFlags.Boolean) !== 0;
}

/**
 * @param {TsType} type 
 * @return {type is import("typescript").LiteralType}
 */
function isStringType(type) {
    return (type.flags & ts.TypeFlags.String) !== 0;
}

/**
 * @param {TsType} type 
 * @return {type is import("typescript").LiteralType}
 */
function isNumberType(type) {
    return (type.flags & ts.TypeFlags.Number) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isAnyType(type) {
    return (type.flags & ts.TypeFlags.Any) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isUnknownType(type) {
    return (type.flags & ts.TypeFlags.Unknown) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isNeverType(type) {
    return (type.flags & ts.TypeFlags.Never) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isVoidType(type) {
    return (type.flags & ts.TypeFlags.Void) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isNullType(type) {
    return (type.flags & ts.TypeFlags.Null) !== 0;
}

/**
 * @param {TsType} type 
 * @return {boolean}
 */
function isUndefinedType(type) {
    return (type.flags & ts.TypeFlags.Undefined) !== 0;
}

/**
 * @param {TsType} type 
 * @return {type is import("typescript").ObjectType}
 */
function isObjectType(type) {
    return (type.flags & ts.TypeFlags.Object) !== 0;
}

/**
 * 
 * @param {TsType} type 
 * @return {type is import("typescript").TypeReference}
 */
function isTypeReference(type) {
    type.isClassOrInterface
    return isObjectType(type) && (type.objectFlags & ts.ObjectFlags.Reference) !== 0;
}

/**
 * @param {ts.Node} node 
 * @param {ts.NodeFlags[]} flags
 * @return boolean
 */
function hasOneOrMoreFlag(node, ...flags) {
    return flags.some(flag => (node.flags & flag) !== 0);
}

/**
 * @param {ts.Node} node 
 * @param {ts.NodeFlags[]} flags
 * @return boolean
 */
function hasAllFlags(node, ...flags) {
    return flags.every(flag => (node.flags & flag) !== 0);
}


/**
 * @param {ts.Node} node 
 * @param {ts.NodeFlags[]} flags
 * @return boolean
 */
function hasNeitherFlag(node, ...flags) {
    return !hasAllFlags(node, ...flags);
}

/**
 * @param {TsType} type 
 * @return {import("typescript").InterfaceType | undefined}
 */
function getInterfaceType(type) {
    if (type.isClassOrInterface()) {
        return type;
    }
    else if (isTypeReference(type)) {
        return type.target;
    }
    else {
        return undefined;
    }
}

/**
 * @param {TsType} type 
 * @return {import("typescript").InterfaceType[]}
 */
function getInterfaceTypes(type) {
    return splitUnionAndIntersectionTypes(type).map(getInterfaceType).filter(isNotUndefined);
}

/**
 * @param {import("typescript").PropertyName} propertyName
 * @return {propertyName is (import("typescript").Identifier | import("typescript").StringLiteral | import("typescript").NumericLiteral)}
 */
function isNonComputedPropertyName(propertyName) {
    return !ts.isComputedPropertyName(propertyName);
}

/**
 * @param {TypeLikeNode | import("typescript").ObjectLiteralExpression | import("typescript").TypeLiteralNode} node 
 */
function getTypeName(node) {
    if (ts.isObjectLiteralExpression(node)) {
        return "<object>";
    }
    if (ts.isTypeLiteralNode(node)) {
        return "<type>";
    }
    if (node.name) {
        return node.name.text || "<unnamed>";
    }
    else {
        return "<anonymous>";
    }
}

/**
 * @param {import("typescript").Symbol | undefined} symbol 
 */
function getSymbolName(symbol) {
    return symbol && symbol.name ? symbol.name : "<unnamed>";
}

/**
 * @param {import("typescript").Type & {intrinsicName?: string}} type 
 * @return {string}
 */
function getNameOfType(type, top = true, processed = new Set()) {
    if (processed.has(type)) {
        return "<recursive>";
    }
    processed.add(type);
    if (isAnyType(type)) {
        return "any";
    }
    else if (type.isLiteral()) {
        return typeof type.value === "object" ? type.value.base10Value : String(type.value);
    }
    else if (type.isUnionOrIntersection()) {
        const joined = type.types.map(t => getNameOfType(t, false)).join(type.isUnion() ? " | " : " & ");
        return top || type.types.length === 1 ? joined : "(" + joined + ")";
    }
    else if (isTypeReference(type) && type.target !== type) {
        return getSymbolName(type.target.getSymbol());
    }
    else if (type.isClassOrInterface()) {
        return getSymbolName(type.getSymbol());
    }
    else if (type.intrinsicName) {
        return type.intrinsicName;
    }
    else {
        return getSymbolName(type.getSymbol());
    }
}

/**
 * @param {import("typescript").PropertyName} node 
 */
function getPropertyName(node) {
    if (ts.isComputedPropertyName(node)) {
        return "<computed>";
    }
    else {
        return node.text || "<unnamed>";
    }
}

/**
 * @param {CallOrMethodLikeNode} node 
 * @return {string}
 */
function getPropertyNameOfCallLikeNode(node) {
    return ts.isCallSignatureDeclaration(node) ? "__call" : getPropertyName(node.name);
}

/**
 * @param {TypeLikeNode | CallOrMethodLikeNode} node
 * @return {Set<string>}
 */
function getTypeParameterNames(node) {
    if (node.typeParameters) {
        return new Set(
            node.typeParameters
                .map(typeParam => typeParam.name.text)
                .filter(isNotUndefined)
        );
    }
    else {
        return new Set();
    }
}

/**
 * Creates a set of all (local) type parameters of the given type.
 * @param {TsType} type 
 * @return {Set<string>}
 */
function getLocalTypeParameterNames(type) {
    if (type.isClassOrInterface()) {
        if (type.localTypeParameters) {
            return new Set(
                type
                    .localTypeParameters
                    .map(x => getSymbolName(x.getSymbol()))
            );
        }
        else {
            return new Set();
        }
    }
    else if (type.getCallSignatures().length > 0) {
        return new Set(
            type
                .getCallSignatures()
                .flatMap(x => x.typeParameters)
                .filter(isNotUndefined)
                .map(x => getSymbolName(x.getSymbol()))
        );
    }
    else {
        return new Set();
    }
}

/**
 * Performs a depth-first iteration of the given node and its children. Invokes
 * the callback for each node. If the callback returns `false` no child of the
 * node is traversed.
 * @param {TsNode} rootNode 
 * @param {(node: TsNode) => boolean} fn 
 * @param {import("typescript").SourceFile} sourceFile 
 */
function depthFirstNode(rootNode, fn, sourceFile = rootNode.getSourceFile()) {
    const stack = [rootNode];
    while (stack.length > 0) {
        const node = stack.pop();
        if (node) {
            const proceedWalk = fn(node);
            if (proceedWalk && !isTokenNode(node)) {
                for (let i = node.getChildCount(sourceFile) - 1; i >= 0; --i) {
                    stack.push(node.getChildAt(i, sourceFile));
                }
            }
        }
    }
}

/**
 * @param {TsType} type
 * @return {import("typescript").Type[]}
 */
function splitUnionAndIntersectionTypes(type) {
    /** @type {Set<import("typescript").Type>} */
    const processed = new Set([]);
    /** @type {import("typescript").Type[]} */
    const result = [];
    const stack = [type];
    while (stack.length > 0) {
        const current = stack.pop();
        if (current && !processed.has(current)) {
            processed.add(current);
            if (current.isUnionOrIntersection()) {
                for (const base of current.types) {
                    stack.push(base);
                }
            }
            else {
                result.push(type);
            }
        }
    }
    return result;
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {MethodLikeNode} node
 * @return {boolean} Whether the given method overrides some parent method.
 */
function isMethodOverriding(checker, node) {
    const propertyName = getPropertyName(node.name);
    const classType = checker.getTypeAtLocation(node.parent);
    const heritageTypes = getHeritageTypesRecursive(checker, classType);
    for (const heritageType of heritageTypes) {
        const members = getOwnMembersOfType(heritageType);
        const methods = members.methods.get(propertyName);
        if (methods !== undefined && methods.length > 0) {
            return true;
        }
    }
    return false;
}

/**
 * @param {TsType} type
 * @return {TsNodeMembersResult}
 */
function getOwnMembersOfType(type) {
    /** @type {TsNodeMembersResult} */
    const result = {
        constructors: [],
        methods: new Map(),
        properties: new Map(),
    }
    return getDeclarationsOfType(type)
        .filter(isTypeLike)
        .map(getOwnMembersOfNode)
        .reduce((r, members) => {
            result.constructors.push(...members.constructors);
            mergeIntoMap(
                result.methods,
                (oldMethods, newMethods) => [...oldMethods, ...newMethods],
                members.methods
            );
            mergeIntoMap(
                result.properties,
                (oldMethods, newMethods) => [...oldMethods, ...newMethods],
                members.properties
            );
            return r;
        }, result);
}

/**
 * @param {TypeLikeNode} node
 * @return {TsNodeMembersResult}
 */
function getOwnMembersOfNode(node) {
    /** @type {import("typescript").NodeArray<TsNode>} */
    const members = node.members;
    const methods = collectToMap(members.filter(isCallOrMethodLike),
        item => getPropertyNameOfCallLikeNode(item),
        item => [item],
        {
            reducer: (prev, cur) => [...prev, ...cur],
        },
    );
    const properties = collectToMap(members.filter(isPropertyLike),
        item => getPropertyName(item.name),
        item => [item],
        {
            reducer: (prev, cur) => [...prev, ...cur],
        },
    );
    return {
        constructors: members.filter(isConstructorLike),
        methods: methods,
        properties: properties,
    };
}

/**
 * @param {TsType} type
 * @return {Set<import("typescript").Type>}
 */
function getBaseTypesRecursive(type) {
    /** @type {Set<import("typescript").Type>} */
    const processed = new Set();
    /** @type {Set<import("typescript").Type>} */
    const baseTypes = new Set();
    const stack = [type];
    for (let current = stack.pop(); current !== undefined; current = stack.pop()) {
        if (!processed.has(current)) {
            processed.add(current);
            if (current) {
                for (const baseType of getBaseTypes(current)) {
                    baseTypes.add(baseType);
                    stack.push(baseType);
                }
            }
        }
    }
    return baseTypes;
}

/**
 * @param {TsType} type
 * @return {Set<import("typescript").Type>}
 */
function getBaseTypes(type) {
    /** @type {Set<import("typescript").Type>} */
    const baseTypes = new Set();
    const currentBaseTypes = type.getBaseTypes();
    if (currentBaseTypes) {
        for (const currentBaseType of currentBaseTypes) {
            for (const interfaceType of getInterfaceTypes(currentBaseType)) {
                baseTypes.add(interfaceType);
            }
        }
    }
    return baseTypes;
}

/**
 * Find a list of nodes referred to by the heritage clauses `extends` and `implements` of the given node.
 * @param {import("typescript").TypeChecker} checker
 * @param {TypeLikeNode} node
 * @return {(TypeLikeNode)[]}
 */
function getHeritageNodes(checker, node) {
    const result = [];
    if (node.heritageClauses) {
        for (const heritageClause of node.heritageClauses) {
            for (const heritage of heritageClause.types) {
                for (const interfaceType of getInterfaceTypes(checker.getTypeAtLocation(heritage))) {
                    for (const declaration of getDeclarationsOfType(interfaceType)) {
                        if (isTypeLike(declaration)) {
                            result.push(declaration);
                        }
                    }
                }
            }
        }
    }
    return result;
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {TypeLikeNode} node
 * @return {Set<(TypeLikeNode)>}
 */
function getHeritageNodesRecursive(checker, node) {
    /** @type {Set<TypeLikeNode>} */
    const result = new Set();
    const stack = [node];
    while (stack.length > 0) {
        const current = stack.pop();
        if (current) {
            const nodes = getHeritageNodes(checker, current);
            for (const heritageNode of nodes) {
                if (!result.has(heritageNode)) {
                    stack.push(heritageNode);
                    result.add(heritageNode);
                }
            }
        }
    }
    return result;
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {TsType} type 
 * @return {boolean} 
 */
function hasHeritageTypes(checker, type) {
    return getHeritageTypes(checker, type).size > 0;
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {TsType} type 
 * @return {Set<import("typescript").Type>} 
 */
function getHeritageTypesRecursive(checker, type) {
    /** @type {Set<import("typescript").Type>} */
    const processed = new Set();
    /** @type {Set<import("typescript").Type>} */
    const result = new Set();
    const stack = [type];
    for (let item = stack.pop(); item !== undefined; item = stack.pop()) {
        if (!processed.has(item)) {
            processed.add(item);
            for (const heritageType of getHeritageTypes(checker, item)) {
                result.add(heritageType);
                stack.push(heritageType);
            }
        }
    }
    return result;
}

/**
 * Finds all types that are a direct super type of the given type.
 * @param {import("typescript").TypeChecker} checker
 * @param {TsType} type 
 * @return {Set<import("typescript").Type>} 
 */
function getHeritageTypes(checker, type) {
    /** @type {Set<import("typescript").Type>} */
    const result = new Set();
    for (const declaration of getDeclarationsOfType(type)) {
        if (isTypeLike(declaration)) {
            for (const heritageNode of getHeritageNodes(checker, declaration)) {
                for (const interfaceType of getInterfaceTypes(checker.getTypeAtLocation(heritageNode))) {
                    result.add(interfaceType);
                }
            }
        }
    }
    return result;
}

/**
 * Given a node, finds its type and all node declarations for that type. Then return a list of nodes referred to by the
 * heritage clauses `extends` and `implements` of the all those nodes.
 * @param {import("typescript").TypeChecker} checker
 * @param {TypeLikeNode} node
 * @return {(TypeLikeNode)[]}
 */
function getAllHeritageNodes(checker, node) {
    return Array
        .from(getHeritageTypes(checker, checker.getTypeAtLocation(node)))
        .flatMap(getDeclarationsOfType)
        .filter(isTypeLike);
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {TypeLikeNode} node
 * @return {TypeLikeNode[]}
 */
function getAllHeritageNodesRecursive(checker, node) {
    return Array
        .from(getHeritageTypesRecursive(checker, checker.getTypeAtLocation(node)))
        .flatMap(getDeclarationsOfType)
        .filter(isTypeLike);
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {TypeLikeNode} node
 * @param {(method: TypeLikeNode) => boolean} predicate
 * @return {TypeLikeNode[]}
 */
function getAllHeritageNodesRecursiveUntilMatching(checker, node, predicate) {
    /** @type {TypeLikeNode[]} */
    const result = [];
    const stack = [checker.getTypeAtLocation(node)];
    const processed = new Set();
    for (let item = stack.pop(); item !== undefined; item = stack.pop()) {
        if (!processed.has(item)) {
            processed.add(item);
            for (const heritageType of getHeritageTypes(checker, item)) {
                const declarations = getDeclarationsOfType(heritageType).filter(isTypeLike);
                if (!declarations.some(predicate)) {
                    stack.push(heritageType);
                }
                result.push(...declarations);
            }
        }
    }
    return result;
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {CallOrMethodLikeNode} node
 * @return {TsOverridenMethodsResult}
 */
function getOverridenMethods(checker, node) {
    const classType = checker.getTypeAtLocation(node.parent);
    const propertyName = getPropertyNameOfCallLikeNode(node);
    return collectToMap(getHeritageTypes(checker, classType),
        type => type,
        type => getOwnMembersOfType(type).methods.get(propertyName) || [],
        {
            filter: (type, methods) => methods.length > 0,
        },
    );
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {CallOrMethodLikeNode} node 
 * @return {TsOverridenMethodsResult}
 */
function getOverridenMethodsRecursive(checker, node) {
    const classType = checker.getTypeAtLocation(node.parent);
    const propertyName = getPropertyNameOfCallLikeNode(node);
    return collectToMap(getHeritageTypesRecursive(checker, classType),
        type => type,
        type => getOwnMembersOfType(type).methods.get(propertyName) || [],
        {
            filter: (type, methods) => methods.length > 0,
        },
    );
}

/**
 * @param {import("typescript").TypeChecker} checker
 * @param {CallOrMethodLikeNode} node 
 * @param {(method: CallOrMethodLikeNode) => boolean} predicate
 * @return {TsOverridenMethodsResult}
 */
function getOverriddenMethodsRecursiveUntilMatching(checker, node, predicate) {
    const classType = checker.getTypeAtLocation(node.parent);
    const propertyName = getPropertyNameOfCallLikeNode(node);
    const stack = [classType];
    const processed = new Set();
    /** @type {TsOverridenMethodsResult} */
    const result = new Map();
    for (let item = stack.pop(); item !== undefined; item = stack.pop()) {
        if (!processed.has(item)) {
            processed.add(item);
            for (const heritageType of getHeritageTypes(checker, item)) {
                const methods = getOwnMembersOfType(heritageType).methods.get(propertyName);
                if (!methods || !methods.some(predicate)) {
                    stack.push(heritageType);
                }
                if (methods) {
                    const list = result.get(heritageType);
                    if (list) {
                        list.push(...methods);
                    }
                    else {
                        result.set(heritageType, methods);
                    }
                }
            }
        }
    }
    return result;
}

/**
 * @param {TsCreateSignatureLookupStackItem[]} stack 
 * @param {TsCallSignatureLookup} lookup
 */
function processBindingNames(stack, lookup) {
    for (let item = stack.pop(); item !== undefined; item = stack.pop()) {
        if (ts.isArrayBindingPattern(item.bindingPattern)) {
            for (let i = 0, len = item.bindingPattern.elements.length; i < len; ++i) {
                const arrayBindingElement = item.bindingPattern.elements[i];
                if (ts.isBindingElement(arrayBindingElement)) {
                    const indexPart = arrayBindingElement.dotDotDotToken ? `[...${i}]` : `[${i}]`;
                    if (ts.isIdentifier(arrayBindingElement.name)) {
                        const index = item.path.join(".") + "." + indexPart;
                        lookup.indexToName.set(index, arrayBindingElement.name.text);
                        lookup.nameToIndex.set(arrayBindingElement.name.text, index);
                    }
                    else {
                        stack.push({
                            bindingPattern: arrayBindingElement.name,
                            path: [...item.path, indexPart],
                        });
                    }
                }
            }
        }
        else {
            for (let i = 0, len = item.bindingPattern.elements.length; i < len; ++i) {
                const objectBindingElement = item.bindingPattern.elements[i];
                // { propertyName: name }
                const indexPart = objectBindingElement.propertyName ?
                    ts.isComputedPropertyName(objectBindingElement.propertyName) ?
                        String(objectBindingElement.propertyName.pos) :
                        getPropertyName(objectBindingElement.propertyName) :
                    undefined;
                if (ts.isIdentifier(objectBindingElement.name)) {
                    const name = indexPart ?
                        indexPart :
                        objectBindingElement.dotDotDotToken ? `[${i}]` : objectBindingElement.name.text;
                    const index = item.path.join(".") + "." + name;
                    lookup.indexToName.set(index, objectBindingElement.name.text);
                    lookup.nameToIndex.set(objectBindingElement.name.text, index);
                }
                else if (indexPart) {
                    stack.push({
                        bindingPattern: objectBindingElement.name,
                        path: [...item.path, indexPart],
                    });
                }
                // else: cannot happen as 'function foo({x, {y}}) {}' is not legal
            }
        }
    }
}

/**
 * Given a call signature, returns a mapping between the name of a parameter and its logical position in the call
 * signature. This is used to match signatures of overridden methods that may have different arbitrary parameter names.
 * For example, given the following function:
 * 
 * ```javascript
 * function test(x, y, [a1,,,,a2,...a], {o1,o2,o3:q1,9:q2,[1+2]:c1,...o}, [{w: {v: [m]}}], ...args) {}
 * ```
 * 
 * The resulting `indexToName` map is:
 * 
 * ```javascript
 * {
 *   "0": "x",
 *   "1": "y",
 *   "2": "param2",
 *   "3": "param3",
 *   "4": "param4",
 *   "...5": "args",
 *   "4.[0].w.v.[0]": "m",
 *   "3.o1": "o1",
 *   "3.o2": "o2",
 *   "3.o3": "q1",
 *   "3.9": "q2",
 *   "3.o": "o",
 *   "2.[0]": "a1",
 *   "2.[4]": "a2",
 *   "2.[...5]": "a",
 * }
 * ```
 * @param {import("typescript").SignatureDeclaration} callSignature
 * @return {TsCallSignatureLookup}
 */
function getCallSignatureLookup(callSignature) {
    /** @type {TsCallSignatureLookup} */
    const lookup = {
        indexToName: new Map(),
        nameToIndex: new Map(),
    };
    /** @type {TsCreateSignatureLookupStackItem[]} */
    const stack = [];
    callSignature.parameters.forEach((param, i) => {
        if (ts.isIdentifier(param.name)) {
            // Basic parameter, possibly rest
            const index = param.dotDotDotToken ? `...${i}` : String(i);
            lookup.indexToName.set(index, param.name.text);
            lookup.nameToIndex.set(param.name.text, index);
        }
        else {
            // Destructured parameter
            const name = `param${i}`;
            const index = String(i);
            lookup.indexToName.set(index, name);
            lookup.nameToIndex.set(name, index);
            stack.push({
                bindingPattern: param.name,
                path: [index],
            });
        }
    });
    processBindingNames(stack, lookup);
    return lookup;
}

/**
 * Fixes nodes:
 * - Fills the `parent` property for all nodes.
 * - Overwrites the `toString` method with a more sensible default.
 * @param {import("typescript").TypeChecker} typeChecker
 * @param {TsNode} rootNode 
 * @param {import("typescript").SourceFile} sourceFile 
 */
function fixupNodes(typeChecker, rootNode, sourceFile = rootNode.getSourceFile()) {
    const stack = [rootNode];
    for (let node = stack.pop(); node !== undefined; node = stack.pop()) {
        if (!isTokenNode(node)) {
            for (let i = node.getChildCount(sourceFile) - 1; i >= 0; --i) {
                const child = node.getChildAt(i, sourceFile);
                if (child.parent === undefined) {
                    /** @type {any} */
                    const c = child;
                    c.parent = node;
                }
                stack.push(child);
            }
        }
    }
    depthFirstNode(rootNode, node => {
        if (node.parent) {
            try {
                const type = typeChecker.getTypeAtLocation(node);
                node.toString = curry1(getNameOfType, type);
            }
            catch (e) {
                node.toString = () => "<unknown>";
            }
        }
        else {
            node.toString = () => "<root>";
        }
        return true;
    });
}

module.exports = {
    depthFirstNode,

    getAllHeritageNodes,
    getAllHeritageNodesRecursive,
    getAllHeritageNodesRecursiveUntilMatching,
    getBaseTypes,
    getBaseTypesRecursive,
    getCallSignatureLookup,
    getDeclarationsOfType,
    getHeritageNodes,
    getHeritageNodesRecursive,
    getHeritageTypes,
    getHeritageTypesRecursive,
    getLocalTypeParameterNames,
    getNameOfType,
    getOwnMembersOfNode,
    getOwnMembersOfType,
    getOverridenMethods,
    getOverridenMethodsRecursive,
    getOverriddenMethodsRecursiveUntilMatching,
    getPropertyName,
    getPropertyNameOfCallLikeNode,
    getSymbolName,
    getTypeName,
    getTypeParameterNames,

    hasOneOrMoreFlag,
    hasAllFlags,
    hasNeitherFlag,

    fixupNodes,

    hasBaseTypes,
    hasCallSignature,
    hasHeritageTypes,

    isAnyType,
    isBooleanType,
    isCallLike,
    isCallOrMethodLike,
    isConstructorLike,
    isMethodLike,
    isMethodOverriding,
    isNeverType,
    isNonComputedPropertyName,
    isNullType,
    isNumberType,
    isObjectType,
    isPropertyLike,
    isStringType,
    isTokenNode,
    isTypeLike,
    isTypeReference,
    isUndefinedType,
    isUnknownType,
    isVoidType,
};