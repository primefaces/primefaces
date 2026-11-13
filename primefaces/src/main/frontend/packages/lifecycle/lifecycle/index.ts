import { Lifecycle } from "./src/lifecycle.js";

PrimeFaces.component.Lifecycle = Lifecycle;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Lifecycle: typeof Lifecycle;
        }
    }
}
