import { Galleria } from "./src/galleria.widget.js";

PrimeFaces.widget.Galleria = Galleria;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Galleria: typeof Galleria;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type GalleriaCfg = import("./src/galleria.widget.js").GalleriaCfg;
    }
}
