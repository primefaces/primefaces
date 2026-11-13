import { Clock, SimpleDateFormat } from "./src/clock.js";

PrimeFaces.component.Clock = Clock;
// TODO SimpleDateFormat is NOT a component, just a class, and should not be added to the PrimeFaces.component scope
// Add it for now to preserve backwards compatibility in case external code uses it.
PrimeFaces.component.SimpleDateFormat = SimpleDateFormat;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Clock: typeof Clock;
            SimpleDateFormat: typeof SimpleDateFormat;
        }
    }
}
