import { Galleria } from "./src/galleria.widget.js";

PrimeFaces.widget.Galleria = Galleria;

declare global {
    namespace PrimeType {
        export interface WidgetRegistry {
            Galleria: typeof Galleria;
        }
    }

    namespace PrimeType.widget {
        export type GalleriaCfg = import("./src/galleria.widget.js").GalleriaCfg;
    }
}
