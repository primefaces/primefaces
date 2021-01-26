//@ts-check

const { Tags } = require("./constants");
const { checkTagHasDescription, checkSymbolHasDescription, checkTagHasNoDescription, checkTagHasName, checkTagHasType, checkTagIsPlain } = require("./doc-comment-check-tags");
const { handleError, newMethodErrorMessage } = require("./error");
const { createTypedefTagHandler } = require("./create-doc-info-typedef");

/**
 * Takes the doc comments for a method (function) and analyzes the comments. Returns a structured representation of
 * those doc comments.
 * @param {string} name Name of the method
 * @param {import("comment-parser").Comment} jsdoc Doc comment for the method.
 * @param {ObjectCodeMethod} method Method to document.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {MethodDocInfo}
 */
function createMethodDocInfo(name, jsdoc, method, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newMethodErrorMessage(message, method);

    checkSymbolHasDescription(jsdoc, factory, severitySettings, jsdoc.tags, `method ${name}`);

    /** @type {Set<string>} */
    const existingTemplates = new Set();

    /** @type {MethodDocInfo} */
    const docInfo = {
        abstract: false,
        additionalTags: [],
        constructor: false,
        description: jsdoc.description || "",
        next: {
            description: "",
            hasNext: false,
            typedef: "",
        },
        patterns: new Map(),
        return: {
            description: "",
            hasReturn: false,
            typedef: "",
        },
        templates: [],
        thisTypedef: "",
        typedefs: [],
        variables: new Map(),
        visibility: undefined,
        yield: {
            description: "",
            hasYield: false,
            typedef: "",
        },
    };
    const typedefTagHandler = createTypedefTagHandler(method.node, severitySettings, docInfo.typedefs);
    for (const tag of jsdoc.tags) {
        if (!tag) {
            continue;
        }
        switch (tag.tag) {
            // @async
            case Tags.Async: {
                typedefTagHandler.async(tag, jsdoc.tags, true);
                break;
            }

            // @constructor
            case Tags.Constructor: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.constructor) {
                    handleError("tagDuplicateConstructor", severitySettings, () => factory(`Found duplicate tag '@constructor' in doc comments`));
                }
                docInfo.constructor = true;
                break;
            }

            // @generator
            case Tags.Generator: {
                typedefTagHandler.generator(tag, jsdoc.tags, true);
                break;
            }

            // @this {string}
            case Tags.This: {
                if (!typedefTagHandler.this(tag, jsdoc.tags, false)) {
                    const type = checkTagHasType(tag, severitySettings, factory);
                    checkTagHasNoDescription(tag, severitySettings, factory, false);
                    docInfo.thisTypedef = type;
                }
                break;
            }

            // @param {string} [x="9"]
            case Tags.Param: {
                if (!typedefTagHandler.params(tag, jsdoc.tags, false)) {
                    const tagName = checkTagHasName(tag, severitySettings, factory);
                    if (tagName.length > 0) {
                        if (docInfo.variables.has(tagName)) {
                            handleError("tagDuplicateParameter", severitySettings, () => factory(`@param ${tagName} must not occur multiple times`));
                        }
                        const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                        docInfo.variables.set(tagName, {
                            name: tagName,
                            // no type required in case of destructuring 
                            typedef: tag.type && tag.type !== "*" ? tag.type : "",
                            initializer: tag.default || "",
                            required: tag.optional === undefined ? true : !tag.optional,
                            description: desc,
                        });
                    }
                }
                break;
            }

            // @return {string}
            case Tags.Return:
            case Tags.Returns: {
                if (!typedefTagHandler.return(tag, jsdoc.tags, false)) {
                    if (docInfo.return.hasReturn) {
                        handleError("tagDuplicateReturn", severitySettings, () => factory("'@return' or '@returns' must not occur multiple times"));
                    }
                    const type = checkTagHasType(tag, severitySettings, factory);
                    const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, false);
                    docInfo.return.hasReturn = true;
                    docInfo.return.description = desc;
                    docInfo.return.typedef = type;
                }
                break;
            }

            // @yield {string}
            case Tags.Yield:
            case Tags.Yields: {
                if (!typedefTagHandler.yield(tag, jsdoc.tags, false)) {
                    if (docInfo.yield.hasYield) {
                        handleError("tagDuplicateYield", severitySettings, () => factory("'@yield' or '@yields' must not occur multiple times"));
                    }
                    const type = checkTagHasType(tag, severitySettings, factory);
                    const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, false);
                    docInfo.yield.hasYield = true;
                    docInfo.yield.description = desc;
                    docInfo.yield.typedef = type;
                }
                break;
            }

            // @next {string}
            case Tags.Next: {
                if (!typedefTagHandler.next(tag, jsdoc.tags, false)) {
                    if (docInfo.next.hasNext) {
                        handleError("tagDuplicateNext", severitySettings, () => factory("'@next' must not occur multiple times"));
                    }
                    const type = checkTagHasType(tag, severitySettings, factory);
                    const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, false);
                    docInfo.next.hasNext = true;
                    docInfo.next.description = desc;
                    docInfo.next.typedef = type;
                }
                break;
            }

            case Tags.Methodtemplate: {
                typedefTagHandler.methodtemplate(tag, jsdoc.tags, true);
                break;
            }

            // @template {keyof Foo} K
            case Tags.Template: {
                if (!typedefTagHandler.template(tag, jsdoc.tags, false)) {
                    const tagName = checkTagHasName(tag, severitySettings, factory);
                    if (tagName.length > 0) {
                        if (existingTemplates.has(tagName)) {
                            handleError("tagDuplicateTemplate", severitySettings, () => factory(`'@template ${tagName}' must not occur multiple times`));
                        }
                        const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                        existingTemplates.add(tagName);
                        docInfo.templates.push({
                            extends: tag.type || "",
                            description: desc,
                            initializer: tag.default || "",
                            name: tagName,
                        });
                    }
                }
                break;
            }

            // @abstract
            case Tags.Abstract: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.abstract) {
                    handleError("tagDuplicateAbstract", severitySettings, () => factory(`Found duplicate tag '@abstract' in doc comments`));
                }
                docInfo.abstract = true;
                break;
            }

            // @private / @protected / @public
            case Tags.Private:
            case Tags.Protected:
            case Tags.Public: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.visibility) {
                    handleError("tagDuplicateVisibilityModifier", severitySettings, () => factory(`Found duplicate tag '@${tag.tag}' in doc comments`));
                }
                docInfo.visibility = tag.tag === Tags.Private ? "private" : tag.tag === Tags.Protected ? "protected" : "public";
                break;
            }

            // @typedef {string|number} MyType
            case Tags.Typedef: {
                const tagName = checkTagHasName(tag, severitySettings, factory);
                if (tagName.length > 0) {
                    const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                    docInfo.typedefs.push({
                        description: desc,
                        function: undefined,
                        name: tagName,
                        node: method.node,
                        templates: new Map(),
                        typedef: tag.type,
                    });
                }
                break;
            }

            // @pattern {[string, boolean]} 0
            case Tags.Structure:
            case Tags.Pattern: {
                if (!typedefTagHandler.structureOrPattern(tag, jsdoc.tags, false)) {
                    const tagName = checkTagHasName(tag, severitySettings, factory);
                    if (tagName.length > 0) {
                        const index = parseInt(tagName);
                        if (isNaN(index) || index < 0) {
                            handleError("tagNameInvalidIndex", severitySettings, () => factory(`'@pattern' must specify an index that is a number greater than or equal to 0, eg. "@pattern {[number, string]} 0"`));
                        }
                        else {
                            const type = checkTagHasType(tag, severitySettings, factory);
                            if (tag.tag === Tags.Structure) {
                                handleError("unsupportedTag", severitySettings, () => factory(`@${tag.type} is not supported in a method context. Are you missing a @typedef?`));
                            }
                            else if (tag.tag === Tags.Pattern) {
                                if (docInfo.patterns.has(index)) {
                                    handleError("tagDuplicatePattern", severitySettings, () => factory(`'@pattern ${index}' must not occur multiple times`));
                                }
                                const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                                docInfo.patterns.set(index, {
                                    index: index,
                                    typedef: type,
                                    initializer: tag.default || "",
                                    required: tag.optional === undefined ? true : !tag.optional,
                                    description: desc,
                                });
                            }
                        }
                    }
                }
                break;
            }

            case Tags.Func:
            case Tags.Function:
            case Tags.Interface:
            case Tags.Class: {
                // ignore, only used during aggregation
                break;
            }

            // Any other tags
            default:
                docInfo.additionalTags.push(tag);
                break;
        }
    }
    return docInfo;
}

module.exports = {
    createMethodDocInfo,
}