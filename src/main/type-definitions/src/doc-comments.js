// @ts-check

const CommentParser = require("comment-parser");
const { handleError } = require("./error");
const { collectToMap, isNotBlank, removeLineBreaksFromStartAndEnd, splitLines } = require("./lang");
const { NamedTags, Tags } = require("./constants");

/** @type {Partial<import("comment-parser").Options>} */
const CommentParserOpts = {
    // Keep whitespace / line breaks for the description...
    trim: false,
    parsers: [
        CommentParser.PARSERS.parse_tag,
        CommentParser.PARSERS.parse_type,
        CommentParser.PARSERS.parse_name,
        CommentParser.PARSERS.parse_description,
        // but remove whitespace / line breaks from the name
        (source, data) => {
            return {
                source: source,
                data: {
                    ...data,
                    name: data.name ? data.name.trim() : "",
                }
            };
        },
    ],
};

/** @type {import("comment-parser").Comment} */
const EmptyDocComment = {
    description: "",
    line: 0,
    source: "",
    tags: [],
};

/**
 * @param {import("comment-parser").Comment} parseResult A comment to check.
 * @return {boolean} Whether the comment is empty, ie. contains no description and no tags.
 */
function isParseResultEmpty(parseResult) {
    if (parseResult === EmptyDocComment) {
        return true;
    }
    return parseResult.tags.length === 0 && (parseResult.description === undefined || parseResult.description.trim().length === 0);
}

/**
 * Parses a single doc comment.
 * @param {string} commentContent Content of the comment, without the comment delimiters (slash, asterisk)
 * @param {SeveritySettingsConfig} severitySettings
 * @return {import("comment-parser").Comment} The parsed comment.
 */
function parseSingleComment(severitySettings, commentContent = "") {
    let withDelimiters;
    if (commentContent.startsWith("*")) {
        withDelimiters = "/*" + commentContent + "*/";
    }
    else if (commentContent.startsWith("/*")) {
        withDelimiters = commentContent;
    }
    else {
        withDelimiters = "/**" + commentContent + "*/";
    }
    const parsed = CommentParser(withDelimiters, CommentParserOpts)[0];
    if (parsed === undefined && commentContent.length > 0) {
        handleError("docParseError", severitySettings, () => "Could not parse doc comment:\n" + commentContent);
    }
    // make all tags lowercase
    if (parsed !== undefined) {
        for (const tag of parsed.tags) {
            tag.tag = tag.tag.toLowerCase();
        }
    }
    return parsed !== undefined ? parsed : EmptyDocComment;
}

/**
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {string[]} tagNames Name of a tag to check.
 * @return {boolean} `true` if the the comment contains at least one tag with the given name, `false` otherwise.
 */
function hasTag(jsdoc, ...tagNames) {
    return getTag(jsdoc, ...tagNames) !== undefined;
}

/**
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @return {Map<string, import("comment-parser").Tag[]>}
 */
function getTagsByTagKind(jsdoc) {
    return collectToMap(
        jsdoc.tags,
        tag => tag.tag,
        tag => [tag],
        {
            reducer: (prev, cur) => [...prev, ...cur],
        },
    );
}

/**
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {string[]} tagNames Name of a tag to check.
 */
function removeTags(jsdoc, ...tagNames) {
    jsdoc.tags = jsdoc.tags.filter(tag => tagNames.some(x => x !== tag.tag));
}

/**
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {Set<string>} tagNames Name of a tag to check.
 * @return {boolean} `true` if the the comment contains at least one tag with the given name, `false` otherwise.
 */
function hasTagSet(jsdoc, tagNames) {
    return getTagSet(jsdoc, tagNames) !== undefined;
}

/**
 * Creates a new doc comment tag and initializes it with some defaults. You only need
 * to pass in the data you want to set explicitly.
 * @param {string} tag Name of the tag.
 * @param {Partial<import("comment-parser").Tag>} data Additional data for the tag.
 * @return {import("comment-parser").Tag} The newly created tag.
 */
function createTag(tag, data = {}) {
    return Object.assign({
        tag: tag,
        name: "",
        description: "",
        default: undefined,
        line: 0,
        optional: false,
        source: "",
        type: "",
    }, data);
}

/**
 * If the doc comments contains at least one tag of the given kind, returns the first such tag.
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {string[]} tagKinds Tag kinds to look for.
 * @return {import("comment-parser").Tag | undefined} The first tag with the given name.
 */
function getTag(jsdoc, ...tagKinds) {
    const set = new Set(tagKinds);
    return jsdoc.tags.find(tag => set.has(tag.tag));
}

/**
 * If the doc comments contains at least one tag with the given name, returns the first such tag.
 * @param {import("comment-parser").Tag[]} tags A parsed doc comment.
 * @param {string} tagKind Type of tag to look for.
 * @param {string} tagName Name of the tag to look for.
 * @return {import("comment-parser").Tag | undefined} The first tag with the given name.
 */
