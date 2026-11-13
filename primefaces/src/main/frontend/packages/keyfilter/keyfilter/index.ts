import { KeyFilter } from "./src/keyfilter.js";

PrimeFaces.component.KeyFilter = KeyFilter;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            KeyFilter: typeof KeyFilter;
        }
    }

    namespace PrimeFacesTypes.component {
        export type KeyFilterCfg = import("./src/keyfilter.js").KeyFilterCfg;
        export type KeyFilterMask = import("./src/keyfilter.js").KeyFilterMask;
        export type KeyFilterTestFunction = import("./src/keyfilter.js").KeyFilterTestFunction;
    }
}
