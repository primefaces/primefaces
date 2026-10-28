import { Knob } from "./src/knob.widget.js";

PrimeFaces.widget.Knob = Knob;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Knob: typeof Knob;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type KnobCfg = import("./src/knob.widget.js").KnobCfg;
    }
}
