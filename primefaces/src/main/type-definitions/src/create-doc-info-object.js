//@ts-check

const { Tags } = require("./constants");
const { createTag, shiftName } = require("./doc-comments");
const { checkTagHasDescription, checkSymbolHasDescription, checkTagHasNoDescription, checkTagHasNoType, checkTagHasType, checkTagIsPlain } = require("./doc-comment-check-tags");
const { handleError, newNodeErrorMessage } = require("./error");
const { NativeInsertionOrderMap } = require("./InsertionOrderMap");
const { createTypedefTagHandler } = require("./create-doc-info-typedef");

/**
 * @param {import("estree").Node} node
 * @param {import("comment-parser").Comment} jsdoc 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ObjectDocInfo}
 */
function createObjectDocInfo(jsdoc, node, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newNodeErrorMessage(message, node);

    checkSymbolHasDescription(jsdoc, factory, severitySettings, jsdoc.tags, `object`);

    /** @type {ObjectDocShape} */
    const root = {
        abstract: false,
        additionalTags: [],
        constants: [],
        export: {
            description: "",
            name: "",
            type: "unspecified",
        },
        extends: new Set(),
        implements: new Set(),
        jsdoc: undefined,
        method: undefined,
        name: "",
        optional: false,
        props: new Map(),
        readonly: false,
        templates: new NativeInsertionOrderMap(),
        visibility: undefined,
    };

    /** @type {DocInfoTypedef[]} */
    const typedefs = [];

    /** @type {ObjectDocInfo} */
    const result = {
        description: jsdoc.description || "",
        shape: root,
        typedefs: typedefs,
    };

    const typedefTagHandler = createTypedefTagHandler(node, severitySettings, typedefs);

    /** @type {string[][]} */
    const modifierPaths = [];

    for (const oldTag of jsdoc.tags) {
        if (oldTag) {
            const oldName = oldTag.name;
            const isDot = oldTag.name === ".";
            const parts = isDot || oldTag.name === "" ? [] : oldTag.name.split(".");
            //const level = isDot ? 0 : parts.length;
            let name = parts.length > 0 ? parts[parts.length - 1] : "";
            const tag = createTag(oldTag.tag, oldTag);
            switch (tag.tag) {
                // Handle top-level object tags

                // @interface {ConstructorOptions}
                // @class {ConstructorOptions}
                case Tags.Class:
                case Tags.Interface:
                case Tags.Namespace: {
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    if (nested.export.type !== "unspecified") {
                        handleError("tagDuplicateClassOrInterface", severitySettings, () => `Found duplicate tag '@interface','@class', or '@namespace' in doc comments`);
                    }
                    else {
                        switch (tag.tag) {
                            case Tags.Class: nested.export.type = "class"; break;
                            case Tags.Interface: nested.export.type = "interface"; break;
                            case Tags.Namespace: nested.export.type = "namespace"; break;
                            default: throw new Error("Unhandled tag: " + tag.tag);
                        }
                        nested.export.name = tag.type && tag.type !== "*" ? tag.type : "";
                        nested.export.description = tag.description || "";
                    }
                    break;
                }

                // @const {PrimeFaces.ajax} Access to the AJAX object
                case Tags.Const:
                case Tags.Constant: {
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    const type = checkTagHasType(tag, severitySettings, factory);
                    const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                    if (type) {
                        nested.constants.push({
                            description: desc || "",
                            name: tag.type,
                        });
                    }
                    break;
                }

                // @extends {BaseClass}
                case Tags.Extends: {
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    checkTagHasNoDescription(tag, severitySettings, factory, true);
                    const type = checkTagHasType(tag, severitySettings, factory);
                    if (type !== "") {
                        nested.extends.add(type);
                    }
                    break;
                }

                // @implements {IDoable}
                case Tags.Implements: {
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    checkTagHasNoDescription(tag, severitySettings, factory, true);
                    const type = checkTagHasType(tag, severitySettings, factory);
                    if (type !== "") {
                        if (nested.implements.has(type)) {
                            handleError("tagDuplicateImplements", severitySettings, () => factory(`Found duplicate tag '@implements ${oldName}' in doc comments`));
                        }
                        nested.implements.add(type);
                    }
                    break;
                }

                // Handle methods

                case Tags.Constructor: {
                    if (!typedefTagHandler._constructor(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.constructor) {
                            handleError("tagDuplicateConstructor", severitySettings, () => `Found duplicate tag '@constructor ${tag.name}' in doc comments`);
                        }
                        tag.name = "";
                        checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                        method.constructor = true;
                    }
                    break;
                }

                // @method settings.show Shows the element
                // @constructor settings Creates a new settings instance
                case Tags.Method: {
                    const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                    if (method.method !== undefined) {
                        handleError("tagDuplicateMethod", severitySettings, () => factory(`Found duplicate tag '@method ${tag.name}' in doc comments`));
                    }
                    else {
                        tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                        checkTagHasNoType(tag, severitySettings, factory);
                        tag.name = "";
                        method.name = name;
                        method.method = tag;
                    }
                    break;
                }

                // @async settings.show
                case Tags.Async: {
                    if (!typedefTagHandler.async(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.async) {
                            handleError("tagDuplicateAsync", severitySettings, () => `Found duplicate tag '@async ${tag.name}' in doc comments`);
                        }
                        tag.name = "";
                        checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                        method.async = true;
                    }
                    break;
                }

                // @generator settings.show
                case Tags.Generator: {
                    if (!typedefTagHandler.generator(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.generator) {
                            handleError("tagDuplicateGenerator", severitySettings, () => `Found duplicate tag '@generator ${tag.name}' in doc comments`);
                        }
                        tag.name = "";
                        checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                        method.generator = true;
                    }
                    break;
                }

                // @return {boolean} settings.show The return value
                case Tags.Return:
                case Tags.Returns: {
                    if (!typedefTagHandler.return(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.return !== undefined) {
                            handleError("tagDuplicateReturn", severitySettings, () => factory(`Found duplicate tag '@return(s) ${tag.name}' in doc comments`));
                        }
                        else {
                            tag.type = checkTagHasType(tag, severitySettings, factory);
                            tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                            tag.name = "";
                            method.return = tag;
                        }
                    }
                    break;
                }

                // @yield {boolean} settings.show The yield value
                case Tags.Yield:
                case Tags.Yields: {
                    if (!typedefTagHandler.yield(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.yield !== undefined) {
                            handleError("tagDuplicateYield", severitySettings, () => factory(`Found duplicate tag '@yield ${tag.name}' in doc comments`));
                        }
                        else {
                            tag.type = checkTagHasType(tag, severitySettings, factory);
                            tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                            tag.name = "";
                            method.yield = tag;
                        }
                    }
                    break;
                }

                // @next {boolean} settings.show The next value
                case Tags.Next: {
                    if (!typedefTagHandler.next(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.next !== undefined) {
                            handleError("tagDuplicateNext", severitySettings, () => factory(`Found duplicate tag '@next ${tag.name}' in doc comments`));
                        }
                        else {
                            tag.type = checkTagHasType(tag, severitySettings, factory);
                            tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                            tag.name = "";
                            method.next = tag;
                        }
                    }
                    break;
                }

                // @this {string}
                case Tags.This: {
                    if (!typedefTagHandler.this(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, 0);
                        if (method.thisTypedef) {
                            handleError("tagDuplicateThis", severitySettings, () => factory(`Found this tag '@param ${tag.name}' in doc comments`))
                        }
                        else {
                            const type = checkTagHasType(tag, severitySettings, factory);
                            checkTagHasNoDescription(tag, severitySettings, factory, true);
                            method.thisTypedef = type;
                        }
                    }
                    break;
                }


                // @param {boolean} settings.show.force Whether to force show
                case Tags.Param: {
                    if (!typedefTagHandler.params(tag, jsdoc.tags, false)) {
                        const method = getOrCreateMethodAtNestingLevel(node, root, parts, -1);
                        if (method.params.has(name)) {
                            handleError("tagDuplicateParameter", severitySettings, () => factory(`Found duplicate tag '@param ${tag.name}' in doc comments`));
                        }
                        else {
                            // No type required in case of destructuring
                            tag.type = tag.type && tag.type !== "*" ? tag.type : "",
                                tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                            tag.name = name;
                            method.params.set(name, tag);
                        }
                    }
                    break;
                }

                // @pattern {Vector3} 1
                // @structure {{x,y}} 1
                case Tags.Pattern:
                case Tags.Structure: {
                    if (!typedefTagHandler.structureOrPattern(tag, jsdoc.tags, false)) {
                        const index = parseInt(name);
                        if (isNaN(index) || index < 0) {
                            handleError("tagNameInvalidIndex", severitySettings, () => factory(`'@${tag.tag}' must specify an index that is a number greater than or equal to 0, eg. "@${tag.tag} {...} 0"`));
                        }
                        else {
                            const destructuring = getOrCreateDestructuringAtNestingLevel(node, root, parts, index);
                            if (tag.tag === Tags.Structure) {
                                if (destructuring.structure !== undefined) {
                                    handleError("tagDuplicateStructure", severitySettings, () => factory(`Found duplicate tag '@structure ${tag.name}' in doc comments`));
                                }
                                else {
                                    checkTagHasNoDescription(tag, severitySettings, factory, true);
                                    tag.description = "";
                                    tag.name = name;
                                    destructuring.structure = tag;
                                }
                            }
                            else if (tag.tag === Tags.Pattern) {
                                if (destructuring.pattern !== undefined) {
                                    handleError("tagDuplicatePattern", severitySettings, () => factory(`Found duplicate tag '@pattern ${tag.name}' in doc comments`));
                                }
                                else {
                                    tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                                    tag.name = name;
                                    destructuring.pattern = tag;
                                }
                            }
                        }
                    }
                    break;
                }

                case Tags.Methodtemplate: {
                    typedefTagHandler.methodtemplate(tag, jsdoc.tags, true);
                    break;
                }

                // @template {keyof Foo} settings.show.K Type of keys
                // @template {T} cfg
                case Tags.Template: {
                    if (!typedefTagHandler.template(tag, jsdoc.tags, false)) {
                        const nested = getOrCreateNestedObjectDocShape(root, parts, -1);
                        if (nested.templates.has(name)) {
                            handleError("tagDuplicateTemplate", severitySettings, () => factory(`Found duplicate tag '@template ${tag.name}' in doc comments`))
                        }
                        else {
                            tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                            tag.name = name;
                            nested.templates.set(name, tag);
                        }
                    }
                    break;
                }

                // Handle properties

                // @prop {boolean} cfg.enabled Whether it is enabled
                case Tags.Prop:
                case Tags.Property: {
                    tag.description = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                    tag.name = name;
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    nested.jsdoc = tag;
                    if (tag.optional) {
                        if (nested.optional) {
                            handleError("tagDuplicateDefault", severitySettings, () => factory(`Property was already specified as optional by a '@default ${tag.name}' tag`));
                        }
                        else {
                            nested.optional = tag.optional;
                        }
                    }
                    break;
                }

                // @private / @protected / @public
                case Tags.Private:
                case Tags.Protected:
                case Tags.Public: {
                    tag.name = "";
                    checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    if (nested.visibility) {
                        handleError("tagDuplicateVisibilityModifier", severitySettings, () => `Found duplicate tag '@${tag.tag} ${tag.name}' in doc comments`);
                    }
                    modifierPaths.push(parts);
                    nested.visibility = tag.tag === Tags.Private ? "private" : tag.tag === Tags.Protected ? "protected" : "public";
                    break;
                }

                // @abstract foo.mymethod
                case Tags.Abstract: {
                    tag.name = "";
                    checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    if (nested.abstract) {
                        handleError("tagDuplicateAbstract", severitySettings, () => factory(`Duplicate '@abstract ${tag.name}' found in doc comments`));
                    }
                    modifierPaths.push(parts);
                    nested.abstract = true;
                    break;
                }

                // @readonly cfg.enabled
                case Tags.Readonly: {
                    tag.name = "";
                    checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    if (nested.readonly) {
                        handleError("tagDuplicateReadonly", severitySettings, () => factory(`Duplicate '@readonly ${tag.name}' found in doc comments`));
                    }
                    nested.readonly = true;
                    break;
                }

                // @default cfg.enabled
                case Tags.Default: {
                    tag.name = "";
                    checkTagIsPlain(tag, severitySettings, factory, { checkName: false });
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    if (nested.optional) {
                        handleError("tagDuplicateDefault", severitySettings, () => factory(`Duplicate '@default ${tag.name}' found in doc comments, or property was already specified as optional`));
                    }
                    nested.optional = true;
                    break;
                }

                // Typedefs
                case Tags.Typedef: {
                    if (oldName.length > 0) {
                        typedefs.push({
                            description: checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true),
                            function: undefined,
                            name: oldName,
                            node: node,
                            templates: new Map(),
                            typedef: tag.type,
                        });
                    }
                    break;
                }
                // Other tags
                default: {
                    const nested = getOrCreateNestedObjectDocShape(root, parts, 0);
                    nested.additionalTags.push(shiftName(tag));
                }
            }
        }
    }
    processAbstractModifierForMethods(result.shape, modifierPaths);
    return result;
}

