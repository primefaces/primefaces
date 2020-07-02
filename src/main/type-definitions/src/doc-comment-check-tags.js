//@ts-check

// Methods for validating doc comment tags

const { handleError } = require("./error");
const { Tags } = require("./constants");
const { getTagByKindAndName } = require("./doc-comments");

/**
 * @param {import("comment-parser").Tag | undefined} tag An optional tag to check. Checks the root comment otherwise.
 * @param {import("comment-parser").Tag[]} tags A list of all tags of the symbol to document.
 * @return {boolean} Whether the tag is has got an inheritdoc annotation that might give it a description.
 */
function isInheritDocTarget(tag, tags) {
    /** @type {string | undefined} */
    let name;
    if (tag !== undefined) {
        switch (tag.tag) {
            case Tags.Typeparam:
            case Tags.Param:
                name = tag.name.split(".").slice(0, -2)[0] || "";
                break;
            case Tags.Return:
                name = tag.name.split(".").slice(0, -1)[0] || "";
                break;
            default:
                // Other tags cannot get their description etc. from an inheritdoc annotation.
                name = undefined;
        }
    }
    else {
        name = "";
    }
    if (name !== undefined) {
        const override = getTagByKindAndName(tags, Tags.Override, name);
        const inheritDoc = getTagByKindAndName(tags, Tags.InheritDoc, name);
        return override !== undefined && inheritDoc !== undefined;
    }
    else {
        return false;
    }
}

/**
 * @param {import("comment-parser").Tag} tag
 * @param {SeveritySettingsConfig} severitySettings
 * @param {(message: string) => string} errorFactory
 * @return {string} The tag, without trailing whitespaces or other invalid characters.
 */
function checkTagHasName(tag, severitySettings, errorFactory) {
    const tagName = tag !== undefined && tag.name !== undefined ? tag.name.trim() : "";
    if (tagName.length === 0) {
        handleError("tagMissingName", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} must have a name, eg. '@${tag ? tag.tag : "UNNAMED"} TagName'`));
    }
    return tagName;
}

/**
 * @param {import("comment-parser").Comment | string} jsdoc
 * @param {(message: string) => string} errorFactory
 * @param {SeveritySettingsConfig} severitySettings
 * @param {import("comment-parser").Tag[]} tags A list of all tags of the symbol to document.
 * @param {string} symbolName
 */
function checkSymbolHasDescription(jsdoc, errorFactory, severitySettings, tags, symbolName) {
    const desc = typeof jsdoc === "string" ? jsdoc.trim() : jsdoc.description ? jsdoc.description.trim() : "";
    if (desc.length === 0) {
        /** @type {"symbolOverriddenMissingDesc" | "symbolMissingDesc"} */
        let key;
        if (isInheritDocTarget(undefined, tags)) {
            key = "symbolOverriddenMissingDesc";
        }
        else {
            key = "symbolMissingDesc";
        }
        handleError(key, severitySettings, () => errorFactory(`Doc comment for ${symbolName} does not have any description.`));
    }
}

/**
 * @param {import("comment-parser").Tag} tag
 * @param {(message: string) => string} errorFactory
 * @param {boolean} excludeName `true` if it is a tag with a name, `false` otherwise. 
 * @param {SeveritySettingsConfig} severitySettings
 * @param {import("comment-parser").Tag[]} tags A list of all tags of the symbol to document.
 * @return {string} The description, without trailing white spaces or other invalid characters.
 */
function checkTagHasDescription(tag, severitySettings, errorFactory, tags, excludeName = false) {
    const desc = tag !== undefined && tag.description !== undefined ? tag.description.trim() : "";
    const withName = excludeName ? desc : ((tag.name || "").trim() + " " + desc).trim();
    if (withName.length === 0 && tag.tag !== Tags.Pattern) {
        /** @type {"tagOverriddenMissingDesc" | "tagParamMissingDesc" | "tagReturnMissingDesc" | "tagTemplateMissingDesc" | "tagTypedefMissingDesc" | "tagMissingDesc"} */
        let key;
        if (isInheritDocTarget(tag, tags)) {
            key = "tagOverriddenMissingDesc";
        }
        else if (tag.tag === Tags.Param) {
            key = "tagParamMissingDesc";
        }
        else if (tag.tag === Tags.Return || tag.tag === Tags.Returns) {
            key = "tagReturnMissingDesc";
        }
        else if (tag.tag === Tags.Template) {
            key = "tagTemplateMissingDesc";
        }
        else if (tag.tag === Tags.Typedef) {
            key = "tagTypedefMissingDesc";
        }
        else {
            key = "tagMissingDesc";
        }
        handleError(key, severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} (${tag.name ? tag.name : "<no-name>"}) does not have any description.`));
    }
    return withName;
}

