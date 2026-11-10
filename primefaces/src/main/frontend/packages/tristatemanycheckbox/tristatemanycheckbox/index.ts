import { TriStateManyCheckbox } from "./src/tristatemanycheckbox.js";

PrimeFaces.widget.TriStateManyCheckbox = TriStateManyCheckbox;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TriStateManyCheckbox: typeof TriStateManyCheckbox;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type TriStateManyCheckboxCfg = import("./src/tristatemanycheckbox.js").TriStateManyCheckboxCfg;
    }
}
