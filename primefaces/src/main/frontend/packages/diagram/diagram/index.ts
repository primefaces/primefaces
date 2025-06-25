import * as _jsPlumb from "jsplumb";

import "./src/diagram.widget.js";

declare global {
    const jsPlumb: typeof _jsPlumb.jsPlumb;
    const jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
    const jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    interface Window {
        jsPlumb: typeof _jsPlumb.jsPlumb;
        jsPlumbInstance: typeof _jsPlumb.jsPlumbInstance;
        jsPlumbUtil: typeof _jsPlumb.jsPlumbUtil;
    }
}

// Expose jsPlumb to the global scope
Object.assign(window, { ..._jsPlumb });
