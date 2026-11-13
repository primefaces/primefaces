import { Lifecycle } from "./src/lifecycle.js";

PrimeFaces.component.Lifecycle = Lifecycle;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Lifecycle: typeof Lifecycle;
        }
    }

    namespace PrimeFacesTypes.component {
        export type LifecycleCfg = import("./src/lifecycle.js").LifecycleCfg;
    }
}
