import { Knob } from "./src/knob.js";

PrimeFaces.component.Knob = Knob;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Knob: typeof Knob;
        }
    }

    namespace PrimeFacesTypes.component {
        export type KnobCfg = import("./src/knob.js").KnobCfg;
    }
}
