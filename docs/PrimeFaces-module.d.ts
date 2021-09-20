import "raphael";
/**
 * Additional plugins for the Raphaël library.
 */
declare module "raphael" {
    /**
     * A connection between two mindmap nodes
     */
    export interface MindmapConnection {
        /**
         *  The background element of the connection line.
         */
        bg: import("raphael").RaphaelElement;
        /**
         *  The line element representing the connection.
         */
        line: import("raphael").RaphaelElement;
        /**
         *  The source element where the connection starts.
         */
        from: import("raphael").RaphaelElement;
        /**
         *  The target element the connection ends at.
         */
        to: import("raphael").RaphaelElement;
    }
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
        connection(obj1: import("raphael").RaphaelElement, obj2: import("raphael").RaphaelElement, line: string | null, bg: string | null, effectSpeed: number): undefined | MindmapConnection;
    }
}
