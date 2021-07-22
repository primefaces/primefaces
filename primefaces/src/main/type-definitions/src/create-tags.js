//@ts-check

const { Tags } = require("./constants");
const { createTag } = require("./doc-comments");

/**
 * @param {string} description 
 * @param {string | undefined} initializer 
 */
function createDescriptionWithInitializer(description, initializer) {
    if (initializer !== undefined && initializer.length > 0) {
        return `Defaults to \`${initializer}\`. ${description}`;
    }
    return description;
}

/**
 * @param {string} tagName 
 * @param {string} description 
 * @param {string | undefined} initializer
 * @return {import("comment-parser").Tag[]} 
 */
function createTagForTypeParameter(tagName, description, initializer) {
    const typeparamTag = createTag(Tags.Typeparam, {
        name: tagName,
        description: createDescriptionWithInitializer(description, initializer),
    });
    return [typeparamTag];
}

/**
 * @param {string} name 
 * @param {string} description 
 * @param {string | undefined} initializer 
 * @param {boolean} required
 * @return {import("comment-parser").Tag} 
 */
function createTagForParameter(name, description, initializer, required) {
    return createTag(Tags.Param, {
        name: name,
        description: createDescriptionWithInitializer(description, initializer),
        // Whether the parameter is optional is already indicated by the type declaration
        optional: false, //!variable.required,
        // The default value should be described in the doc comment
        default: "", //variable.initializer,        
    })
}


/**
 * @param {number} index 
 * @param {string} description 
 * @param {string | undefined} initializer 
 * @param {boolean} required 
 * @return {import("comment-parser").Tag} 
 */
function createTagForPositionalParameter(index, description, initializer, required) {
    return createTag(Tags.Param, {
        name: `param${index}`,
        description: createDescriptionWithInitializer(description, initializer),
        // Whether the parameter is optional is already indicated by the type declaration
        optional: false, //!pattern.required,
        // The default value should be described in the doc comment
        default: "", //pattern.initializer,        
    });
}

module.exports = {
    createTagForTypeParameter,
    createTagForParameter,
    createTagForPositionalParameter,
};