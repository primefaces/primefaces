import { Log } from "./src/log.js";

PrimeFaces.widget.Log = Log;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Log: typeof Log;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type LogCfg = import("./src/log.js").LogCfg;
    }

    namespace PrimeFacesTypes.widget.Log {
        /**
         * Available severity levels for log messages used by the `Log` widget.
         */
        export type Severity = "all" | "info" | "warn" | "debug" | "error";
    }
}
