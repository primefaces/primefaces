//@ts-check

const { Tags } = require("./constants");
const { shiftName } = require("./doc-comments");
const { checkTagHasDescription, checkSymbolHasDescription, checkTagHasNoDescription, checkTagHasType, checkTagIsPlain } = require("./doc-comment-check-tags");
const { handleError, newPropErrorMessage } = require("./error");
const { createTypedefTagHandler } = require("./create-doc-info-typedef");

/**
 * @param {string} name Name of the property.
 * @param {import("comment-parser").Comment} jsdoc
 * @param {ObjectCodeProperty} property
 * @param {SeveritySettingsConfig} severitySettings
 * @return {PropDocInfo}
 */
function createPropDocInfo(name, jsdoc, property, severitySettings) {
    /** @type {MessageFactory} */
    const factory = message => newPropErrorMessage(message, property);

    checkSymbolHasDescription(jsdoc, factory, severitySettings, jsdoc.tags, `property ${name}`);

    /** @type {PropDocInfo} */
    const docInfo = {
        additionalTags: [],
        constants: [],
        description: jsdoc.description || "",
        name: name,
        optional: false,
        readonly: false,
        subObject: undefined,
        typedef: "",
        typedefs: [],
        visibility: undefined,
    };

    let foundType = false;
    const typedefTagHandler = createTypedefTagHandler(property.node, severitySettings, docInfo.typedefs);
    /** @type {import("comment-parser").Tag[]} */
    const subObjectTags = [];
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

            // @prop {string} foobar
            // @prop {string} [foobar] An optional property
            // @prop {string} foobar
            case Tags.Type: {
                if (foundType) {
                    handleError("tagDuplicateType", severitySettings, () => factory(`Tag '@type' for property '${name}' must not be specified multiple times`));
                }
                else {
                    foundType = true;
                    checkTagHasNoDescription(tag, severitySettings, factory, false);
                    const typedef = checkTagHasType(tag, severitySettings, factory);
                    docInfo.typedef = typedef;
                }
                break;
            }
            case Tags.Private:
            case Tags.Protected:
            case Tags.Public: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.visibility) {
                    handleError("tagDuplicateVisibilityModifier", severitySettings, () => factory(`Duplicate '@${tag.tag}' found in doc comments for property '${name}'`));
                }
                docInfo.visibility = tag.tag === Tags.Private ? "private" : tag.tag === Tags.Protected ? "protected" : "public";
                break;
            }
            case Tags.Readonly: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.readonly) {
                    handleError("tagDuplicateReadonly", severitySettings, () => factory(`Duplicate '@readonly' found in doc comments for property '${name}'`));
                }
                docInfo.readonly = true;
                break;
            }
            case Tags.Default: {
                checkTagIsPlain(tag, severitySettings, factory);
                if (docInfo.optional) {
                    handleError("tagDuplicateDefault", severitySettings, () => factory(`Duplicate '@default' found in doc comments for property '${name}'`));
                }
                docInfo.optional = true;
                break;
            }
            case Tags.Typedef: {
                const type = checkTagHasType(tag, severitySettings, factory);
                if (type.length > 0) {
                    docInfo.typedefs.push({
                        description: checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true),
                        function: undefined,
                        name: tag.name,
                        node: property.node,
                        templates: new Map(),
                        typedef: tag.type,
                    });
                }
                break;
            }
            case Tags.Class:
            case Tags.Interface:
            case Tags.Namespace: {
                if (docInfo.subObject !== undefined) {
                    handleError("propDuplicateIfaceOrClass", severitySettings, () => factory(`Duplicate '@interface', '@class', or '@namespace' found in doc comments for property '${name}'`));
                }
                else {
                    /** @type {ExportInfoType} */
                    let exportTypeInfo;
                    switch (tag.tag) {
                        case Tags.Class: exportTypeInfo = "class"; break;
                        case Tags.Interface: exportTypeInfo = "interface"; break;
                        case Tags.Namespace: exportTypeInfo = "namespace"; break;
                        default: throw new Error("Unhandled tag: " + tag.tag);
                    }
                    docInfo.subObject = {
                        description: tag.description,
                        tags: subObjectTags,
                        type: exportTypeInfo,
                    }
                    subObjectTags.push(tag);
                }
                break;
            }
            case Tags.Const:
            case Tags.Constant: {
                const desc = checkTagHasDescription(tag, severitySettings, factory, jsdoc.tags, true);
                const type = checkTagHasType(tag, severitySettings, factory);
                if (type) {
                    docInfo.constants.push({
                        description: desc,
                        name: tag.type,
                    });    
                }
                break;
            }
            default:
                docInfo.additionalTags.push(shiftName(tag));
                subObjectTags.push(tag);
        }
    }
    return docInfo;
}

module.exports = {
    createPropDocInfo,
}