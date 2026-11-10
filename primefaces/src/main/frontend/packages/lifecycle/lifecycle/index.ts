import { Lifecycle } from "./src/lifecycle.widget.js";

PrimeFaces.widget.Lifecycle = Lifecycle;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Lifecycle: typeof Lifecycle;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type LifecycleCfg = import("./src/lifecycle.widget.js").LifecycleCfg;
    }
}
