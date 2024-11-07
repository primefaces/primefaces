
import "../../src/schedule/1-schedule.js";
import "../../src/schedule/fullcalendar-custom.css";

// Not necessary for our code, but expose to global scope for backwards compatibility
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
});
