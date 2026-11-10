import { Schedule } from "./src/schedule.js";
import { createFullCalendarGlobal, type FullCalendarGlobal } from "./src/createFullCalendarGlobal.js";

PrimeFaces.widget.Schedule = Schedule;

declare global {
    const FullCalendar: FullCalendarGlobal;
    interface Window {
        FullCalendar: FullCalendarGlobal;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Schedule: typeof Schedule;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ScheduleCfg = import("./src/schedule.js").ScheduleCfg;
    }

    namespace PrimeFacesTypes.widget.Schedule {
        /**
         * Name of JavaScript function to extend the options of the underlying FullCalendar plugin. Access the this
         * schedule widget via the this context, and change the FullCalendar configuration stored in `this.cfg`. See
         * also {@link ScheduleCfg.extender}.
         */
        export type ScheduleExtender =
            /**
             * @this The schedule widget whose FullCalendar configuration is extended.
             */
            (this: import("./src/schedule.js").Schedule) => void;
    }
}

// Expose some full calendar features to the global scope
Object.assign(window, { FullCalendar: createFullCalendarGlobal(), });
