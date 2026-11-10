import {
    Calendar,
    type CalendarOptions,
    type DateSelectArg,
    type EventClickArg,
    type EventDropArg,
    type EventHoveringArg,
    type EventMountArg,
    type EventSourceFunc,
    type ViewContentArg
} from "@fullcalendar/core";
import allLocales from "@fullcalendar/core/locales-all.js";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin, { type DateClickArg, type EventResizeDoneArg } from "@fullcalendar/interaction";
import listPlugin from "@fullcalendar/list";
import momentPlugin from "@fullcalendar/moment";
import momentTimezonePlugin from "@fullcalendar/moment-timezone";
import timeGridPlugin from "@fullcalendar/timegrid";

/** The options of a FullCalendar view. */
type ViewOptions = NonNullable<CalendarOptions["views"]>[string];

/**
 * The configuration for the {@link Schedule} widget.
 */
export interface ScheduleCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Format of the column header per view name, e.g. `{ dayGridMonth: "ddd" }`. */
    columnFormatOptions: Record<string, ViewOptions["dayHeaderFormat"]>;
    /** Whether the description of an event in the tooltip is HTML escaped, default is `true`. */
    escape: boolean;
    /**
     * Name of JavaScript function to extend the options of the underlying FullCalendar plugin. Access the this schedule
     * widget via the this context, and change the FullCalendar configuration stored in `this.cfg`.
     */
    extender: PrimeFacesTypes.widget.Schedule.ScheduleExtender;
    /** Locale code of the locale for the FullCalendar, such as `de` or `en`. */
    locale: string;
    /**
     * Whether for URL events access to the opener window from the target site should be prevented (phishing
     * protection), default value is `true`.
     */
    noOpener: boolean;
    /** The configuration object that is passed to the FullCalendar upon initialization. */
    options: CalendarOptions;
    /** Theme system used for rendering the calendar. */
    themeSystem: boolean;
    /** Whether a tooltip should be displayed on hover. */
    tooltip: boolean;
    /**
     * Target for events with urls. Clicking on such events in the schedule will not trigger the `selectEvent` but
     * open the url using this target instead. Default is `_blank`.
     */
    urlTarget: string;
    /**
     * Legacy location of the view options, only used as fallback for {@link CalendarOptions.views} for backwards
     * compatibility.
     */
    views: CalendarOptions["views"];
}

/**
 * __PrimeFaces Schedule Widget__
 *
 * Schedule provides an Outlook Calendar, iCal like Faces component to manage events.
 */
