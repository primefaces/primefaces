import { Knob } from "./src/knob.js";

PrimeFaces.component.Knob = Knob;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Knob: typeof Knob;
        }
    }
}
