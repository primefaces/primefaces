import { KeyFilter } from "./src/keyfilter.js";

PrimeFaces.widget.KeyFilter = KeyFilter;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            KeyFilter: typeof KeyFilter;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type KeyFilterCfg = import("./src/keyfilter.js").KeyFilterCfg;
        export type KeyFilterMask = import("./src/keyfilter.js").KeyFilterMask;
        export type KeyFilterTestFunction = import("./src/keyfilter.js").KeyFilterTestFunction;
    }
}
