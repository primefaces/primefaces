import * as _jsPlumb from "jsplumb";

import { Diagram } from "./src/diagram.js";

PrimeFaces.widget.Diagram = Diagram;

declare global {
    const jsPlumb: typeof _jsPlumb.jsPlumb;
    const jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
    const jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    interface Window {
        jsPlumb: typeof _jsPlumb.jsPlumb;
        jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
        jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Diagram: typeof Diagram;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type DiagramCfg = import("./src/diagram.js").DiagramCfg;
    }

    namespace PrimeFacesTypes.widget.Diagram {
        /**
         * Details about a connection between two nodes that was either established or dissolved.
         */
        export interface ConnectionInfo {
            /** ID of the source node where the connection starts. */
            sourceId: string;
            /** ID of the target node where the connection end. */
            targetId: string;
            /** The point (port) where the connections starts. */
            sourceEndpoint: import("./src/diagram.js").DiagramEndpoint;
            /** The point (port) where the connections ends. */
            targetEndpoint: import("./src/diagram.js").DiagramEndpoint;
        }

        /**
         * Details about a connection between two nodes that was changed.
         */
        export interface UpdateConnectionInfo extends ConnectionInfo {
            /** Previous ID of the source node where the connection started. */
            originalSourceId: string;
            /** Previous ID of the target node where the connection ended. */
            originalTargetId: string;
            /** New ID of the source node where the connection starts. */
            newSourceId: string;
            /** New ID of the target node where the connection ends. */
            newTargetId: string;
            /** Previous point (port) where the connections started. */
            originalSourceEndpoint: import("./src/diagram.js").DiagramEndpoint;
            /** Previous point (port) where the connections ended. */
            originalTargetEndpoint: import("./src/diagram.js").DiagramEndpoint;
            /** New point (port) where the connections starts. */
            newSourceEndpoint: import("./src/diagram.js").DiagramEndpoint;
            /** New point (port) where the connections ends. */
            newTargetEndpoint: import("./src/diagram.js").DiagramEndpoint;
        }

        /**
         * Details about an element when its location was changed.
         */
        export interface UpdateElementInfo {
            /** ID of the element that was changed. */
            elementId: string;
            /** New horizontal position of the element. */
            x: number;
            /** New vertical position of the element. */
            y: number;
        }
    }
}

// Expose jsPlumb to the global scope
Object.assign(window, { ..._jsPlumb });
