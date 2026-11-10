import { ScrollPanel } from "./src/scrollpanel.js";

PrimeFaces.widget.ScrollPanel = ScrollPanel;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ScrollPanel: typeof ScrollPanel;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ScrollPanelCfg = import("./src/scrollpanel.js").ScrollPanelCfg;
    }
}
