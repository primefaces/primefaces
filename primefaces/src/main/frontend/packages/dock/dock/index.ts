import { Dock } from "./src/dock.js";

PrimeFaces.widget.Dock = Dock;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Dock: typeof Dock;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type DockCfg = import("./src/dock.js").DockCfg;
    }
}
