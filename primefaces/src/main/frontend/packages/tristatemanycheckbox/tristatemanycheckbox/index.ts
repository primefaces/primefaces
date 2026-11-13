import { TriStateManyCheckbox } from "./src/tristatemanycheckbox.js";

PrimeFaces.component.TriStateManyCheckbox = TriStateManyCheckbox;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            TriStateManyCheckbox: typeof TriStateManyCheckbox;
        }
    }
}