/**
 * @param {ObjectDocShape} root 
 * @param {string[][]} abstractModifierPaths
 */
function processAbstractModifierForMethods(root, abstractModifierPaths) {
    for (const path of abstractModifierPaths) {
        const object = getNestedObjectDocShape(root, path, 0);
        if (object && object.method) {
            object.method.abstract = object.abstract;
            object.method.visibility = object.visibility;
        }
    }
}

/**
 * @param {import("estree").Node} node
 * @param {ObjectDocShape} current Object shape to start at.
 * @param {string[]} path Path to access.
 * @param {number} offset 0 for tags such as `method` and `return`, `-1` for tags such as `param` and `pattern`.
 * @return {MethodDocShape} The method  shape at the given path.
 */
function getOrCreateMethodAtNestingLevel(node, current, path, offset) {
    const actualPath = offset === 0 ? path : path.slice(0, offset);
    const nested = getOrCreateNestedObjectDocShape(current, actualPath, 0);
    if (nested.method === undefined) {
        nested.method = {
            abstract: false,
            async: false,
            constructor: false,
            destructuring: new Map(),
            generator: false,
            method: undefined,
            name: "",
            next: undefined,
            node: node,
            params: new NativeInsertionOrderMap(),
            return: undefined,
            templates: new Map(),
            thisTypedef: "",
            yield: undefined,
            visibility: undefined,
        };
    }
    return nested.method;
}

