declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to add arbitrary additional tags
     * @foo tag foo
     */
    export class DocCommentAdditionalTags {
        /**
         * prop x
         * @foox tag foox
         */
        x: number;
        /**
         * method y
         * @fooy tag fooy
         */
        y(): void;
        /**
         * prop bar
         * @bar tag bar
         */
        bar: number;
        /**
         * method baz
         * @baz tag baz
         */
        baz(): void;
    }
}