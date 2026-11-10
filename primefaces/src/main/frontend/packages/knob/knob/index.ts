import { Knob } from "./src/knob.js";

PrimeFaces.widget.Knob = Knob;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Knob: typeof Knob;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type KnobCfg = import("./src/knob.js").KnobCfg;
    }
}