export class Schedule<Cfg extends ScheduleCfg = ScheduleCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The configuration, the server always renders the FullCalendar `options`. */
    declare cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg> & Pick<ScheduleCfg, "options">;

    /**
     * The current full calendar instance. May be `undefined` when the widget exists, but was not yet rendered, such
     * as when inside a non-active tab.
     */
    calendar?: Calendar;
    /** The clicked date, used internally when an event was clicked to detect a double click. */
    doubleClick?: string | null;
    /** Timeout ID, used internally when an event was clicked to detect a double click. */
    clickTimer?: number | null;
    /** The DOM element for the tooltip. */
    tip?: JQuery;
    /** The set-timeout timer ID for displaying a delayed tooltip. */
    tipTimeout?: number;
    /** The DOM element for the hidden input storing the current view state. */
    viewNameState: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.options.themeSystem = 'standard';
        this.cfg.options.slotLabelFormat = this.cfg.options.slotLabelFormat || undefined;
        // FullCalendar uses the return value as class names, undefined adds none
        this.cfg.options.viewClassNames = this.onViewChange.bind(this) as unknown as CalendarOptions["viewClassNames"];
        this.viewNameState = $(this.jqId + '_view');
        this.cfg.urlTarget = this.cfg.urlTarget || "_blank";
        // the default imports of the CommonJS plugin modules are the plugins at runtime, their types are the modules
        this.cfg.options.plugins = [
            interactionPlugin,
            dayGridPlugin,
            timeGridPlugin,
            listPlugin,
            momentPlugin,
            momentTimezonePlugin
        ] as unknown as CalendarOptions["plugins"];

        this.setupEventSource();

        this.configureLocale();

        if (this.cfg.tooltip) {
            this.tip = $('<div class="p-tooltip p-widget p-widget-content"></div>').appendTo(document.body);
            this.addDestroyListener(function () { this.tip!.remove(); });
            this.addRefreshListener(function () { this.tip!.remove(); });
        }

        this.setupEventHandlers();

        if (this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.setViewOptions();

        this.renderDeferred();

        // must be done after FullCalendar is built
        this.setupTitlebarHandlers();
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        if (this.calendar) {
            this.calendar.destroy();
        }

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        if (this.calendar) {
            this.calendar.destroy();
        }
    }

    protected override _render(): void {
        const calendarEl = document.getElementById(this.getId())!;
        this.calendar = new Calendar(calendarEl, this.cfg.options);
        this.calendar.render();
    }

    /**
     * Localizes certain aspects of FullCalendar that are exposed. The rest are configured by "locale"
     * setting and FullCalendar and Moment translations for that locale.
     */
    private configureLocale(): void {
        const options = this.cfg.options;

        // #6496 must add all locales
        options.locales = allLocales as unknown as CalendarOptions["locales"];

        const lang = PrimeFaces.locales[this.cfg.locale!];
        if (lang) {
            if (lang.firstDayOfWeek !== undefined) { options.firstDay = lang.firstDayOfWeek; }
            if (lang.weekNumberTitle) { options.weekText = lang.weekNumberTitle; }
            if (lang.allDayText) { options.allDayText = lang.allDayText; }
            if (lang.moreLinkText) { options.moreLinkText = lang.moreLinkText; }
            if (lang.noEventsText) { options.noEventsText = lang.noEventsText; }

            const buttonText = options.buttonText || {};
            if (lang.aria.previous) { buttonText.prev = lang.aria.previous; }
            if (lang.aria.next) { buttonText.next = lang.aria.next; }
            if (lang.today) { buttonText.today = lang.today; }
            if (lang.year) { buttonText.year = lang.year; }
            if (lang.month) { buttonText.month = lang.month; }
            if (lang.week) { buttonText.week = lang.week; }
            if (lang.day) { buttonText.day = lang.day; }
            if (lang.list) { buttonText.list = lang.list; }
            options.buttonText = buttonText;
        }
    }

    /**
     * Creates and sets the event listeners for the full calendar.
     */
    private setupEventHandlers(): void {
        this.cfg.options.dateClick = (dateClickInfo: DateClickArg) => {
            const currentDate = dateClickInfo.date.toISOString();
            const ext = {
                params: [{
                    name: this.id + '_selectedDate',
                    value: currentDate
                }]
            };

            if (this.doubleClick === currentDate) {
                this.doubleClick = null;
                if (this.hasBehavior('dateDblSelect')) {
                    this.callBehavior('dateDblSelect', ext);
                }
            } else {
                this.doubleClick = currentDate;
                clearInterval(this.clickTimer ?? undefined);
                this.clickTimer = setInterval(() => {
                    this.doubleClick = null;
                    clearInterval(this.clickTimer ?? undefined);
                    this.clickTimer = null;
                }, 500);

                if (this.hasBehavior('dateSelect')) {
                    this.callBehavior('dateSelect', ext);
                }
            }
        };

        this.cfg.options.eventClick = (eventClickInfo: EventClickArg) => {
            if (eventClickInfo.event.url) {
                const targetWindow = window.open('', this.cfg.urlTarget)!;
                if (this.cfg.noOpener) {
                    targetWindow.opener = null;
                }
                targetWindow.location = eventClickInfo.event.url;
                eventClickInfo.jsEvent.preventDefault(); // don't let the browser navigate
                return false;
            }

            const eventId = eventClickInfo.event.id;
            const ext = {
                params: [
                    { name: this.id + '_selectedEventId', value: eventId }
                ]
            };

            if (this.doubleClick === eventId) {
                this.doubleClick = null;
                if (this.hasBehavior('eventDblSelect')) {
                    this.callBehavior('eventDblSelect', ext);
                }
            } else {
                this.doubleClick = eventId;
                clearInterval(this.clickTimer ?? undefined);
                this.clickTimer = setInterval(() => {
                    this.doubleClick = null;
                    clearInterval(this.clickTimer ?? undefined);
                    this.clickTimer = null;
                }, 500);

                if (this.hasBehavior('eventSelect')) {
                    this.callBehavior('eventSelect', ext);
                }
            }
            return undefined;
        };

        this.cfg.options.eventDrop = (eventDropInfo: EventDropArg) => {
            if (this.hasBehavior('eventMove')) {
                const ext = {
                    params: [
                        { name: this.id + '_movedEventId', value: eventDropInfo.event.id },
                        { name: this.id + '_yearDelta', value: eventDropInfo.delta.years },
                        { name: this.id + '_monthDelta', value: eventDropInfo.delta.months },
                        { name: this.id + '_dayDelta', value: eventDropInfo.delta.days },
                        { name: this.id + '_minuteDelta', value: (eventDropInfo.delta.milliseconds / 60000) },
                        { name: this.id + '_allDay', value: (eventDropInfo.event.allDay) }
                    ]
                };

                this.callBehavior('eventMove', ext);
            }
        };

        this.cfg.options.eventResize = (eventResizeInfo: EventResizeDoneArg) => {
            if (this.hasBehavior('eventResize')) {
                const ext = {
                    params: [
                        { name: this.id + '_resizedEventId', value: eventResizeInfo.event.id },
                        { name: this.id + '_startDeltaYear', value: eventResizeInfo.startDelta.years },
                        { name: this.id + '_startDeltaMonth', value: eventResizeInfo.startDelta.months },
                        { name: this.id + '_startDeltaDay', value: eventResizeInfo.startDelta.days },
                        { name: this.id + '_startDeltaMinute', value: (eventResizeInfo.startDelta.milliseconds / 60000) },
                        { name: this.id + '_endDeltaYear', value: eventResizeInfo.endDelta.years },
                        { name: this.id + '_endDeltaMonth', value: eventResizeInfo.endDelta.months },
                        { name: this.id + '_endDeltaDay', value: eventResizeInfo.endDelta.days },
                        { name: this.id + '_endDeltaMinute', value: (eventResizeInfo.endDelta.milliseconds / 60000) }
                    ]
                };

                this.callBehavior('eventResize', ext);
            }
        };

        if (this.cfg.options.selectable) {
            this.cfg.options.select = (selectionInfo: DateSelectArg) => {
                if (this.hasBehavior('rangeSelect')) {
                    const ext = {
                        params: [
                            { name: this.id + '_startDate', value: selectionInfo.start.toISOString() },
                            { name: this.id + '_endDate', value: selectionInfo.end.toISOString() }
                        ]
                    };

                    this.callBehavior('rangeSelect', ext);
                }
            };
        }

        if (this.cfg.tooltip) {
            this.cfg.options.eventMouseEnter = (mouseEnterInfo: EventHoveringArg) => {
                if (mouseEnterInfo.event.extendedProps.description) {
                    this.tipTimeout = PrimeFaces.queueTask(() => {
                        this.tip!.css({
                            'left': mouseEnterInfo.jsEvent.pageX + 'px',
                            'top': (mouseEnterInfo.jsEvent.pageY + 15) + 'px',
                            'z-index': PrimeFaces.nextZindex()
                        });
                        let description: string = mouseEnterInfo.event.extendedProps.description;
                        if (this.cfg.escape !== false) {
                            description = PrimeFaces.escapeHTML(description);
                        }
                        this.tip![0]!.innerHTML = description;
                        this.tip!.show();
                    }, 150);
                }
            };

            this.cfg.options.eventMouseLeave = () => {
                if (this.tipTimeout) {
                    clearTimeout(this.tipTimeout);
                }

                if (this.tip!.is(':visible')) {
                    this.tip!.hide();
                    this.tip!.text('');
                }
            };
        } else {
            // PF #2795 default to regular tooltip
            this.cfg.options.eventDidMount = (info: EventMountArg) => {
                if (info.event.extendedProps.description) {
                    $(info.el).attr('title', info.event.extendedProps.description);
                }
            };
        }
    }

    /**
     * Creates and sets the event listeners for the previous, next, and today buttons in the title bar.
     */
    private setupTitlebarHandlers(): void {
        // only the own buttons, the ones of other schedules on the page are not replaced by an update of this one
        this.jq.find('.fc-prev-button, .fc-next-button, .fc-today-button').off('click.' + this.id).on('click.' + this.id, () => {
            this.callBehavior('viewChange');
        });
    }

    /**
     * Creates the event listeners for the FullCalendar events.
     */
    private setupEventSource(): void {
        const events: EventSourceFunc = (fetchInfo, successCallback) => {
            // #13346 - schedule events must be called after the viewChange event is fired
            PrimeFaces.queueTask(() => {
                const options: PrimeFacesTypes.ajax.Configuration = {
                    source: this.getId(),
                    process: this.getId(),
                    update: this.getId(),
                    ignoreAutoUpdate: true,
                    formId: this.getParentFormId(),
                    params: [
                        { name: this.id + '_event', value: true },
                        { name: this.id + '_start', value: fetchInfo.start.toISOString() },
                        { name: this.id + '_end', value: fetchInfo.end.toISOString() }
                    ],
                    onsuccess: (responseXML, status, xhr) => {
                        PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                            widget: this,
                            handle: (content) => {
                                successCallback(JSON.parse(content).events);
                            }
                        });

                        return true;
                    }
                };
                PrimeFaces.ajax.Request.handle(options);
            });
        };
        this.cfg.options.events = events;
    }

    /**
     * Updates and refreshes the schedule view.
     */
    update(): void {
        this.calendar!.refetchEvents();
    }

    /**
     * The event listener for when the user switches the to a different view (month view, week day, or time view).
     * Updates the hidden input field with the current view name. Used for restoring the view after an AJAX update.
     * @param arg Event data passed by FullCalendar when the view changes.
     */
    private onViewChange(arg: ViewContentArg): void {
        this.viewNameState.val(arg.view.type);
        this.callBehavior('viewChange');
    }

    /**
     * Creates and sets the view options for FullCalendar on this widget configuration.
     */
    private setViewOptions(): void {
        const views: Record<string, ViewOptions> = {
            month: {},
            week: {},
            day: {},
            dayGrid: {},
            timeGrid: {},
            list: {},
            dayGridMonth: {},
            dayGridWeek: {},
            dayGridDay: {},
            timeGridWeek: {},
            timeGridDay: {},
            listYear: {},
            listMonth: {},
            listDay: {}
        };

        const columnFormat = this.cfg.columnFormatOptions;
        if (columnFormat) {
            for (const view in views) {
                views[view] = { dayHeaderFormat: columnFormat[view] };
            }
        }

        // Using this.cfg.views was a bug, but fall back to that for backwards compatibility
        // Can be removed in the next major release
        this.cfg.options.views = this.cfg.options.views || this.cfg.views || {};
        $.extend(true, this.cfg.options.views, views);
    }

}
