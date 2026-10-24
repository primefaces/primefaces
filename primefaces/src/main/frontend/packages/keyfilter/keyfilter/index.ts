import { KeyFilter } from "./src/keyfilter.widget.js";

PrimeFaces.widget.KeyFilter = KeyFilter;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            KeyFilter: typeof KeyFilter;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type KeyFilterCfg = import("./src/keyfilter.widget.js").KeyFilterCfg;
        export type KeyFilterMask = import("./src/keyfilter.widget.js").KeyFilterMask;
        export type KeyFilterTestFunction = import("./src/keyfilter.widget.js").KeyFilterTestFunction;
    }
}
