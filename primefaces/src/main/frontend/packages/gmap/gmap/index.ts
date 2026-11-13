import { GMap } from "./src/gmap.js";

PrimeFaces.component.GMap = GMap;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            GMap: typeof GMap;
        }
    }
}
