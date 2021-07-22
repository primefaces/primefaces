/**
 * Tests whether tags can have multiline descriptions
 */
({
    /**
     * method foo
     * @param {number} x This is the first rather long line of comments. However,
     * it is possible to break the description of a tag into multiple lines. It
     * will still be recognized as belongig to that tag. It should also be
     * possible to includde markdown like this:
     * 
     * - a
     * - b
     * - c
     * 
     * ```javascript
     * const x = answer(42);
     * ```
     * @return {string} This is the second tag.
     */
    foo(x) {return "";}
})