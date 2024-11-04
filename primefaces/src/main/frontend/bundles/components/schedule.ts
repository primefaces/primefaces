import * as Core from "@fullcalendar/core";
import * as Interaction from "@fullcalendar/interaction";
import * as DayGrid from "@fullcalendar/daygrid";
import * as TimeGrid from "@fullcalendar/timegrid";
import * as List from "@fullcalendar/list";
import * as FcMoment from "@fullcalendar/moment";
import * as FcMomentTimeZone from "@fullcalendar/moment-timezone";

import allLocales from "@fullcalendar/core/locales-all"

import interactionPlugin from "@fullcalendar/interaction";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import listPlugin from "@fullcalendar/list";
import momentPlugin from "@fullcalendar/moment";
import momentTimezonePlugin from "@fullcalendar/moment-timezone";

import "../../src/schedule/1-schedule.js";

// Not necessary for our code, but expose to global scope for backwards compatibility
const FullCalendar = {
    ...Core,
    ...Interaction,
    ...DayGrid,
    ...TimeGrid,
    ...List,
    ...FcMoment,
    ...FcMomentTimeZone,
    interactionPlugin,
    dayGridPlugin,
    timeGridPlugin,
    listPlugin,
    momentPlugin,
    momentTimezonePlugin,
    globalLocales: allLocales
};
Object.assign(window, { FullCalendar });
