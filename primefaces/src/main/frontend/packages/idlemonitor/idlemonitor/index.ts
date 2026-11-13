/// <reference path="./src/jquery.idletimer.d.ts" preserve="true" />

import "./src/jquery.idletimer.js";

import { IdleMonitor, registerKillSwitchHookForIdleMonitor } from "./src/idlemonitor.js";

// Expose components to the global scope
PrimeFaces.component.IdleMonitor = IdleMonitor;

// Register a kill switch feature  implementation that kills active idle monitors
registerKillSwitchHookForIdleMonitor();

// Global types
declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            /**
             * __PrimeFaces IdleMonitor Component__
             * 
             * IdleMonitor watches user actions on a page and notify callbacks in case they go idle or active again.
             * 
             * @typeParam Cfg Type of the configuration object.
             */
            IdleMonitor: typeof IdleMonitor;
        }
    }

    namespace PrimeFacesTypes.component {
        /**
         * The configuration for the {@link  IdleMonitor} component.
         * 
         * You can access this configuration via the `cfg` property of the component.
         * Please note that this configuration is usually meant to be read-only
         * and should not be modified.
         */
        export type IdleMonitorCfg = import("./src/idlemonitor.js").IdleMonitorCfg;
    }

    namespace PrimeFacesTypes.component.IdleMonitor {
        /**
        * Client side callback to execute when the user comes back. See
        * also {@link IdleMonitorCfg.onactive | cfg.onactive}.
        */
        export type OnActiveCallback = (this: IdleMonitor) => void;

        /**
         * Client side callback to execute when the user goes idle. See
         * also {@link IdleMonitorCfg.onidle | cfg.onidle}.
         */
        export type OnIdleCallback = (this: IdleMonitor) => void;
    }
}
