/**
 * __PrimeFaces Schedule Widget__
 * 
 * Schedule provides an Outlook Calendar, iCal like JSF component to manage events.
 * 
 * @typedef PrimeFaces.widget.Schedule.ScheduleExtender Name of JavaScript function to extend the options of the
 * underlying FullCalendar plugin. Access the this schedule widget via the this context, and change the FullCalendar
 * configuration stored in `this.cfg`. See also {@link ScheduleCfg.extender}.
 * @this {PrimeFaces.widget.Schedule} PrimeFaces.widget.Schedule.ScheduleExtender 
 * 
 * @prop {import("@fullcalendar/core").Calendar} calendar The current full calendar instance.
 * @prop {JQuery} tip The DOM element for the tooltip.
 * @prop {number} tipTimeout The set-time   out timer ID for displaying a delayed tooltip.
 * @prop {JQuery} viewNameState The DOM element for the hidden input storing the current view state.
 * 
 * @interface {PrimeFaces.widget.ScheduleCfg} cfg The configuration for the {@link  Schedule| Schedule widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * 
 * @prop {PrimeFaces.widget.Schedule.ScheduleExtender} cfg.extender Name of JavaScript function to extend the options of
 * the underlying FullCalendar plugin. Access the this schedule widget via the this context, and change the FullCalendar
 * configuration stored in `this.cfg`.
 * @prop {import("@fullcalendar/core").CalendarOptions} cfg.calendarCfg The configuration object that is passed to the
 * FullCalendar upon initialization, see {@link CalendarOptions|CalendarOptions}.
 * @prop {string} cfg.formId Client ID of the form that is used for AJAX requests.
 * @prop {string} cfg.locale Locale code of the locale for the FullCalendar, such as `de` or `en`.
 * @prop {boolean} cfg.noOpener Whether for URL events access to the opener window from the target site should be
 * prevented (phishing protection), default value is `true`.
 * @prop {boolean} cfg.themeSystem Theme system used for rendering the calendar.
 * @prop {boolean} cfg.tooltip Whether a tooltip should be displayed on hover.
 * @prop {string} cfg.urlTarget Target for events with urls. Clicking on such events in the schedule will not trigger the
 * `selectEvent` but open the url using this target instead. Default is `_blank`.
 */
