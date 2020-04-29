/**
 * __PrimeFaces Schedule Widget__
 * 
 * Schedule provides an Outlook Calendar, iCal like JSF component to manage events.
 * 
 * @typedef {import("@fullcalendar/core").OptionsInput} PrimeFaces.widget.Schedule.OptionsInput Type alias for the
 * {@link "@fullcalendar/core/types/input-types".OptionsInput|OptionsInput} interface from FullCalendar, required for
 * technical reasons.
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
 * @extends {PrimeFaces.widget.Schedule.OptionsInput} cfg
 * 
 * @prop {PrimeFaces.widget.Schedule.ScheduleExtender} cfg.extender Name of JavaScript function to extend the options of
 * the underlying FullCalendar plugin. Access the this schedule widget via the this context, and change the FullCalendar
 * configuration stored in `this.cfg`.
 * @prop {string} cfg.formId Client ID of the form that is used for AJAX requests.
 * @prop {boolean} cfg.noOpener Whether for URL events access to the opener window from the target site should be
 * prevented (phishing protection), default value is `true`.
 * @prop {boolean} cfg.theme Whether theming is enabled.
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
        this.cfg.theme = true;
        this.viewNameState = $(this.jqId + '_view');
        this.cfg.urlTarget = this.cfg.urlTarget || "_blank";
        this.cfg.plugins = [ 'interaction', 'dayGrid', 'timeGrid', 'list', 'moment', 'momentTimezone'];

        this.setupEventSource();

        if(this.cfg.tooltip) {
            this.tip = $('<div class="ui-tooltip ui-widget ui-widget-content ui-shadow ui-corner-all"></div>').appendTo(this.jq);
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
        _self.calendar = new FullCalendar.Calendar(calendarEl, this.cfg);
        _self.calendar.render();

        this.bindViewChangeListener();
    },

    /**
     * Creates and sets the event listeners for the full calendar.
     * @private
     */
    setupEventHandlers: function() {
        var $this = this;

        this.cfg.dateClick = function(dateClickInfo) {
            if($this.hasBehavior('dateSelect')) {
                var ext = {
                    params: [
                        {name: $this.id + '_selectedDate', value: dateClickInfo.date.toISOString()}
                    ]
                };

                $this.callBehavior('dateSelect', ext);
            }
        };

        this.cfg.eventClick = function(eventClickInfo) {
            if (eventClickInfo.event.url) {
                var targetWindow = window.open('', $this.cfg.urlTarget);
                if ($this.cfg.noOpener) {
                    targetWindow.opener = null;    
                }
                targetWindow.location = targetWindow.event.url;
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

        this.cfg.eventDrop = function(eventDropInfo) {
            if($this.hasBehavior('eventMove')) {
                var ext = {
                    params: [
                        {name: $this.id + '_movedEventId', value: eventDropInfo.event.id},
                        {name: $this.id + '_yearDelta', value: eventDropInfo.delta.years},
                        {name: $this.id + '_monthDelta', value: eventDropInfo.delta.months},
                        {name: $this.id + '_dayDelta', value: eventDropInfo.delta.days},
                        {name: $this.id + '_minuteDelta', value: (eventDropInfo.delta.milliseconds/60000)}
                    ]
                };

                $this.callBehavior('eventMove', ext);
            }
        };

        this.cfg.eventResize = function(eventResizeInfo) {
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
            this.cfg.eventMouseEnter = function(mouseEnterInfo) {
                if(mouseEnterInfo.event.extendedProps.description) {
                    $this.tipTimeout = setTimeout(function() {
                        $this.tip.css({
                            'left': mouseEnterInfo.jsEvent.pageX,
                            'top': mouseEnterInfo.jsEvent.pageY + 15,
                            'z-index': ++PrimeFaces.zindex
                        });
                        $this.tip[0].innerHTML = mouseEnterInfo.event.extendedProps.description;
                        $this.tip.show();
                    }, 150);
                }
            };

            this.cfg.eventMouseLeave = function(mouseLeaveInfo) {
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
            this.cfg.eventRender = function(info) {
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

        this.cfg.events = function(fetchInfo, successCallback) {
            var options = {
                source: $this.id,
                process: $this.id,
                update: $this.id,
                formId: $this.cfg.formId,
                params: [
                    {name: $this.id + '_start', value: fetchInfo.start.toISOString()},
                    {name: $this.id + '_end', value: fetchInfo.end.toISOString()}
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
     * Sets up the event listeners for the view buttons.
     * @private
     */
    bindViewChangeListener: function() {
        var excludedClasses = '.fc-prev-button,.fc-next-button,.fc-prevYear-button,.fc-nextYear-button,.fc-today-button';
        var viewButtons = this.jq.find('> .fc-toolbar button:not(' + excludedClasses + ')'),
            $this = this;

        viewButtons.each(function(i) {
            var viewButton = viewButtons.eq(i),
                buttonClasses = viewButton.attr('class').split(' ');
            for(var i = 0; i < buttonClasses.length; i++) {
                var buttonClassParts = buttonClasses[i].split('-');
                if(buttonClassParts.length === 3) {
                    viewButton.data('view', buttonClassParts[1]);
                    break;
                }
            }
        });

        viewButtons.on('click.schedule', function() {
            var viewName = $(this).data('view');

            $this.viewNameState.val(viewName);

            $this.callBehavior('viewChange');
        });
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
                views[view] = {columnHeaderFormat: columnFormat[view]};
            }
        }

        this.cfg.views = this.cfg.views||{};
        $.extend(true, this.cfg.views, views);
    }

});
