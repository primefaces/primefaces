declare namespace PrimeFaces.widget {
    /**
     * Tests whether tags can have multiline descriptions
     */
    export class DocCommentMultilineTags {
        /**
         * method foo
         * @param x This is the first rather long line of comments. However,
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
         * @return This is the second tag.
         */
        foo(x: number): string;
    }
}