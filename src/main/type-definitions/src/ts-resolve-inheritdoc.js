//@ts-check

const { TopologicalSort } = require("topological-sort");
const ts = require("typescript");

const { newTsErrorMessage } = require("./error");

const { FlaggingTags, Tags, UniqueDescriptionTags, UniqueNameTags, UniqueTags } = require("./constants");
const { hasTag, createTag, getTagDescriptionWithName, removeTags } = require("./doc-comments");
const { checkTagHasDescription, checkSymbolHasDescription } = require("./doc-comment-check-tags");
const { compareBy, collectToMap, isNotUndefined, pushToMappedArray, reduceIntoFirstArray, singletonArray } = require("./lang");
const tsx = require("./ts-utils");

/**
 * @param {string} baseDesc 
 * @param {string} parentDesc 
 * @param {string} parentTypeName
 * @return {string}
 */
function mergeDescription(baseDesc, parentDesc, parentTypeName) {
    const baseDescTrimmed = baseDesc.trim();
    const hasBaseDesc = baseDescTrimmed.length > 0;
    if (hasBaseDesc) {
        return `${baseDescTrimmed}\n\n(from super type ${parentTypeName}) ${parentDesc.trim()}`;
    }
    else {
        return parentDesc.trim();
    }
}

/**
 * Merges several tags into one. They must all have the same tag name.
 * @param {string} parentTypeName
 * @param {boolean} nameBelongsToDescription
 * @param  {import("comment-parser").Tag[]} tags 
 * @return {import("comment-parser").Tag | undefined}
 */
function mergeTagsToOne(parentTypeName, nameBelongsToDescription, ...tags) {
    if (tags.length === 0) {
        return undefined;
    }

    let tagName = "";
    let description = "";
    let defaultValue = "";
    let isOptional = false;
    for (const tag of tags) {
        const tagDesc = nameBelongsToDescription && tag.name ? tag.name + " " + tag.description : tag.description;
        tagName = nameBelongsToDescription ? tagName : tagName || tag.name;
        description = mergeDescription(description, tagDesc, parentTypeName);
        defaultValue = defaultValue ? defaultValue : tag.default || "";
        isOptional = isOptional ? isOptional : tag.optional;
    }

    const tagKind = tags[0].tag;
    const newTag = createTag(tagKind, {
        default: defaultValue,
        description: description,
        name: tagName,
        optional: isOptional,
    });
    return newTag;
}

/**
 * @param {import("comment-parser").Tag[]} tags
 * @return {ResolveInheritDocTagCollection}
 */
function collectTags(tags) {
    /** @type {ResolveInheritDocTagCollection} */
    const result = {
        flagging: [],
        others: [],
        params: new Map(),
        returns: [],
        templates: [],
        typeparams: [],
        unique: new Map(),
        uniqueName: new Map(),
        uniqueDescription: new Map(),
    }
    for (const tag of tags) {
        switch (tag.tag) {
            case Tags.Return:
            case Tags.Returns:
                result.returns.push(tag);
                break;
            case Tags.Param:
                result.params.set(tag.name, tag);
                break;
            case Tags.Typeparam: {
                result.typeparams.push(tag);
                break;
            }
            case Tags.Template: {
                result.templates.push(tag);
                break;
            }
            default:
                if (FlaggingTags.has(tag.tag)) {
                    result.flagging.push(tag);
                }
                else if (UniqueTags.has(tag.tag)) {
                    pushToMappedArray(result.unique, tag.tag, tag);
                }
                else if (UniqueNameTags.has(tag.tag)) {
                    pushToMappedArray(result.uniqueName, tag.tag, tag);
                }
                else if (UniqueDescriptionTags.has(tag.tag)) {
                    pushToMappedArray(result.uniqueDescription, tag.tag, getTagDescriptionWithName(tag));
                }
                else {
                    result.others.push(tag);
                }
        }
    }
    return result;
}

/**
 * @param {string} parentTypeName
 * @param {(name: string) => boolean} nameFilter Include only the matching names
 * @param {import("comment-parser").Tag[]} tags
 * @return {import("comment-parser").Tag[]}
 */
function mergeTagsByName(parentTypeName, nameFilter, ...tags) {
    const byName = collectToMap(
        tags,
        tag => tag.name || "",
        tag => [tag],
        {
            filter: nameFilter,
            reducer: reduceIntoFirstArray,
        }
    );
    return Array.from(
        byName,
        entry => mergeTagsToOne(parentTypeName, false, ...entry[1]),
    ).filter(isNotUndefined);
}

