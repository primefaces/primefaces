import { ScrollPanel } from "./src/scrollpanel.js";

PrimeFaces.component.ScrollPanel = ScrollPanel;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            ScrollPanel: typeof ScrollPanel;
        }
    }
}
