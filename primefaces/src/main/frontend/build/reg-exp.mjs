// @ts-check

/**
 * Creates a regexp pattern that matches the given literal text.
 * @param {string} text A piece of literal text. 
 * @returns {string} The resulting RegExp pattern.
 */
export function escapeRegExp(text) {
    return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

/**
 * Creates a RegExp pattern that matches any of the given literal texts.
 * @param {Iterable<string>} patterns A list of literal texts. 
 * @param {string} prefix Optional prefix pattern to add in front of the pattern.
 * @param {string} suffix Optional suffix pattern to add at the end of the pattern.
 * @returns {RegExp} A RegExp matching any of the given texts.
 */
export function joinRegExp(patterns, prefix, suffix) {
    /** @type {string[]} */
    const escaped = [];
    for (const pattern of patterns) {
        escaped.push(escapeRegExp(pattern));
    }
    if (escaped.length === 0) {
        throw new Error("At least one pattern is required.");
    }
    return new RegExp(`${prefix}(${escaped.join("|")})${suffix}`);
}