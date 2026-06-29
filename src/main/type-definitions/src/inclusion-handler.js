//@ts-check

const { Tags } = require("./constants");
const { hasTag, hasTagSet } = require("./doc-comments");

/**
 * @param {import("comment-parser").Comment} jsdoc Associated doc comment for the property or method
 * @param {string} name Name of the property or method to check
 * @param {Set<string>} exclusionTagsSet Tags that mark a symbol as excluded
 * @return {boolean} Whether the property should be excluded in the type declaration file.
 */
function excludeSymbol(jsdoc, name, exclusionTagsSet) {
    const hasPrivate = hasTagSet(jsdoc, exclusionTagsSet);
    const hasInclude = hasTag(jsdoc, Tags.Include);
    if (hasPrivate || (name.startsWith("_") && !hasInclude)) {
        return true;
    }
    return false;
}

/**
 * @param {string[]} exclusionTags
 * @return {InclusionHandler}
 */
function createInclusionHandler(exclusionTags) {
    const exclusionTagsSet = new Set(exclusionTags);
    return {
        isIncludeFunction(jsdoc, functionName) {
            if (jsdoc === undefined || excludeSymbol(jsdoc, functionName, exclusionTagsSet)) {
                return false;
            }
            return true;
        },
        isIncludeProperty(jsdoc, propertyName) {
            if (jsdoc === undefined || excludeSymbol(jsdoc, propertyName, exclusionTagsSet)) {
                return false;
            }
            return true;
        },
        isIncludeType(jsdoc, typeName) {
            if (jsdoc === undefined || excludeSymbol(jsdoc, typeName, exclusionTagsSet)) {
                return false;
            }
            return true;
        },
        isIncludeVariable(jsdoc, variableName) {
            if (jsdoc === undefined || excludeSymbol(jsdoc, variableName, exclusionTagsSet)) {
                return false;
            }
            return true;
        }
    };
}

module.exports = {
    createInclusionHandler,
}