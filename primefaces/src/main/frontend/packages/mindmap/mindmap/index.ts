import "./src/raphael.connection.js";
import { Mindmap } from "./src/mindmap.js";

PrimeFaces.component.Mindmap = Mindmap;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Mindmap: typeof Mindmap;
        }
    }
}
