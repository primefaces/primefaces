import { Schedule } from "./src/schedule.js";
import { createFullCalendarGlobal, type FullCalendarGlobal } from "./src/createFullCalendarGlobal.js";

PrimeFaces.component.Schedule = Schedule;

declare global {
    const FullCalendar: FullCalendarGlobal;
    interface Window {
        FullCalendar: FullCalendarGlobal;
    }

    namespace PrimeFaces {
        export interface ComponentRegistry {
            Schedule: typeof Schedule;
        }
    }
}

// Expose some full calendar features to the global scope
Object.assign(window, { FullCalendar: createFullCalendarGlobal(), });