/**
 * @param {ObjectDocShape} current Object shape to start at.
 * @param {string[]} path Path to access.
 * @param {number} offset 0 for tags such as `method` and `return`, `-1` for tags such as `template`.
 * @return {ObjectDocShape} The object shape at the given path.
 */
function getOrCreateNestedObjectDocShape(current, path, offset) {
    const actualPath = offset === 0 ? path : path.slice(0, offset);
    for (let i = 0; i < actualPath.length; ++i) {
        let sub = current.props.get(actualPath[i]);
        if (sub === undefined) {
            sub = {
                abstract: false,
                additionalTags: [],
                constants: [],
                export: {
                    description: "",
                    name: "",
                    type: "unspecified",
                },
                extends: new Set(),
                implements: new Set(),
                jsdoc: undefined,
                method: undefined,
                name: actualPath[i],
                optional: false,
                props: new Map(),
                readonly: false,
                templates: new NativeInsertionOrderMap(),
                visibility: undefined,
            };
            current.props.set(actualPath[i], sub);
        }
        current = sub;
    }
    return current;
}

/**
 * @param {ObjectDocShape} current Object shape to start at.
 * @param {string[]} path Path to access.
 * @param {number} offset 0 for tags such as `method` and `return`, `-1` for tags such as `template`.
 * @return {ObjectDocShape | undefined} The object shape at the given path if it exists.
 */
