import { Dock } from "./src/dock.js";

PrimeFaces.component.Dock = Dock;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Dock: typeof Dock;
        }
    }

    namespace PrimeFacesTypes.component {
        export type DockCfg = import("./src/dock.js").DockCfg;
    }
}
