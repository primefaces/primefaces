import { TriStateManyCheckbox } from "./src/tristatemanycheckbox.widget.js";

PrimeFaces.widget.TriStateManyCheckbox = TriStateManyCheckbox;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TriStateManyCheckbox: typeof TriStateManyCheckbox;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type TriStateManyCheckboxCfg = import("./src/tristatemanycheckbox.widget.js").TriStateManyCheckboxCfg;
    }
}