PrimeFaces.widget.Schedule = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.cfg.formId = this.jq.closest('form').attr('id');
        this.cfg.calendarCfg.themeSystem = 'standard';
        this.cfg.calendarCfg.locale = FullCalendar.globalLocales[this.cfg.locale || "en"];
        this.cfg.calendarCfg.slotLabelFormat = this.cfg.calendarCfg.slotLabelFormat || undefined; 
        this.cfg.calendarCfg.viewClassNames = this.onViewChange.bind(this);
        this.viewNameState = $(this.jqId + '_view');
        this.cfg.urlTarget = this.cfg.urlTarget || "_blank";
        this.cfg.calendarCfg.plugins = [
            FullCalendar.interactionPlugin, 
            FullCalendar.dayGridPlugin,
            FullCalendar.timeGridPlugin,
            FullCalendar.listPlugin,
            FullCalendar.momentPlugin,
            FullCalendar.momentTimezonePlugin
        ];

        this.setupEventSource();

        if(this.cfg.tooltip) {
            this.tip = $('<div class="ui-tooltip ui-widget ui-widget-content ui-shadow ui-corner-all"></div>').appendTo(document.body);
            this.addDestroyListener(function(){this.tip.remove();});
            this.addRefreshListener(function(){this.tip.remove();});
        }

        this.setupEventHandlers();

        if(this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.setViewOptions();

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        var _self = this;
        var calendarEl = document.getElementById(this.cfg.id);
        _self.calendar = new FullCalendar.Calendar(calendarEl, this.cfg.calendarCfg);
        _self.calendar.render();
    },

    /**
     * Creates and sets the event listeners for the full calendar.
     * @private
     */
    setupEventHandlers: function() {
        var $this = this;

        this.cfg.calendarCfg.dateClick = function(dateClickInfo) {
            var currentDate = PrimeFaces.toISOString(dateClickInfo.date);
            var ext = {
                params: [{
                    name: $this.id + '_selectedDate',
                    value: currentDate
                }]
            };

            if ($this.doubleClick === currentDate) {
                $this.doubleClick = null;
                if ($this.hasBehavior('dateDblSelect')) {
                    $this.callBehavior('dateDblSelect', ext);
                }
            } else {
                $this.doubleClick = currentDate;
                clearInterval($this.clickTimer);
                $this.clickTimer = setInterval(function() {
                    $this.doubleClick = null;
                    clearInterval($this.clickTimer);
                    $this.clickTimer = null;
                }, 500);

                if ($this.hasBehavior('dateSelect')) {
                    $this.callBehavior('dateSelect', ext);
                }
            }
        };
        
        this.cfg.calendarCfg.eventClick = function(eventClickInfo) {
            if (eventClickInfo.event.url) {
                var targetWindow = window.open('', $this.cfg.urlTarget);
                if ($this.cfg.noOpener) {
                    targetWindow.opener = null;    
                }
                targetWindow.location = eventClickInfo.event.url;
                return false;
            }

            if($this.hasBehavior('eventSelect')) {
                var ext = {
                    params: [
                        {name: $this.id + '_selectedEventId', value: eventClickInfo.event.id}
                    ]
                };

                $this.callBehavior('eventSelect', ext);
            }
        };

        this.cfg.calendarCfg.eventDrop = function(eventDropInfo) {
            if($this.hasBehavior('eventMove')) {
                var ext = {
                    params: [
                        {name: $this.id + '_movedEventId', value: eventDropInfo.event.id},
                        {name: $this.id + '_yearDelta', value: eventDropInfo.delta.years},
                        {name: $this.id + '_monthDelta', value: eventDropInfo.delta.months},
                        {name: $this.id + '_dayDelta', value: eventDropInfo.delta.days},
                        {name: $this.id + '_minuteDelta', value: (eventDropInfo.delta.milliseconds/60000)},
                        {name: $this.id + '_allDay', value: (eventDropInfo.event.allDay)}
                    ]
                };

                $this.callBehavior('eventMove', ext);
            }
        };

        this.cfg.calendarCfg.eventResize = function(eventResizeInfo) {
            if($this.hasBehavior('eventResize')) {
                var ext = {
                    params: [
                        {name: $this.id + '_resizedEventId', value: eventResizeInfo.event.id},
                        {name: $this.id + '_startDeltaYear', value: eventResizeInfo.startDelta.years},
                        {name: $this.id + '_startDeltaMonth', value: eventResizeInfo.startDelta.months},
                        {name: $this.id + '_startDeltaDay', value: eventResizeInfo.startDelta.days},
                        {name: $this.id + '_startDeltaMinute', value: (eventResizeInfo.startDelta.milliseconds/60000)},
                        {name: $this.id + '_endDeltaYear', value: eventResizeInfo.endDelta.years},
                        {name: $this.id + '_endDeltaMonth', value: eventResizeInfo.endDelta.months},
                        {name: $this.id + '_endDeltaDay', value: eventResizeInfo.endDelta.days},
                        {name: $this.id + '_endDeltaMinute', value: (eventResizeInfo.endDelta.milliseconds/60000)}
                    ]
                };

                $this.callBehavior('eventResize', ext);
            }
        };

        if(this.cfg.tooltip) {
            this.cfg.calendarCfg.eventMouseEnter = function(mouseEnterInfo) {
                if(mouseEnterInfo.event.extendedProps.description) {
                    $this.tipTimeout = setTimeout(function() {
                        $this.tip.css({
                            'left': mouseEnterInfo.jsEvent.pageX + 'px',
                            'top': (mouseEnterInfo.jsEvent.pageY + 15) + 'px',
                            'z-index': PrimeFaces.nextZindex()
                        });
                        $this.tip[0].innerHTML = mouseEnterInfo.event.extendedProps.description;
                        $this.tip.show();
                    }, 150);
                }
            };

            this.cfg.calendarCfg.eventMouseLeave = function(mouseLeaveInfo) {
                if($this.tipTimeout) {
                    clearTimeout($this.tipTimeout);
                }

                if($this.tip.is(':visible')) {
                    $this.tip.hide();
                    $this.tip.text('');
                }
            };
        } else {
            // PF #2795 default to regular tooltip
            this.cfg.calendarCfg.eventDidMount = function(info) {
                if(info.event.description) {
                    element.attr('title', info.event.description);
                }
            };
        }
    },

    /**
     * Creates the event listeners for the FullCalendar events.
     * @private
     */
    setupEventSource: function() {
        var $this = this;

        this.cfg.calendarCfg.events = function(fetchInfo, successCallback) {
            var options = {
                source: $this.id,
                process: $this.id,
                update: $this.id,
                formId: $this.cfg.formId,
                params: [
                    {name: $this.id + '_start', value: PrimeFaces.toISOString(fetchInfo.start)},
                    {name: $this.id + '_end', value: PrimeFaces.toISOString(fetchInfo.end)}
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            successCallback(JSON.parse(content).events);
                        }
                    });

                    return true;
                }
            };

            PrimeFaces.ajax.Request.handle(options);
        };
    },

    /**
     * Updates and refreshes the schedule view.
     */
    update: function() {
        var _self = this;
        _self.calendar.refetchEvents();
    },

    /**
     * The event listener for when the user switches the to a different view (month view, week day, or time view).
     * Updates the hidden input field with the current view name. Used for restoring the view after an AJAX update.
     * @param {import("@fullcalendar/common").ViewContentArg} arg Event data passed by FullCalendar when the view
     * changes.
     * @private
     */
    onViewChange: function(arg) {
        this.viewNameState.val(arg.view.type);
        this.callBehavior('viewChange');
    },

    /**
     * Creates and sets the view options for FullCalendar on this widget configuration.
     * @private
     */
    setViewOptions: function() {
        var views = {
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

        var columnFormat = this.cfg.columnFormatOptions;
        if(columnFormat) {
            for (var view in views) {
                views[view] = {dayHeaderFormat: columnFormat[view]};
            }
        }

        this.cfg.calendarCfg.views = this.cfg.views||{};
        $.extend(true, this.cfg.calendarCfg.views, views);
    }

});
