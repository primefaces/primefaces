import { TriStateManyCheckbox } from "./src/tristatemanycheckbox.js";

PrimeFaces.component.TriStateManyCheckbox = TriStateManyCheckbox;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            TriStateManyCheckbox: typeof TriStateManyCheckbox;
        }
    }

    namespace PrimeFacesTypes.component {
        export type TriStateManyCheckboxCfg = import("./src/tristatemanycheckbox.js").TriStateManyCheckboxCfg;
    }
}
