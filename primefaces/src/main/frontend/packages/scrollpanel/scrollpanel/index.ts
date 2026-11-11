import { ScrollPanel } from "./src/scrollpanel.js";

PrimeFaces.component.ScrollPanel = ScrollPanel;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            ScrollPanel: typeof ScrollPanel;
        }
    }

    namespace PrimeFacesTypes.component {
        export type ScrollPanelCfg = import("./src/scrollpanel.js").ScrollPanelCfg;
    }
}
