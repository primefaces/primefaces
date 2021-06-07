declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to add inline sub properties
     */
    export class PropSubInline {
        /**
         * prop bar
         */
        bar: {
            /**
             * prop bar-x
             */
            x: string;
            /**
             * prop bar-y
             */
            y: number;
            /**
             * prop bar-z
             */
            z: {
                /**
                 * prop bar-z-a1
                 */
                a1: boolean;
                /**
                 * prop bar-z-a2
                 */
                a2: boolean;
            };
        };
    }
}