function getTagByKindAndName(tags, tagKind, tagName) {
    return tags.find(tag => tag.tag === tagKind && tag.name === tagName);
}

/**
 * If the doc comments contains at least one tag with the given name, returns the first such tag.
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {Set<string>} tagNames Name of the tag to look for.
 * @return {import("comment-parser").Tag | undefined} The first tag with the given name.
 */
function getTagSet(jsdoc, tagNames) {
    const set = new Set(tagNames);
    return jsdoc.tags.find(tag => set.has(tag.tag));
}

/**
 * Creates a new tag by removing the first word of the description and uses that
 * as the new name.
 * >  &#64;test . A param
 * 
 * becomes
 * 
 * > &#64;test A param
 * 
 * @param {import("comment-parser").Tag} tag 
 * @return {import("comment-parser").Tag} 
 */
function shiftName(tag) {
    const newTag = createTag(tag.tag, tag);
    const desc = tag.description = tag.description || "";
    const parts = desc.match(/^(\w+)\s*(.*)/);
    newTag.name = parts ? parts[1] : tag.name;
    newTag.description = parts ? (parts[2] || "") : tag.description;
    return newTag;
}

/**
 * @param {import("comment-parser").Comment} jsdoc A parsed doc comment.
 * @param {string} tagName Name of the tag to look for.
 * @return {import("comment-parser").Tag[]} A list of all tags with the given name.
 */
function getTags(jsdoc, tagName) {
    return jsdoc.tags.filter(tag => tagName === tag.tag);
}

/**
 * @param {import("comment-parser").Tag} tag 
 * @return {string}
 */
function getTagDescriptionWithName(tag) {
    if (tag.name) {
        const desc = tag.description.trim();
        return desc.length > 0 ? tag.name + " " + desc : tag.name;
    }
    else {
        return tag.description.trim();
    }
}

/**
 * Sanitizes the given tag, by applying the following modifications:
 * 
 * - If `initializer` is given, `optional` must be set to `true`
 * - Name cannot have trailing whitespace or newlines
 * 
 * @param {import("comment-parser").Tag} tag 
 * @return {import("comment-parser").Tag}
 */
function sanitizeTag(tag) {
    const newTag = createTag(tag.tag, tag);
    if (tag.default && !tag.optional) {
        newTag.optional = true;
        newTag.name = newTag.name.trim();
    }
    // empty strin is interpreted as having a default...
    if (newTag.default === "") {
        newTag.default = undefined;
    }
    return newTag;
}

/**
 * @param {import("comment-parser").Comment} doc
 * @return {boolean} `true` if the given comment or one of its tags have got any description.
 */
function hasParseResultAnyDescription(doc) {
    if (isNotBlank(doc.description)) {
        return true;
    }
    else {
        return doc.tags.some(tag => NamedTags.has(tag.tag) ? isNotBlank(tag.description) : isNotBlank(tag.name));
    }
}

/**
 * Checks whether the given tag should be output. Some tags that are only used internally such as `@include` are
 * omitted.
 * @param {import("comment-parser").Tag} tag Tag to check.
 * @return {boolean} Whether the tag should be included in the generated declaration file.
 */
function includeTagInDoc(tag) {
    switch (tag.tag) {
        case Tags.Include:
            return false;
        default:
            return true;
    }
}

/**
 * Creates a new doc comment with the given description and tags. Adds the doc comment
 * limiters (slash and asterisk) and properly indents the lines. 
 * @param {string} description Description of the comment, may be empty.
 * @param {import("comment-parser").Tag[]} tags Tags of the comment, may be an empty array.
 * @return {string[]} The doc comment with the given description and tags.
 */
function createDocComment(description, tags) {
    description = removeLineBreaksFromStartAndEnd(description);

    /** @type {import("comment-parser").Comment} */
    const parseResult = {
        description: removeLineBreaksFromStartAndEnd(description),
        line: 0,
        source: "",
        tags: tags.map(sanitizeTag).filter(includeTagInDoc),
    };
    let stringified = CommentParser.stringify(parseResult, { indent: 0 });
    if (stringified === undefined || stringified === "") {
        return [];
    }
    stringified = removeLineBreaksFromStartAndEnd(stringified);
    const lines = splitLines(stringified, {
        removeTrailingLines: "*",
    });
    return [
        "/**",
        ...lines.map(x => (x.startsWith("*") ? " " : " * ") + x),
        " */"
    ];
}

/**
 * @return {import("comment-parser").Comment} An empty doc comment with no description and no tags.
 */
function getEmptyDocComment() {
    return EmptyDocComment;
}

module.exports = {
    createDocComment,
    createTag,
    getEmptyDocComment,
    getTagByKindAndName,
    isParseResultEmpty,
    getTag,
    getTagDescriptionWithName,
    getTagSet,
    getTags,
    getTagsByTagKind,
    hasParseResultAnyDescription,
    hasTag,
    hasTagSet,
    parseSingleComment,
    removeTags,
    shiftName,
};