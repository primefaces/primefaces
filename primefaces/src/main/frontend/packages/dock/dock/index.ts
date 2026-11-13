import { Dock } from "./src/dock.js";

PrimeFaces.component.Dock = Dock;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Dock: typeof Dock;
        }
    }
}
