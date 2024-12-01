import "./schedule.widget.js";
import "./fullcalendar-custom.css";

// Expose some full calendar features to the global scope
// Not needed for our code, but may already be used by external code
import * as FullCalendarCore from "@fullcalendar/core";
import * as FullCalendarInteraction from "@fullcalendar/interaction";
import * as FullCalendarDayGrid from "@fullcalendar/daygrid";
import * as FullCalendarTimeGrid from "@fullcalendar/timegrid";
import * as FullCalendarList from "@fullcalendar/list";
import * as FullCalendarMoment from "@fullcalendar/moment";
import * as FullCalendarMomentTimeZone from "@fullcalendar/moment-timezone";
import FullCalendarCoreLocalesAll from "@fullcalendar/core/locales-all"

import interactionPlugin from "@fullcalendar/interaction";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import listPlugin from "@fullcalendar/list";
import momentPlugin from "@fullcalendar/moment";
import momentTimezonePlugin from "@fullcalendar/moment-timezone";

Object.assign(window, {
    FullCalendar: {
        ...FullCalendarCore,
        ...FullCalendarInteraction,
        ...FullCalendarDayGrid,
        ...FullCalendarTimeGrid,
        ...FullCalendarList,
        ...FullCalendarMoment,
        ...FullCalendarMomentTimeZone,
        interactionPlugin,
        dayGridPlugin,
        timeGridPlugin,
        listPlugin,
        momentPlugin,
        momentTimezonePlugin,
        globalLocales: FullCalendarCoreLocalesAll
    }
});
