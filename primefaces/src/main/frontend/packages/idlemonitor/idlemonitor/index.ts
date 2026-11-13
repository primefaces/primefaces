/// <reference path="./src/jquery.idletimer.d.ts" preserve="true" />

import "./src/jquery.idletimer.js";

import { IdleMonitor, registerKillSwitchHookForIdleMonitor } from "./src/idlemonitor.js";

// Expose components to the global scope
PrimeFaces.component.IdleMonitor = IdleMonitor;

// Register a kill switch feature  implementation that kills active idle monitors
registerKillSwitchHookForIdleMonitor();

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            IdleMonitor: typeof IdleMonitor;
        }
    }
}
