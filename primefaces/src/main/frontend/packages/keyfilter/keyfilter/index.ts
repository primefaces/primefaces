import { KeyFilter } from "./src/keyfilter.js";

PrimeFaces.component.KeyFilter = KeyFilter;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            KeyFilter: typeof KeyFilter;
        }
    }
}
