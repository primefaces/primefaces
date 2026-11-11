import { Log } from "./src/log.js";

PrimeFaces.component.Log = Log;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Log: typeof Log;
        }
    }

    namespace PrimeFacesTypes.component {
        export type LogCfg = import("./src/log.js").LogCfg;
    }

    namespace PrimeFacesTypes.component.Log {
        /**
         * Available severity levels for log messages used by the `Log` component.
         */
        export type Severity = "all" | "info" | "warn" | "debug" | "error";
    }
}
