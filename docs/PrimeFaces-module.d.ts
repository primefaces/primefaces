import "raphael";
/**
 * Additional plugins for the Raphaël library.
 */
declare module "raphael" {
    /**
     * Additional methods defined on the paper, in addition to the builtin methods of the RaphaelPaper.
     */
    export interface RaphaelPaper {
        /**
         * Draws a connection between two mindmap nodes.
         * @param obj1 Source node where the connection starts.
         * @param obj2 Target node where the connection ends.
         * @param line Color of the connection.
         * @param bg Background specifier for the connection.
         * @param effectSpeed Effect speed for showing the new connection, in milliseconds.
         * @return An object with the newly created connection and the given source and target nodes.
         */
        connection(obj1: import("raphael").RaphaelElement, obj2: import("raphael").RaphaelElement, line: string | null, bg: string | null, effectSpeed: number): undefined | {
            bg: import("raphael").RaphaelElement;
            line: import("raphael").RaphaelElement;
            from: import("raphael").RaphaelElement;
            to: import("raphael").RaphaelElement;
        };
    }
}