/**
 * @param {Map<string, import("comment-parser").Tag>} parentParamTags
 * @param {TsCallSignatureLookup} baseSignature 
 * @param {CallOrMethodLikeNode} parentNode 
 * @return {(name: string) => import("comment-parser").Tag | undefined}
 */
function createMatchingTagFinder(parentParamTags, baseSignature, parentNode) {
    const parentSignature = tsx.getCallSignatureLookup(parentNode);
    return name => {
        const index = baseSignature.nameToIndex.get(name);
        if (index) {
            const matchingName = parentSignature.indexToName.get(index);
            if (matchingName) {
                return parentParamTags.get(matchingName);
            }
        }
        return undefined;
    }
}

/**
 * @param {string} parentTypeName
 * @param {Map<string, import("comment-parser").Tag>} baseParamTags
 * @param {Map<string, import("comment-parser").Tag>} parentParamTags
 * @param {CallOrMethodLikeNode} baseNode 
 * @param {CallOrMethodLikeNode} parentNode 
 * @return {import("comment-parser").Tag[]}
 */
function mergeParamTags(parentTypeName, baseParamTags, parentParamTags, baseNode, parentNode) {
    const baseSignature = tsx.getCallSignatureLookup(baseNode);
    const findTag = createMatchingTagFinder(parentParamTags, baseSignature, parentNode);
    /** @type {import("comment-parser").Tag[]} */
    const result = [];
    for (const baseParamName of baseSignature.indexToName.values()) {
        const baseParamTag = baseParamTags.get(baseParamName);
        const parentParamTag = findTag(baseParamName);
        const merged = mergeTagsToOne(parentTypeName, false, ...[baseParamTag, parentParamTag].filter(isNotUndefined));
        if (merged) {
            merged.name = baseParamName;
            result.push(merged);
        }
    }
    return result;
}

/**
 * @param {string} parentTypeName 
 * @param {Map<string, import("comment-parser").Tag[]>[]} tagsByKind 
 * @return {import("comment-parser").Tag[]}
 */
function mergeUniqueTags(parentTypeName, ...tagsByKind) {
    /** @type {Set<string>} */
    const tagKinds = tagsByKind.length > 0 ? new Set(tagsByKind[0].keys()) : new Set();
    const uniqueTags = Array.from(
        tagKinds,
        tagKind => {
            const uniqueTags = [];
            for (const tagByKind of tagsByKind) {
                const tags = tagByKind.get(tagKind);
                if (tags) {
                    uniqueTags.push(...tags);
                }
            }
            return mergeTagsToOne(parentTypeName, true, ...uniqueTags);
        }
    ).filter(isNotUndefined);
    return uniqueTags;
}

/**
 * @param {Map<string, string[]>[]} descriptionsByKind
 * @return {import("comment-parser").Tag[]}
 */
function mergeUniqueDescTags(...descriptionsByKind) {
    /** @type {Set<string>} */
    const tagKinds = descriptionsByKind.length > 0 ? new Set(descriptionsByKind[0].keys()) : new Set();
    /** @type {import("comment-parser").Tag[]} */
    const result = [];
    for (const tagKind of tagKinds) {
        /** @type {Set<string>} */
        const uniqueDescriptions = new Set();
        for (const descriptionByKind of descriptionsByKind) {
            const descriptions = descriptionByKind.get(tagKind);
            if (descriptions) {
                for (const description of descriptions) {
                    uniqueDescriptions.add(description);
                }
            }
        }
        for (const desc of uniqueDescriptions) {
            result.push(createTag(tagKind, {
                description: desc,
            }));
        }
    };
    return result;
}

/**
 * @param {string} parentTypeName 
 * @param {Map<string, import("comment-parser").Tag[]>[]} tagsByKind 
 * @return {import("comment-parser").Tag[]}
 */
function mergeUniqueNameTags(parentTypeName, ...tagsByKind) {
    /** @type {Set<string>} */
    const tagKinds = tagsByKind.length > 0 ? new Set(tagsByKind[0].keys()) : new Set();
    const uniqueNameTags = Array.from(tagKinds)
        .flatMap(tagKind => {
            const uniqueTags = [];
            for (const tagByKind of tagsByKind) {
                const tags = tagByKind.get(tagKind);
                if (tags) {
                    uniqueTags.push(...tags);
                }
            }
            return mergeTagsByName(parentTypeName, () => true, ...uniqueTags);
        })
        .filter(isNotUndefined);
    return uniqueNameTags;
}

