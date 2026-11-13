import { Log } from "./src/log.js";

PrimeFaces.component.Log = Log;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Log: typeof Log;
        }
    }
}
