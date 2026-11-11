import { Galleria } from "./src/galleria.js";

PrimeFaces.component.Galleria = Galleria;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Galleria: typeof Galleria;
        }
    }

    namespace PrimeFacesTypes.component {
        export type GalleriaCfg = import("./src/galleria.js").GalleriaCfg;
    }
}