/**
 * @param {MergeDocParams} params 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {import("comment-parser").Comment} 
 */
function mergeDoc(params, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newTsErrorMessage(message, params.baseNode)

    // description
    const mergedDescription = mergeDescription(params.baseDoc.description, params.parentDoc.description, params.parentTypeName);

    // tags
    const baseTags = collectTags(params.baseDoc.tags);
    const parentTags = collectTags(params.parentDoc.tags);

    // @return
    const returnTag = mergeTagsToOne(params.parentTypeName, true, ...baseTags.returns, ...parentTags.returns);

    // @typeparam / @template
    const typeParamNames = tsx.getTypeParameterNames(params.baseNode);
    const typeparamTags = mergeTagsByName(
        params.parentTypeName,
        name => typeParamNames.has(name),
        ...baseTags.typeparams, ...parentTags.typeparams
    );
    const templateTags = mergeTagsByName(
        params.parentTypeName,
        name => typeParamNames.has(name),
        ...baseTags.templates, ...parentTags.templates
    );

    // @param
    const paramTags = params.kind === "method" ?
        mergeParamTags(params.parentTypeName, baseTags.params, parentTags.params, params.baseNode, params.parentNode) :
        [];

    // unique tags
    const uniqueTags = mergeUniqueTags(params.parentTypeName, baseTags.unique, parentTags.unique);

    // unique name tags
    const uniqueNameTags = mergeUniqueNameTags(params.parentTypeName, baseTags.uniqueName, parentTags.uniqueName);

    // uniqueDescriptionTags
    const uniqueDescriptionTags = mergeUniqueDescTags(baseTags.uniqueDescription, parentTags.uniqueDescription);

    // Check whether tags have got a description
    checkSymbolHasDescription(mergedDescription, factory, severitySettings, [], "method");
    for (const tag of [returnTag, ...typeparamTags, ...templateTags, ...paramTags]) {
        if (tag) {
            checkTagHasDescription(tag, severitySettings, factory, [], tag.tag !== Tags.Return);
        }
    }

    return {
        description: mergedDescription,
        line: 0,
        source: "",
        tags: [
            ...baseTags.flagging,
            ...uniqueTags,
            ...typeparamTags,
            ...paramTags,
            ...templateTags,
            ...singletonArray(returnTag),
            ...uniqueNameTags,
            ...uniqueDescriptionTags,
            ...baseTags.others,
            ...parentTags.others,
        ],
    };
}

/**
 * @param {Map<TypeLikeNode, Set<TypeLikeNode>>} edges
 * @param {TypeLikeNode} from 
 * @param {TypeLikeNode} to 
 */
function addEdge(edges, from, to) {
    const successors = edges.get(from);
    if (successors !== undefined) {
        successors.add(to);
    }
    else {
        edges.set(from, new Set([to]));
    }
}

/**
 * @param {ResolveInheritDocState} state
 * @param {TypeLikeNode} node 
 * @param {SeveritySettingsConfig} severitySettings
 */
function resolveType(state, node, severitySettings) {
    let mergedDoc = state.docCommentAccessor.getClosestDocComment(node);
    if (mergedDoc && hasTag(mergedDoc, Tags.InheritDoc)) {
        const heritageNodes = tsx
            .getAllHeritageNodesRecursiveUntilMatching(state.typeChecker, node, state.docCommentAccessor.hasDocComment)
            .sort(compareBy(tsx.getTypeName));
        for (const heritageNode of heritageNodes) {
            const parentDoc = state.docCommentAccessor.getClosestDocComment(heritageNode);
            // merge parent doc in jsdoc
            if (parentDoc) {
                const parentType = state.typeChecker.getTypeAtLocation(heritageNode);
                const parentTypeName = tsx.getNameOfType(parentType);
                mergedDoc = mergeDoc({
                    baseDoc: mergedDoc,
                    baseNode: node,
                    kind: "type",
                    parentDoc: parentDoc,
                    parentNode: heritageNode,
                    parentTypeName,
                }, severitySettings);
            }
        }
        removeTags(mergedDoc, Tags.InheritDoc);
        state.docCommentAccessor.replaceClosestDocComment(node, mergedDoc);
    }
}

/**
 * @param {ResolveInheritDocState} state 
 * @param {TypeLikeNode} node 
 * @param {SeveritySettingsConfig} severitySettings
 */
