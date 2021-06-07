//@ts-check

const { Tags } = require("./constants");
const { checkTagHasDescription, checkTagHasNoDescription, checkTagHasName, checkSymbolHasDescription, checkTagHasType, checkTagIsPlain } = require("./doc-comment-check-tags");
const { newConstantErrorMessage } = require("./error");
const { createTypedefTagHandler } = require("./create-doc-info-typedef");

/**
 * Takes the doc comments for a constant and analyzes the comments. Returns a structured representation of
 * those doc comments.
 * @param {string} name Name of the constant
 * @param {import("comment-parser").Comment} jsdoc Doc comment for the constant.
 * @param {CodeConstant} constant Constant to document.
 * @param {SeveritySettingsConfig} severitySettings
 * @return {ConstantDocInfo}
 */
function createConstantDocInfo(name, jsdoc, constant, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newConstantErrorMessage(message, constant);

    checkSymbolHasDescription(jsdoc, factory, severitySettings, jsdoc.tags, `constant ${name}`);

    /** @type {ConstantDocInfo} */
    const docInfo = {
        additionalTags: [],
        description: jsdoc.description || "",
        name: name,
        optional: false,
        type: undefined,
        typedef: "",
        typedefs: [],
    };
    const typedefTagHandler = createTypedefTagHandler(constant.node, severitySettings, docInfo.typedefs);
    for (const tag of jsdoc.tags) {
        if (!tag) {
            continue;
        }
        switch (tag.tag) {

            case Tags.Async: {
                typedefTagHandler.async(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Constructor: {
                typedefTagHandler._constructor(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Generator: {
                typedefTagHandler.generator(tag, jsdoc.tags, true);
                break;
            }

            case Tags.This: {
                typedefTagHandler.this(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Param: {
                typedefTagHandler.params(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Return:
            case Tags.Returns: {
                typedefTagHandler.return(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Yield:
            case Tags.Yields: {
                typedefTagHandler.yield(tag, jsdoc.tags, true);
                break;
            }
            
            case Tags.Next: {
                typedefTagHandler.next(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Methodtemplate: {
                typedefTagHandler.methodtemplate(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Template: {
                typedefTagHandler.template(tag, jsdoc.tags, true);
                break;
            }

            case Tags.Structure:
            case Tags.Pattern: {
                typedefTagHandler.structureOrPattern(tag, jsdoc.tags, true);
                break;
            }

            // @constant {Foo.Bar}
            case Tags.Const:
            case Tags.Constant: {
                const type = checkTagHasType(tag, severitySettings, factory);
                checkTagHasNoDescription(tag, severitySettings, factory, false);
                docInfo.name = type;
                break;
            }

            // @default
            case Tags.Default: {
                checkTagIsPlain(tag, severitySettings, factory);
                docInfo.optional = true;
                break;
            }

            // @default
            case Tags.Readonly: {
                checkTagIsPlain(tag, severitySettings, factory);
                docInfo.type = "constant";
                break;
            }

            // @type {string}
            case Tags.Type: {
                const type = checkTagHasType(tag, severitySettings, factory);
                checkTagHasNoDescription(tag, severitySettings, factory, false);
                docInfo.typedef = type;
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
                        node: constant.node,
                        templates: new Map(),
                        typedef: tag.type,
                    });
                }
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
    createConstantDocInfo,
}