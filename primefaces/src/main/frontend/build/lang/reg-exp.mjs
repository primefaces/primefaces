// @ts-check

/**
 * Creates a regexp pattern that matches the given literal text.
 * @param {string} text A piece of literal text. 
 * @returns {string} The resulting RegExp pattern.
 */
export function escapeRegExp(text) {
    return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}