function resolveMethods(state, node, severitySettings) {
    for (const methodNodes of tsx.getOwnMembersOfNode(node).methods.values()) {
        for (const methodNode of methodNodes) {
            let mergedDoc = state.docCommentAccessor.getClosestDocComment(methodNode);
            if (mergedDoc && hasTag(mergedDoc, Tags.InheritDoc)) {
                const parentMethods = Array
                    .from(tsx.getOverriddenMethodsRecursiveUntilMatching(state.typeChecker, methodNode, state.docCommentAccessor.hasDocComment))
                    .sort(compareBy(entry => tsx.getNameOfType(entry[0])));
                for (const [parentClassType, parentMethodNodes] of parentMethods) {
                    const parentTypeName = tsx.getNameOfType(parentClassType);
                    for (const parentMethodNode of parentMethodNodes) {
                        const parentDoc = state.docCommentAccessor.getClosestDocComment(parentMethodNode);
                        if (parentDoc) {
                            mergedDoc = mergeDoc({
                                baseDoc: mergedDoc,
                                baseNode: methodNode,
                                kind: "method",
                                parentDoc: parentDoc,
                                parentNode: parentMethodNode,
                                parentTypeName: parentTypeName,
                            }, severitySettings);
                        }
                    }
                }
                removeTags(mergedDoc, Tags.InheritDoc);
                state.docCommentAccessor.replaceClosestDocComment(methodNode, mergedDoc);
            }
        }
    }
}

/**
 * @param {ResolveInheritDocState} state
 * @param {import("typescript").ClassLikeDeclaration | import("typescript").InterfaceDeclaration} node 
 */
function handleType(state, node) {
    const jsdoc = state.docCommentAccessor.getClosestDocComment(node);
    if (jsdoc && hasTag(jsdoc, Tags.InheritDoc)) {
        // resolve parent types first
        state.typeNodes.set(node, node);
        for (const heritageNode of tsx.getAllHeritageNodes(state.typeChecker, node)) {
            state.typeNodes.set(heritageNode, heritageNode);
            addEdge(state.typeEdges, heritageNode, node);
        }
    }
}

/**
 * @param {ResolveInheritDocState} state
 * @param {import("typescript").MethodDeclaration | ts.MethodSignature} node 
 */
function handleMethod(state, node) {
    const baseDoc = state.docCommentAccessor.getClosestDocComment(node);
    if (baseDoc && hasTag(baseDoc, Tags.InheritDoc)) {
        if (tsx.isTypeLike(node.parent)) {
            state.methodNodes.set(node.parent, node.parent);
            for (const heritageNode of tsx.getAllHeritageNodes(state.typeChecker, node.parent)) {
                state.methodNodes.set(heritageNode, heritageNode);
                addEdge(state.methodEdges, heritageNode, node.parent);
            }
        }
    }
}

/**
 * @param {ResolveInheritDocState} state
 * @param {ts.Node} node 
 * @return {boolean}
 */
function processNode(state, node) {
    if (tsx.isTypeLike(node)) {
        handleType(state, node);
    }
    else if (tsx.isMethodLike(node)) {
        handleMethod(state, node);
    }
    return true;
}

/**
 * @template K
 * @template V
 * @param {Map<K, V>} nodes 
 * @param {Map<K, Set<K>>} edges
 * @return {Map<K, import("topological-sort").INodeWithChildren<K, V>>}
 */
function sortTypes(nodes, edges) {
    const typeTree = new TopologicalSort(nodes);
    for (const [from, toList] of edges) {
        for (const to of toList) {
            typeTree.addEdge(from, to);
        }
    }
    try {
        return typeTree.sort();
    }
    catch (e) {
        const message = e instanceof Error ? e.stack || e.message : String(e);
        throw new Error("Could not sort type inheritance graph\n" + message);
    }
}

/**
 * @type {TsHookFnProcessAst}
 */
const resolveInheritDocs = (program, sourceFiles, docCommentAccessor, severitySettings) => {
    /** @type {ResolveInheritDocState} */
    const state = {
        docCommentAccessor: docCommentAccessor,
        methodEdges: new Map(),
        methodNodes: new Map(),
        program: program,
        typeChecker: program.getTypeChecker(),
        typeEdges: new Map(),
        typeNodes: new Map(),
    };
    for (const sourceFile of sourceFiles) {
        tsx.depthFirstNode(sourceFile, node => processNode(state, node));
    }
    for (const item of sortTypes(state.typeNodes, state.typeEdges).values()) {
        resolveType(state, item.node, severitySettings);
    }
    for (const item of sortTypes(state.methodNodes, state.methodEdges).values()) {
        resolveMethods(state, item.node, severitySettings);
    }
};

module.exports = {
    resolveInheritDocs,
}