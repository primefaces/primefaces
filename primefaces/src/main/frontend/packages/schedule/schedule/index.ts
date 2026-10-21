import "./src/schedule.widget.js";
import { createFullCalendarGlobal, type FullCalendarGlobal } from "./src/createFullCalendarGlobal.js";

declare global {
    const FullCalendar: FullCalendarGlobal;
    interface Window {
        FullCalendar: FullCalendarGlobal;
    }
}

// Expose some full calendar features to the global scope
Object.assign(window, { FullCalendar: createFullCalendarGlobal(), });