/**
 * @param {import("comment-parser").Tag} tag
 * @param {(message: string) => string} errorFactory
 * @param {boolean} excludeName `true` if it is a tag with a name, `false` otherwise. 
 * @param {SeveritySettingsConfig} severitySettings
 * @return {boolean} `true` if tag has no description, `false` otherwise.
 * @throws When the tag has a description and the severity level is set to `fatal`.
 */
function checkTagHasNoDescription(tag, severitySettings, errorFactory, excludeName = false) {
    const desc = tag !== undefined && tag.description !== undefined ? tag.description.trim() : "";
    const withName = excludeName ? desc : ((tag.name || "").trim() + " " + desc).trim();
    if (withName.trim().length !== 0) {
        handleError("tagSuperfluousDesc", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} (${tag.name ? tag.name : "<no-name>"}) does not require a description, it will be ignored."`));
        return false;
    }
    return true;
}

/**
 * Checks whether the tag is a plain tag, ie. only with a tag name and nothing else, such as `@readonly`.
 * @param {import("comment-parser").Tag} tag
 * @param {(message: string) => string} errorFactory
 * @param {SeveritySettingsConfig} severitySettings
 */
function checkTagIsPlain(tag, severitySettings, errorFactory, { checkName = true, checkDesc = true, checkType = true } = {}) {
    if (checkName && tag !== undefined && tag.name !== undefined && tag.name !== "") {
        handleError("tagNotPlain", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} must be plain and contain neither a type, name, nor description.`));
    }
    if (checkDesc && tag !== undefined && tag.description !== undefined && tag.description !== "") {
        handleError("tagNotPlain", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} must be plain and contain neither a type, name, nor description.`));
    }
    if (checkType && tag !== undefined && tag.type !== undefined && tag.type !== "") {
        handleError("tagNotPlain", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} must be plain and contain neither a type, name, nor description`));
    }
}

/**
 * @param {import("comment-parser").Tag} tag
 * @param {(message: string) => string} errorFactory
 * @param {SeveritySettingsConfig} severitySettings
 * @return {string} The cleaned up type.
 */
function checkTagHasType(tag, severitySettings, errorFactory) {
    const type = tag !== undefined && tag.type !== undefined && tag.type !== "*" ? tag.type.trim() : "";
    if (type.length === 0) {
        handleError("tagMissingType", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} (${tag.name ? tag.name : "<no-name>"}) must specify a type, eg. '@${tag ? tag.tag : "UNNAMED"} {string} ${tag.name.trim()}'`));
    }
    return type || "any";
}

/**
 * @param {import("comment-parser").Tag} tag
 * @param {SeveritySettingsConfig} severitySettings
 * @param {(message: string) => string} errorFactory
 */
function checkTagHasNoType(tag, severitySettings, errorFactory) {
    const type = tag !== undefined && tag.type !== undefined && tag.type !== "*" ? tag.type.trim() : "";
    if (type.length > 0) {
        handleError("tagSuperfluousType", severitySettings, () => errorFactory(`Tag @${tag ? tag.tag : "UNNAMED"} (${tag.name ? tag.name : "<no-name>"}) does not require a type, it will be ignored`));
    }
}

module.exports = {
    checkSymbolHasDescription,
    checkTagHasDescription,
    checkTagHasName,
    checkTagHasNoDescription,
    checkTagHasNoType,
    checkTagHasType,
    checkTagIsPlain,
}