import * as FullCalendarCore from "@fullcalendar/core";
import * as FullCalendarInteraction from "@fullcalendar/interaction";
import * as FullCalendarDayGrid from "@fullcalendar/daygrid";
import * as FullCalendarTimeGrid from "@fullcalendar/timegrid";
import * as FullCalendarList from "@fullcalendar/list";
import * as FullCalendarMoment from "@fullcalendar/moment";
import * as FullCalendarMomentTimeZone from "@fullcalendar/moment-timezone";
import FullCalendarCoreLocalesAll from "@fullcalendar/core/locales-all.js";

import interactionPlugin from "@fullcalendar/interaction";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import listPlugin from "@fullcalendar/list";
import momentPlugin from "@fullcalendar/moment";
import momentTimezonePlugin from "@fullcalendar/moment-timezone";

export type FullCalendarCoreType = Omit<typeof FullCalendarCore, "default">;
export type FullCalendarInteractionType = Omit<typeof FullCalendarInteraction, "default">;
export type FullCalendarDayGridType = Omit<typeof FullCalendarDayGrid, "default">;
export type FullCalendarTimeGridType = Omit<typeof FullCalendarTimeGrid, "default">;
export type FullCalendarListType = Omit<typeof FullCalendarList, "default">;
export type FullCalendarMomentType = Omit<typeof FullCalendarMoment, "default">;
export type FullCalendarMomentTimeZoneType = Omit<typeof FullCalendarMomentTimeZone, "default">;

export interface FullCalendarGlobal extends
    FullCalendarCoreType,
    FullCalendarInteractionType,
    FullCalendarDayGridType,
    FullCalendarTimeGridType,
    FullCalendarListType,
    FullCalendarMomentType,
    FullCalendarMomentTimeZoneType {
    interactionPlugin: typeof interactionPlugin;
    dayGridPlugin: typeof dayGridPlugin;
    timeGridPlugin: typeof timeGridPlugin;
    listPlugin: typeof listPlugin;
    momentPlugin: typeof momentPlugin;
    momentTimezonePlugin: typeof momentTimezonePlugin;
    globalLocales: typeof FullCalendarCoreLocalesAll.default;
}

export function createFullCalendarGlobal(): FullCalendarGlobal {
    return {
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
        globalLocales: FullCalendarCoreLocalesAll.default,
    };
}