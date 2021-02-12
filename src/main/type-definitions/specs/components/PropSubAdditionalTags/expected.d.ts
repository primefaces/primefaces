declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to add custom tags to sub properties
     */
    export class PropSubAdditionalTags {
        /**
         * prop bar
         * @foobar tag foobar
         */
        bar: {
            /**
             * prop bar-x
             * @foobarx tag foobarx
             */
            x: string;
            /**
             * prop bar-y
             * @foobary tag foobary
             */
            y: number;
            /**
             * prop bar-z
             * @foobarz tag foobarz
             */
            z: {
                /**
                 * prop bar-z-a1
                 * @foobarza1 tag foobarza1
                 */
                a1: boolean;
                /**
                 * prop bar-z-a2
                 * @foobarza2 tag foobarza2
                 */
                a2: boolean;
            };
        };
    }
}