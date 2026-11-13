import { Galleria } from "./src/galleria.js";

PrimeFaces.component.Galleria = Galleria;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Galleria: typeof Galleria;
        }
    }
}