function getNestedObjectDocShape(current, path, offset) {
    const actualPath = offset === 0 ? path : path.slice(0, offset);
    for (let i = 0; i < actualPath.length; ++i) {
        let sub = current.props.get(actualPath[i]);
        if (sub === undefined) {
            return undefined;
        }
        current = sub;
    }
    return current;
}

/**
 * @param {Pick<MethodDocShape, "destructuring">} method
 * @param {DestructuringInfo | undefined} destructuring
 * @param {number} index Index of the pattern or structure tag.
 * @return {DestructuringInfo} The destructuring info at the given path.
 */
function getOrCreateDestructuring(method, destructuring, index) {
    if (destructuring === undefined) {
        destructuring = {
            pattern: undefined,
            structure: undefined,
        }
        method.destructuring.set(index, destructuring);
        return destructuring;
    }
    return destructuring;
}

/**
 * @param {import("estree").Node} node
 * @param {ObjectDocShape} current Object shape to start at.
 * @param {string[]} path Path to access.
 * @param {number} index Index of the pattern or structure tag.
 * @return {DestructuringInfo} The destructuring info at the given path.
 */
function getOrCreateDestructuringAtNestingLevel(node, current, path, index) {
    const method = getOrCreateMethodAtNestingLevel(node, current, path, -1);
    const destructuring = method.destructuring.get(index);
    return getOrCreateDestructuring(method, destructuring, index);
}

module.exports = {
    createObjectDocInfo,
};