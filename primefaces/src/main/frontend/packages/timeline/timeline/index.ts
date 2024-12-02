import "./src/timeline.widget.js";
import { createVisGlobal, type VisGlobal } from "./src/create-vis-global.js";

import "moment/locale/de.js";
import "moment/locale/es.js";
import "moment/locale/fr.js";
import "moment/locale/it.js";
import "moment/locale/ja.js";
import "moment/locale/nl.js";
import "moment/locale/pl.js";
import "moment/locale/ru.js";
import "moment/locale/uk.js";

declare global {
    const vis: VisGlobal;
    interface Window {
        vis: VisGlobal;
    }
}

// Their type declarations are wrong, timeline exists as a named export
declare module "vis-timeline" {
    export const timeline: unknown;
}

// Expose some vis features to the global scope
Object.assign(window, { vis: createVisGlobal(), });