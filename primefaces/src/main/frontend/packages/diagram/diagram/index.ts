import * as _jsPlumb from "jsplumb";

import { Diagram } from "./src/diagram.js";

PrimeFaces.component.Diagram = Diagram;

declare global {
    const jsPlumb: typeof _jsPlumb.jsPlumb;
    const jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
    const jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    interface Window {
        jsPlumb: typeof _jsPlumb.jsPlumb;
        jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
        jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            Diagram: typeof Diagram;
        }
    }
}

// Expose jsPlumb to the global scope
Object.assign(window, { ..._jsPlumb });
